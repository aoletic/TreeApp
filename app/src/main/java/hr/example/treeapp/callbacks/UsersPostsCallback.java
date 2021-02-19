package hr.example.treeapp.callbacks;

import com.example.core.entities.Post;
import com.example.core.entities.User;

import java.util.List;

public interface UsersPostsCallback {
    void onCallback(List<Post> usersPostsList);
}
