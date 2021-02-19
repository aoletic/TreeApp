package hr.example.treeapp.addTree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import hr.example.treeapp.R;

public class CroppActivity extends AppCompatActivity {

    private ImageView img;
    private final int CODE_IMG_GALERY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropp);
        
        init();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                .setType("image/*"),CODE_IMG_GALERY);
            }
        });
    }

    private void init() {
        this.img=findViewById(R.id.imageView);
    }
}