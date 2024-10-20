package com.example.myapplication.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.home.MainActivity;
import com.example.myapplication.databinding.ActivitySignInBinding;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.example.myapplication.utilities.ImageUtil; // Import lớp ImageUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Kiểm tra nếu người dùng đã đăng nhập
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

        configureGoogleSignIn();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
        binding.googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        binding.buttonResetPassword.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class))
        );
    }

    private void signIn() {
        loading(true);
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            fetchUserDataFromFirestore(user.getUid());
                        } else {
                            loading(false);
                            showToast("Authentication failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void fetchUserDataFromFirestore(String userId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        loading(false);
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            if (!documentSnapshot.contains(Constants.KEY_FRIEND_REQUEST_TIMESTAMPS)) {
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(userId)
                                        .update(Constants.KEY_FRIEND_REQUEST_TIMESTAMPS, new HashMap<String, com.google.firebase.Timestamp>());
                            }
                            if (!documentSnapshot.contains(Constants.KEY_FRIENDS)) {
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(userId)
                                        .update(Constants.KEY_FRIENDS, new ArrayList<String>());
                            }
                            if (!documentSnapshot.contains(Constants.KEY_FRIEND_REQUESTS)) {
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(userId)
                                        .update(Constants.KEY_FRIEND_REQUESTS, new ArrayList<String>());
                            }
                            if (!documentSnapshot.contains(Constants.KEY_SENT_FRIEND_REQUESTS)) {
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(userId)
                                        .update(Constants.KEY_SENT_FRIEND_REQUESTS, new ArrayList<String>());
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Unable to fetch user data");
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    showToast("Đặng nhập bằng Google thành công");
                }
            } catch (ApiException e) {
                showToast("Đăng nhập bằng Google thất bại do: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            saveUserInfo(user,user.getUid());
                        } else {
                            showToast("Ủy quyền thất bại.");
                        }
                    }
                });
    }

    private void saveUserInfo(FirebaseUser user, String userId) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Người dùng đã tồn tại, cập nhật thông tin
                                updateUserInfo(document, userId);
                            } else {
                                // Người dùng chưa tồn tại, tạo mới
                                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            showToast("Lỗi khi truy xuất thông tin người dùng: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateUserInfo(DocumentSnapshot document, String userId) {
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putString(Constants.KEY_USER_ID, document.getId());
        preferenceManager.putString(Constants.KEY_NAME, document.getString(Constants.KEY_NAME));
        preferenceManager.putString(Constants.KEY_IMAGE, document.getString(Constants.KEY_IMAGE));
        preferenceManager.putString(Constants.KEY_EMAIL, document.getString(Constants.KEY_EMAIL));

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (!document.contains(Constants.KEY_FRIEND_REQUEST_TIMESTAMPS)) {
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .update(Constants.KEY_FRIEND_REQUEST_TIMESTAMPS, new HashMap<String, com.google.firebase.Timestamp>());
        }

        if (!document.contains(Constants.KEY_FRIENDS)) {
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .update(Constants.KEY_FRIENDS, new ArrayList<String>());
        }
        if (!document.contains(Constants.KEY_FRIEND_REQUESTS)) {
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .update(Constants.KEY_FRIEND_REQUESTS, new ArrayList<String>());
        }
        if (!document.contains(Constants.KEY_SENT_FRIEND_REQUESTS)) {
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .update(Constants.KEY_SENT_FRIEND_REQUESTS, new ArrayList<String>());
        }

        navigateToMainActivity();
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Nhập email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Hãy nhập email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Nhập mật khẩu");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
            googleSignInClient = null;
        }
    }
}
