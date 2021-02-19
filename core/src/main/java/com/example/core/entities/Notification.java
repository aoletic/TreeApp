package com.example.core.entities;

import java.util.Date;

public class Notification {
    public String notificationId;
    public String receiverId;
    public String senderId;
    public String postId;
    public NotificationType type;
    public Date timestamp;

    public Notification(String notificationId, String receiverId, String senderId, String postId, NotificationType type, Date timestamp) {
        this.notificationId = notificationId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.postId = postId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
