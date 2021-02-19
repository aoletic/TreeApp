package hr.example.treeapp.postView;

import android.content.Context;
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
import hr.example.treeapp.callbacks.UserCallback;
import hr.example.treeapp.entities.UserImage;
import hr.example.treeapp.repositories.UserRepository;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder> {


    private final Context mContext;
    private final List<String> mData;

    public ReactionAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_reactions, parent, false);
        return new ReactionViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        String userId = mData.get(position);
        UserRepository userRepository = new UserRepository();
        if(!userId.equals("Anonymous")) {
            userRepository.getUser(userId, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    userRepository.getUserImage(userId, new ProfileImageCallback() {
                        @Override
                        public void onCallbackList(UserImage userImage) {
                            holder.username.setText(user.korisnickoIme);
                            if (userImage.image != null && userImage.url == null)
                                Glide.with(mContext).load(userImage.image).into(holder.userImage);
                            if (userImage.url != null && userImage.image == null) {
                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round);
                                Glide.with(mContext).load(userImage.url).apply(options).into(holder.userImage);
                            }

                        }
                    });
                }
            });
        }
        else{
            holder.username.setText(R.string.Anonymous);
            Glide.with(mContext).load(R.drawable.default_image).into(holder.userImage);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ReactionViewHolder extends  RecyclerView.ViewHolder {

        ImageView userImage;
        TextView username;

        public ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.profile_image_reaction);
            username = itemView.findViewById(R.id.reactionUsername);
        }
    }
}
