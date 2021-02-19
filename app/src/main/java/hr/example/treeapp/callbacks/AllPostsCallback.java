package hr.example.treeapp.callbacks;

import com.example.core.entities.Post;

import java.util.List;

public interface AllPostsCallback {
    void onCallback(List<Post> postList);
}
