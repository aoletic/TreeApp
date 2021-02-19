package hr.example.treeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.Observer;

import hr.example.treeapp.addTree.AddTreeActivity;
import hr.example.treeapp.callbacks.UserAnonymousCallback;
import hr.example.treeapp.leaderboard.LeaderboardFragment;
import hr.example.treeapp.leaderboard.LeaderboardLocationMapview;
import hr.example.treeapp.postView.SinglePostViewActivity;
import hr.example.treeapp.profile.UserProfileFragment;
import hr.example.treeapp.repositories.GetPostData;
import hr.example.treeapp.repositories.UserRepository;
import hr.example.treeapp.userSearch.UserSearchFragment;
import hr.example.treeapp.managers.DataManager;
import hr.example.treeapp.managers.DataPresentersManager;

import com.example.core.LiveData.LiveData;
import com.example.core.VisibleMapRange;
import com.example.core.entities.Post;
import com.example.core.entities.User;
import com.example.timeline.PostListFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    public DataPresentersManager dataPresentersManager;
    Context context;
    private LiveData model;
    private static final int MY_REQUEST_CODE = 0xe111;
    public static ImageButton chooseLocationButton;
    public static int current=0;
    int currentModule=0;
    GetPostData getPostData;
    public static HorizontalScrollView horizontalScrollView;
    public static LinearLayout myLayout;
    public static BottomNavigationView bottomNav;
    private boolean currentUserIsGuest=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);
        context=this;
        chooseLocationButton = findViewById(R.id.chooseLocationTimeline);
        showHideChooseLocationButtonTimeline("");
        DataManager dataManager = DataManager.getInstance();
        getPostData = new GetPostData();

        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(String lastPostID) {
                if (lastPostID == "Timeline send new data") {
                    dataPresentersManager.refreshDataTimeline();
                } else {
                    dataManager.GetPostsFromLastID();
                }
            }
        };
        model.lastPostID().observe(this, nameObserver);

        horizontalScrollView =findViewById(R.id.topmenu);
        myLayout = (LinearLayout) findViewById(R.id.topmenumainlayout);

        final Observer<String> postIdObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null){
                    Intent singlePostView = new Intent(MainActivity.this, SinglePostViewActivity.class);
                    singlePostView.putExtra("postId",s);
                    startActivity(singlePostView);
                }
            }
        };
        model.selectedPostId().observe(this, postIdObserver);

        final Observer<VisibleMapRange> visibleMapRangeObserver = new Observer<VisibleMapRange>() {
            @Override
            public void onChanged(VisibleMapRange mapMinMaxLatLng) {
                dataManager.getPostsInLatLng(mapMinMaxLatLng.minLatitude, mapMinMaxLatLng.maxLatitude, mapMinMaxLatLng.minLongitude, mapMinMaxLatLng.maxLongitude);
            }
        };
        model.visibleMapRange().observe(this, visibleMapRangeObserver);


        dataPresentersManager=new DataPresentersManager(context);
        if(dataPresentersManager.firstPresenter!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dataPresentersManager.firstPresenter.getFragment()).commit();
            FillTopMenu();
            String moduleName = dataPresentersManager.firstPresenter.getModuleName(context);
            showHideChooseLocationButtonTimeline(moduleName);
        }

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setItemIconTintList(null);

        UserRepository userRepository = new UserRepository();
        userRepository.isCurrentUserAnonymous(new UserAnonymousCallback() {
            @Override
            public void onCallback(boolean isAnonymous) {
                currentUserIsGuest=isAnonymous;
                if(isAnonymous)
                    bottomNav.getMenu().findItem(R.id.nav_addtree).setVisible(false);
            }
        });
        //displayMainFragment();
    }

    private void FillTopMenu() {
        HorizontalScrollView horizontalScrollView = findViewById(R.id.topmenu);
        LinearLayout mainLinearLayout =findViewById(R.id.topmenumainlayout);
        for (int i=0;i<dataPresentersManager.presenters.size() ;i++){
            LinearLayout newLayout = new LinearLayout(this);
            mainLinearLayout.addView(newLayout);
            Button button=new Button(this);
            button.setText(dataPresentersManager.presenters.get(i).getModuleName(this));
            button.setBackgroundColor(getResources().getColor(R.color.baby_green));
            Typeface typeface = ResourcesCompat.getFont(this,R.font.roboto);
            button.setTextColor(getResources().getColor(R.color.tree_green));
            button.setTypeface(typeface);
            button.setPadding(32,0,0,0);



            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentModule != finalI + 1) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dataPresentersManager.presenters.get(finalI).getFragment()).commit();
                        dataPresentersManager.loadFragment(finalI);
                        String moduleName = dataPresentersManager.presenters.get(finalI).getModuleName(context);
                        showHideChooseLocationButtonTimeline(moduleName);
                        currentModule = finalI + 1;
                    }
                }

            });
            newLayout.addView(button);

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment =null;
                    switch(item.getItemId()){
                        case R.id.nav_home:
                            if(current!=1) {
                                if (dataPresentersManager.firstPresenter != null) {
                                    selectedFragment = dataPresentersManager.firstPresenter.getFragment();
                                    horizontalScrollView.setVisibility(HorizontalScrollView.VISIBLE);
                                    myLayout.setVisibility(LinearLayout.VISIBLE);
                                    String moduleName = dataPresentersManager.firstPresenter.getModuleName(context);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                                    dataPresentersManager.loadFragment(0);
                                    showHideChooseLocationButtonTimeline(moduleName);
                                    current=1;
                                }
                            }

                            break;
                        case R.id.nav_leaderboard:
                            if(current!=2) {
                                selectedFragment = new LeaderboardFragment();
                                horizontalScrollView.setVisibility(HorizontalScrollView.GONE);
                                myLayout.setVisibility(LinearLayout.GONE);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                                current=2;
                            }
                            break;
                        case R.id.nav_addtree:
                                Intent open = new Intent(MainActivity.this, AddTreeActivity.class);
                                startActivity(open);
                                break;
                        case R.id.nav_search:
                            if(current!=4) {
                                //Intent newi = new Intent(LoginTest.this, NotificationsActivity.class);
                                //startActivity(newi);
                                selectedFragment = new UserSearchFragment();
                                horizontalScrollView.setVisibility(HorizontalScrollView.INVISIBLE);
                                chooseLocationButton.setVisibility(View.GONE);
                                myLayout.setVisibility(LinearLayout.GONE);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                                current=4;
                            }
                            break;
                        case R.id.nav_profile:
                            if(current!=5) {
                                selectedFragment = new UserProfileFragment(getPostData.getCurrentUserID());
                                horizontalScrollView.setVisibility(HorizontalScrollView.INVISIBLE);
                                chooseLocationButton.setVisibility(View.GONE);
                                myLayout.setVisibility(LinearLayout.GONE);
                                //Fragment selectedFragment =new UserProfileFragment(user);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                                current=5;
                            }
                            break;
                    }
                    return true;
                }
            };

    public void showHideChooseLocationButtonTimeline(String moduleName){
        String requiredModuleNameForLocationButton = "Timeline";
        if(requiredModuleNameForLocationButton.equals(moduleName)){
            chooseLocationButton.setVisibility(View.VISIBLE);
        } else{
            chooseLocationButton.setVisibility(View.GONE);
        }
    }

    public void chooseLocationButtonClick(View view) {
        Intent open = new Intent(getApplicationContext(), LeaderboardLocationMapview.class);
        open.putExtra("requestCode", MY_REQUEST_CODE);
        startActivityForResult(open, MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requestCode) {
            List<Post> posts = (List<Post>) data.getSerializableExtra("POSTLIST");
            List<User> users = (List<User>) data.getSerializableExtra("USERLIST");
            DataManager dataManager = DataManager.getInstance();
            dataManager.sendPostsUsersByLocation(posts, users);
        }
    }
}