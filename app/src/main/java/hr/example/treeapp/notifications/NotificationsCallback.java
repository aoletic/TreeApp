package hr.example.treeapp.notifications;

import com.example.core.entities.Notification;

import java.util.List;

public interface NotificationsCallback {
    void onCallback(List<Notification> notificationList);
}
