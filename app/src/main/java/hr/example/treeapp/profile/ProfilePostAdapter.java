package hr.example.treeapp.profile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.core.entities.Post;

import java.util.ArrayList;
import java.util.List;

import hr.example.treeapp.repositories.GetPostData;

public class ProfilePostAdapter extends BaseAdapter {
    private Context mContext;
    GetPostData getPostData;
    // Constructor
    List<Post> usersPostsList=new ArrayList<>();
    int imageWidth;
    public ProfilePostAdapter(Context c, List<Post> usersPostsList, int imageWidth) {
        mContext = c;
        this.usersPostsList=usersPostsList;
        this.imageWidth=imageWidth;
    }

    public int getCount() {
        return usersPostsList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(usersPostsList.get(position).getSlika());
        return imageView;
    }

}
