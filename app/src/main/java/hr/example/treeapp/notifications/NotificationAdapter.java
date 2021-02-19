package hr.example.treeapp.notifications;

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
import com.example.core.entities.Notification;
import com.example.core.entities.NotificationType;
import com.example.core.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hr.example.treeapp.callbacks.ProfileImageCallback;
import hr.example.treeapp.R;
import hr.example.treeapp.callbacks.UserCallback;
import hr.example.treeapp.entities.UserImage;
import hr.example.treeapp.repositories.UserRepository;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private Context mContext;
    private List<Notification> mData;
    private UserRepository userRepository = new UserRepository();
    private OnItemClicked onClickListener;

    public NotificationAdapter(Context mContext, List<Notification> mData, OnItemClicked onItemClicked) {
        this.mContext=mContext;
        this.mData=mData;
        this.onClickListener = onItemClicked;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_notification, parent, false);
        return new NotificationViewHolder(row, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        String postId = mData.get(position).getPostId();
        String senderId = mData.get(position).getSenderId();
        Date timestamp =mData.get(position).getTimestamp();
        NotificationType type = mData.get(position).getType();

        if(type.equals(NotificationType.comment)){
            Glide.with(mContext).load(R.drawable.comment_notification).into(holder.notificationIcon);
            holder.notificationText.setText(R.string.comment_notification);
        }
        if(type.equals(NotificationType.leaf)){
            Glide.with(mContext).load(R.drawable.leaf_notification).into(holder.notificationIcon);
            holder.notificationText.setText(R.string.leaf_notification);
        }

        if(checkIfTimestampWasToday(timestamp))
            holder.timestamp.setText(timestamp.getHours()+":"+timestamp.getMinutes());
        else
            holder.timestamp.setText(timestamp.getDate()+"."+(timestamp.getMonth()+1)+"."+(timestamp.getYear()+1900)+".");

        userRepository.getUser(senderId, new UserCallback() {
            @Override
            public void onCallback(User user) {
                if(user==null){
                    holder.username.setText("Anonymous");
                    Glide.with(mContext).load(R.drawable.default_image).into(holder.userImage);
                }
                else{
                    holder.username.setText(user.korisnickoIme);
                    userRepository.getUserImage(user.uid, new ProfileImageCallback() {
                        @Override
                        public void onCallbackList(UserImage userImage) {
                            if(userImage.image!=null && userImage.url==null)
                                Glide.with(mContext).load(userImage.image).into(holder.userImage);
                            if(userImage.url!=null && userImage.image==null){
                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round);
                                Glide.with(mContext).load(userImage.url).apply(options).into(holder.userImage);
                            }
                        }
                    });
                }

            }
        });

        holder.notificationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(position);
            }
        });

        holder.timestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(position);
            }
        });
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(position);
            }
        });
    }

    private boolean checkIfTimestampWasToday(Date timestamp) {
       Date today = Calendar.getInstance().getTime();
        return timestamp.getDate() == today.getDate() && timestamp.getMonth() == today.getMonth() && timestamp.getYear() == today.getYear();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        ImageView userImage, notificationIcon;
        TextView username, notificationText, timestamp;
        OnItemClicked onItemClicked;
        public NotificationViewHolder(@NonNull View itemView, OnItemClicked onItemClicked) {
            super(itemView);
            this.onItemClicked=onItemClicked;
            userImage = itemView.findViewById(R.id.profile_image_notification);
            notificationIcon = itemView.findViewById(R.id.icon_notification);
            username=itemView.findViewById(R.id.username_notification);
            notificationText = itemView.findViewById(R.id.content_notification);
            timestamp = itemView.findViewById(R.id.timestamp_notification);
        }
    }
}
