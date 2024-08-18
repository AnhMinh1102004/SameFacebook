import com.google.firebase.Timestamp;
import java.util.List;

public class Post {
    private String id;
    private String userId;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private Timestamp timestamp;
    private int likes;
    private List<String> commentIds;

    // Constructor mặc định cần thiết cho Firestore
    public Post() {}

    public Post(String id, String userId, String content, String imageUrl, String videoUrl, Timestamp timestamp, int likes, List<String> commentIds) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.timestamp = timestamp;
        this.likes = likes;
        this.commentIds = commentIds;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public Timestamp getTimestamp() { return timestamp; }
    public int getLikes() { return likes; }
    public List<String> getCommentIds() { return commentIds; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setCommentIds(List<String> commentIds) { this.commentIds = commentIds; }

    // Phương thức tiện ích
    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public void addCommentId(String commentId) {
        this.commentIds.add(commentId);
    }

    public void removeCommentId(String commentId) {
        this.commentIds.remove(commentId);
    }
}
