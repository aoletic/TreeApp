package com.example.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.core.entities.User;
import com.example.core.entities.Post;


public class PostRecyclerAdapter extends RecyclerView.Adapter<PostViewHolder> {
    public ArrayList<PostItem> postItemList;
    public ArrayList<UserItem> userItemList;
    private Context context;


    private OnItemClicked onClickListener;

    public PostRecyclerAdapter(@NonNull List<PostItem> postItemList, List<UserItem> userItemList, Context context, OnItemClicked onItemClicked) {
        this.postItemList = (ArrayList<PostItem>) postItemList;
        this.userItemList = (ArrayList<UserItem>) userItemList;
        this.context = context;
        this.onClickListener =onItemClicked;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.post_list_item, parent, false);
        return new PostViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        final Post post = postItemList.get(position);
        User user = new User();

        for(User u : userItemList){
            if(u.uid.equals(post.getKorisnik_ID()))
                user = u;
        }
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(position);
            }
        });
        holder.bindToData(post, user);
    }

    @Override
    public int getItemCount() {
        return postItemList == null? 0: postItemList.size();
    }

    /**
     * https://www.youtube.com/watch?v=69C1ljfDvl0&ab_channel=CodingWithMitch
     */
    public interface OnItemClicked {
        void onItemClick(int position);
    }

}