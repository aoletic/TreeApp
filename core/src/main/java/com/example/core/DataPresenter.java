package com.example.core;

import android.content.Context;

import com.example.core.entities.Post;
import com.example.core.entities.User;

import java.util.List;

import androidx.fragment.app.Fragment;

public interface DataPresenter {
    void setData(List<Post> posts, List<User> users, boolean isDataNew);

    String getModuleName(Context context);
    Fragment getFragment();
}
