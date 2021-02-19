package hr.example.treeapp.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.core.entities.Notification;

import java.util.ArrayList;
import java.util.List;

import hr.example.treeapp.callbacks.DeleteDoneCallback;
import hr.example.treeapp.R;
import hr.example.treeapp.postView.SinglePostViewActivity;
import hr.example.treeapp.repositories.UserRepository;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnItemClicked {
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private UserRepository userRepository = new UserRepository();
    private RelativeLayout noNotifications;
    private List<Notification> notifications = new ArrayList<Notification>();
    private Button refreshButton;
    private ImageButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        noNotifications = findViewById(R.id.noNotificationsRelativeLayout);
        refreshButton = findViewById(R.id.refreshButton);
        clearButton = findViewById(R.id.clearNotificationsButton);
        //createMockData();
        fetchNotificatons();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNotificatons();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNotifications();
            }
        });
    }

    private void setNotificationsAdapter(List<Notification> notificationsList) {
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter= new NotificationAdapter(getApplicationContext(), notificationsList,this::onItemClick);
        notificationRecyclerView.setAdapter(notificationAdapter);
    }

    private void fetchNotificatons() {
        userRepository.getCurrentUserNotifications(new NotificationsCallback() {
            @Override
            public void onCallback(List<Notification> notificationList) {
                if(notificationList!=null){
                    setNotificationsAdapter(notificationList);
                    notifications=notificationList;
                    noNotifications.setVisibility(View.GONE);
                    notificationRecyclerView.setVisibility(View.VISIBLE);
                    clearButton.setVisibility(View.VISIBLE);
                }
                else{
                    notifications=null;
                    noNotifications.setVisibility(View.VISIBLE);
                    notificationRecyclerView.setVisibility(View.GONE);
                    clearButton.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        String postId = notifications.get(position).getPostId();
        Intent singlePostView = new Intent(NotificationsActivity.this, SinglePostViewActivity.class);
        singlePostView.putExtra("postId",postId);
        startActivity(singlePostView);
    }
    private void clearNotifications(){
        userRepository.clearCurrentUserNotifications(new DeleteDoneCallback() {
            @Override
            public void onCallbackList(boolean deleteDone) {
                if(deleteDone)
                    fetchNotificatons();
            }
        });
    }
}