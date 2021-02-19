package hr.example.treeapp.callbacks;

import com.example.core.entities.User;

import java.util.List;

public interface AllUsersCallback {
    void onCallback(List<User> userList);
}
