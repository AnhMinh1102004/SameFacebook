package com.example.myapplication.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String id;
    public String name;
    public String email;
    public String image;
    public String token;
    public List<String> friends;
    public List<String> posts;

    public User() {
        // Default constructor required for Firestore
        friends = new ArrayList<>();
        posts = new ArrayList<>();
    }

    public User(String id, String name, String email, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.friends = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    public void addFriend(String friendId) {
        if (!friends.contains(friendId)) {
            friends.add(friendId);
        }
    }

    public void removeFriend(String friendId) {
        friends.remove(friendId);
    }

    public void addPost(String postId) {
        if (!posts.contains(postId)) {
            posts.add(postId);
        }
    }

    public void removePost(String postId) {
        posts.remove(postId);
    }
}
