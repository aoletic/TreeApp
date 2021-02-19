package hr.example.treeapp.userSearch;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.core.entities.User;

import java.util.List;

import hr.example.treeapp.callbacks.ProfileImageCallback;
import hr.example.treeapp.R;
import hr.example.treeapp.entities.UserImage;
import hr.example.treeapp.repositories.UserRepository;

public class UserSearchRecyclerAdapter extends RecyclerView.Adapter<UserSearchRecyclerAdapter.MyViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    Context mContext;
    List<User> users;
    OnItemClickListener listener;
    public UserSearchRecyclerAdapter(Context mContext, List<User> users, OnItemClickListener listener) {
        this.mContext = mContext;
        this.users = users;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v= LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent, false);
        MyViewHolder vHolder=new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(users.get(position), listener);//dod
        String userId = users.get(position).getUid();
        UserRepository userRepository = new UserRepository();
        userRepository.getUserImage(userId, new ProfileImageCallback() {
            @Override
            public void onCallbackList(UserImage userImage) {
                if(userImage.image!=null && userImage.url==null) {
                    Glide.with(mContext).load(userImage.image).into(holder.profile_picture);
                }
                if(userImage.url!=null && userImage.image==null){
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher_round)
                            .error(R.mipmap.ic_launcher_round);
                    Glide.with(mContext).load(userImage.url).apply(options).into(holder.profile_picture);
                }

            }

        });
        Resources res = mContext.getResources();
        holder.username.setText(users.get(position).getKorisnickoIme());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView profile_picture;
        private TextView username;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            profile_picture=(ImageView)itemView.findViewById(R.id.imageViewUserProfilePost);
            username=(TextView)itemView.findViewById(R.id.textViewUserName);


        }

        public void bind(User user, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }
    }
}
