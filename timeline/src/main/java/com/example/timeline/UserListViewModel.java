package com.example.timeline;

import com.example.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserListViewModel {
    public static List<UserItem> convertToUserItemList(List<User> users) {
        List<UserItem> userItems = new ArrayList<>();
        for (User u : users) {
            userItems.add(new UserItem(u));
        }
        return userItems;
    }
}
