package hr.example.treeapp.callbacks;

import com.example.core.entities.Post;

import java.util.List;

public interface GetPostsFromLastID {
    void onCallback(List<Post> postList);
}
