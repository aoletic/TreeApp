package hr.example.treeapp.leaderboard;

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

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.MyViewHolder> {

    Context mContext;
    List<User> users;
    String string;



    public LeaderboardRecyclerAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v= LayoutInflater.from(mContext).inflate(R.layout.leaderboard_item, parent, false);
        MyViewHolder vHolder=new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String userId = users.get(position).getUid();
        holder.ranking_number.setText(position+1+".");
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
        string=res.getString(R.string.leaderboard_item_points_text);

        holder.username.setText(users.get(position).getKorisnickoIme());
        holder.points.setText((Integer.toString((int) users.get(position).getBodovi()))+string);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView ranking_number;
        private ImageView profile_picture;
        private TextView username;
        private TextView points;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            ranking_number=(TextView)itemView.findViewById(R.id.ranking_number);
            profile_picture=(ImageView)itemView.findViewById(R.id.profile_image_leaderboard);
            username=(TextView)itemView.findViewById(R.id.username_leaderboard);
            points=(TextView)itemView.findViewById(R.id.points);


        }
    }
}
