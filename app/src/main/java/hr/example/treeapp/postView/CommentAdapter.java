package hr.example.treeapp.postView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.core.entities.Comment;
import com.example.core.entities.User;

import java.util.List;

import hr.example.treeapp.callbacks.CurrentUserRoleCallback;
import hr.example.treeapp.repositories.GetPostData;
import hr.example.treeapp.callbacks.ProfileImageCallback;
import hr.example.treeapp.R;
import hr.example.treeapp.callbacks.UserCallback;
import hr.example.treeapp.entities.UserImage;
import hr.example.treeapp.repositories.UserRepository;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;

    public List<Comment> mData;

    private GetPostData getPostData;
    public static String postID;


    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
        getPostData=new GetPostData();
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String userId = mData.get(position).getKorisnik_ID();
        UserRepository userRepository = new UserRepository();


        userRepository.getUser(userId, new UserCallback() {
            @Override
            public void onCallback(User user) {
                userRepository.getUserImage(userId, new ProfileImageCallback() {
                    @Override
                    public void onCallbackList(UserImage userImage) {
                        holder.username.setText(user.korisnickoIme);
                        holder.commentContent.setText(mData.get(position).getTekst().trim());
                        holder.date.setText("");
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
        });

        //holder.commentContent.setText(mData.get(position).getTekst());
        holder.date.setText(mData.get(position).getDatum());
        holder.commentPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupComment = new PopupMenu(mContext, view);
                MenuInflater inflater = popupComment.getMenuInflater();
                inflater.inflate(R.menu.commentpopupmenu, popupComment.getMenu());
                getPostData.getCurrentUserRole(new CurrentUserRoleCallback() {
                    @Override
                    public void onCallback(int userRole) {
                        if(userRole!=1  && !getPostData.getCurrentUserID().equals(userId)) {
                            popupComment.getMenu().findItem(R.id.commentpopupdelete).setVisible(false);


                        }
                        else{
                            popupComment.getMenu().findItem(R.id.commentpopupdelete).setVisible(true);
                        }
                    }
                });
                popupComment.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.commentpopupdelete:
                                getPostData.deleteComment( postID, mData.get(position).getKomentar_ID());
                                notifyItemRangeChanged(position, mData.size());
                                SinglePostViewActivity singlePostViewActivity= new SinglePostViewActivity();
                                singlePostViewActivity.getPostForRemovingCommentsData(postID);
                                mData.remove(position);
                                notifyItemRemoved(position);
                                break;
                        }
                        return true;
                    }
                });
                popupComment.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends  RecyclerView.ViewHolder {

        ImageView userImage;
        TextView username, commentContent, date;
        ImageButton commentPopup;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.commentUsername);
            commentContent= itemView.findViewById(R.id.commentText);
            date = itemView.findViewById(R.id.commentDate);
            commentPopup=itemView.findViewById(R.id.imageButtonComment);

        }
    }
}
