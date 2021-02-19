package com.example.timeline;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.core.entities.User;
import com.example.core.entities.Post;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {
    ImageView postImage;
    ImageView profileImage;
    TextView username;
    TextView postDescription;
    TextView leafNumberTextView;
    private View itemView;
    PostRecyclerAdapter.OnItemClicked onItemClicked;

    public PostViewHolder(@NonNull View itemView, PostRecyclerAdapter.OnItemClicked onItemClicked) {
        super(itemView);
        postImage = itemView.findViewById(R.id.post_image);
        profileImage = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.username);
        postDescription = itemView.findViewById(R.id.post_description);
        leafNumberTextView = itemView.findViewById(R.id.leafNumberTextView);
        this.onItemClicked=onItemClicked;
        this.itemView = itemView;
    }

    public void bindToData(Post post, User user){
        username.setText(user.korisnickoIme);
        postDescription.setText(post.getOpis());
        leafNumberTextView.setText(String.valueOf(post.getBroj_lajkova()));

        if(post.getURL_slike().contains("https://")) {
            Picasso.with(itemView.getContext())
                    .load(post.getURL_slike())
                    .into(postImage);
            postImage.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            Glide.with(itemView.getContext())
                    .load(post.getSlika())
                    .into(postImage);
        }

        if(user.getProfilnaSlika().contains("https://")) {
            Picasso.with(itemView.getContext())
                    .load(user.getProfilnaSlika())
                    .into(profileImage);
        } else {
            Glide.with(itemView.getContext())
                    .load(user.getSlika())
                    .into(profileImage);
        }
    }

}
