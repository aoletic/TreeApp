package com.example.timeline;

import com.example.core.entities.Post;

import java.util.ArrayList;
import java.util.List;

public class PostListViewModel {
    public static List<PostItem> convertToPostItemList(List<Post> posts) {
        List<PostItem> postItems = new ArrayList<>();
        for (Post p : posts) {
            postItems.add(new PostItem(p));
        }
        return postItems;
    }
}
