package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivitySignInBinding;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Kiểm tra nếu người dùng đã đăng nhập
        if (preferenceManager.getBoolean(Constants.Key_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
binding.buttonSignIn.setOnClickListener(v -> {
    if (isValidSignInDetails()){
        signIn();
    }
});
    }
    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.Key_COLLECTION_USER)
                .whereEqualTo(Constants.Key_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.Key_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.Key_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.Key_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.Key_NAME, documentSnapshot.getString(Constants.Key_NAME));
                        preferenceManager.putString(Constants.Key_IMAGE, documentSnapshot.getString(Constants.Key_IMAGE));
                        preferenceManager.putString(Constants.Key_EMAIL, documentSnapshot.getString(Constants.Key_EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();  // Ensure that SignInActivity finishes so the user can't go back to it
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }

private void loading(Boolean isloading){
        if (isloading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);

        }
}
private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
}
private Boolean isValidSignInDetails(){
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
        showToast("Enter email");
        return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
         showToast("enter valid email");
         return false;
        }
        else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        }else {
            return true;
        }
}

}