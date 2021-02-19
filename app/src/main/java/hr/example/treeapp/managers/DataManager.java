package hr.example.treeapp.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.core.DataPresenter;
import com.example.core.entities.Post;
import com.example.core.entities.User;

import java.util.ArrayList;
import java.util.List;

import hr.example.treeapp.callbacks.AllPostsCallback;
import hr.example.treeapp.callbacks.AllUsersCallback;
import hr.example.treeapp.repositories.GetPostData;
import hr.example.treeapp.callbacks.GetPostsFromLastID;
import hr.example.treeapp.callbacks.GetPostsInLatLng;
import hr.example.treeapp.callbacks.PostImageCallback;
import hr.example.treeapp.callbacks.UserCallback;
import hr.example.treeapp.callbacks.UserImageCallback;
import hr.example.treeapp.repositories.UserRepository;

public class DataManager {
    private DataManager(){
    }
    private static DataManager instance = new DataManager();
    private GetPostData getPostData = new GetPostData();
    private UserRepository userRepository = new UserRepository();
    private boolean postsReady = false;
    private boolean usersReady = false;
    private DataPresenter presenter;
    private Context context;
    private List<Post> posts = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int numberOfPosts = 0;
    private int numberOfUsers = 0;
    private boolean postBitmapsReady = false;
    private boolean userBitmapsReady = false;
    private boolean newPostsReady = false;

    private boolean firstCall = true;
    private int numberOfNewUsers = 0;
    private boolean userPostoji = false;

    private boolean userpostoji=false;
    private boolean lastWasLocation = false;


    public static DataManager getInstance(){
        return instance;
    }

    public void loadAllData(DataPresenter presenter, Context context){
        this.presenter = presenter;
        this.context = context;
        numberOfPosts = 0;
        numberOfUsers = 0;
        postBitmapsReady = false;
        userBitmapsReady = false;
        postsReady = false;
        usersReady = false;
        newPostsReady = false;
        firstCall = true;
        numberOfNewUsers = 0;
        users.clear();
        userPostoji = false;

        getPostData.getAllPosts(new AllPostsCallback() {
            @Override
            public void onCallback(List<Post> postList) {
                if (postList != null) {
                    posts = postList;
                    postsReady = true;
                    fillPostsWithBitmaps();
                    sendDataToPresenter(presenter);
                } else {
                    Log.d("dokument", "Nema dokumenta objave.");
                }
            }
        });

        userRepository.getAllUsers(new AllUsersCallback() {
            @Override
            public void onCallback(List<User> userList) {
                if (userList != null) {
                    users = userList;
                    usersReady = true;
                    fillUsersWithBitmaps();
                    sendDataToPresenter(presenter);
                } else {
                    Log.d("dokument", "Nema dokumenta objave.");
                }
            }
        });

        }

    public void loadDataTimeline(DataPresenter presenter, Context context){
        lastWasLocation = false;
        this.presenter = presenter;
        this.context = context;
        numberOfPosts = 0;
        numberOfUsers = 0;
        postBitmapsReady = false;
        userBitmapsReady = false;
        postsReady = false;
        usersReady = false;
        newPostsReady = false;
        firstCall = true;
        numberOfNewUsers = 0;
        users.clear();
        userPostoji = false;

        getPostData.getFirstPosts(new AllPostsCallback() {
            @Override
            public void onCallback(List<Post> postList) {
                if (postList != null) {
                    posts = postList;
                    postsReady = true;
                    fillPostsWithBitmaps();
                    getUsers(posts);
                    sendDataToPresenter(presenter);
                } else {
                    Log.d("dokument", "Nema dokumenta objave.");
                }
            }
        });

    }

    public void loadDataMap(DataPresenter presenter, Context context){
        this.presenter = presenter;
        this.context = context;
        numberOfPosts = 0;
        numberOfUsers = 0;
        postBitmapsReady = false;
        userBitmapsReady = false;
        postsReady = false;
        usersReady = false;
        newPostsReady = false;
        firstCall = true;
        numberOfNewUsers = 0;
        users.clear();
        userPostoji = false;



    }
    /**
    public void getPostsInLatLng(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude){
        getPostData.getPostsInLatLngBoundry(minLatitude, maxLatitude, minLongitude, maxLongitude, new GetPostsInLatLng() {
            @Override
            public void onCallbackPostsInLatLng(List<Post> postsInLatLng) {
                if(postsInLatLng!=null){
                    posts=postsInLatLng;
                    newPostsReady=true;
                    postBitmapsReady=true;
                    sendNewDataToPresenter(presenter);
                }
            }
        });
    }*/
    public void getPostsInLatLng(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude){
        getPostData.getPostsInLatLngBoundry(minLatitude, maxLatitude, minLongitude, maxLongitude, new GetPostsInLatLng() {
            @Override
            public void onCallbackPostsInLatLng(List<Post> postsInLatLng) {
                if(postsInLatLng!=null){
                    posts=postsInLatLng;
                    newPostsReady=true;
                    postBitmapsReady=true;
                    usersReady = true;
                    userBitmapsReady = true;
                    sendNewDataToPresenter(presenter);
                }
            }
        });
    }

    private void getUsers(List<Post> posts) {
        numberOfNewUsers = 0;
        if(posts.isEmpty()){
            usersReady = true;
            userBitmapsReady = true;
            sendDataToPresenter(presenter);
            sendNewDataToPresenter(presenter);
        }
        else {
            for (Post p : posts) {
                userRepository.getUser(p, new UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        if (user != null) {
                            numberOfNewUsers++;
                            userPostoji = false;
                            for (User u : users) {
                                if (u.getKorisnickoIme() == user.getKorisnickoIme()) {
                                    userPostoji = true;
                                }
                            }
                            if (!userPostoji) {
                                users.add(user);
                            }
                            if (posts.size() == numberOfNewUsers) {
                                usersReady = true;
                                fillUsersWithBitmaps();
                                sendDataToPresenter(presenter);
                                sendNewDataToPresenter(presenter);
                            }
                        } else {
                            numberOfNewUsers++;
                            sendDataToPresenter(presenter);
                            sendNewDataToPresenter(presenter);
                            Log.d("dokument", "Nema dokumenta objave.");
                        }
                    }
                });
            }
        }
    }

    public void sendDataToPresenter(DataPresenter presenter){
        if(postsReady && usersReady && postBitmapsReady && userBitmapsReady&&firstCall){
            presenter.setData(posts, users, true);
            postBitmapsReady = false;
            usersReady = false;
            userBitmapsReady = false;
            firstCall = false;
        }
    }

    private void fillPostsWithBitmaps() {
        if (numberOfPosts == posts.size()) {
            postBitmapsReady = true;
            sendNewDataToPresenter(presenter);
        } else {
            for (Post p : posts) {
                if (p.getSlika() == null) {
                    getPostData.getPostImage(p.getURL_slike(), new PostImageCallback() {
                        @Override
                        public void onCallback(Bitmap slika) {
                            p.setSlika(slika);
                            numberOfPosts++;
                            if (numberOfPosts == posts.size()) {
                                postBitmapsReady = true;
                                sendDataToPresenter(presenter);
                                sendNewDataToPresenter(presenter);
                            }

                        }
                    });
                }
            }
        }
    }

    private void fillUsersWithBitmaps(){
        numberOfUsers = 0;
        for (User u : users) {
            if (!(u.getProfilnaSlika().contains("https://"))) {
                if (u.getSlika() == null) {
                    userRepository.getUserImage(u.getProfilnaSlika(), new UserImageCallback() {
                        @Override
                        public void onCallback(Bitmap slika) {
                            u.setSlika(slika);
                            numberOfUsers++;
                            if (numberOfUsers == users.size()) {
                                userBitmapsReady = true;
                                sendDataToPresenter(presenter);
                                sendNewDataToPresenter(presenter);
                            }

                        }
                    });
                } else {
                    numberOfUsers++;
                    if (numberOfUsers == users.size()) {
                        userBitmapsReady = true;
                        sendDataToPresenter(presenter);
                        sendNewDataToPresenter(presenter);
                    }
                }
            }
            else{
                numberOfUsers++;
                if (numberOfUsers == users.size()) {
                    userBitmapsReady = true;
                    sendDataToPresenter(presenter);
                    sendNewDataToPresenter(presenter);
                }
            }
        }
    }

    public void GetPostsFromLastID() {
        if (lastWasLocation == false) {
            getPostData.getPostsFromLastID(new GetPostsFromLastID() {
                @Override
                public void onCallback(List<Post> postList) {
                    if (postList != null) {
                        posts.addAll(postList);
                        newPostsReady = true;
                        fillPostsWithBitmaps();
                        getUsers(postList);
                        sendNewDataToPresenter(presenter);
                    } else {
                        Log.d("dokument", "Nema dokumenta objave.");
                    }
                }
            });
        }
    }

    public void sendPostsUsersByLocation(List<Post> postList, List<User> userList){
        lastWasLocation = true;
        numberOfUsers = 0;
        numberOfPosts = 0;
        firstCall = true;
        posts = postList;
        users = userList;
        postsReady = true;
        usersReady = true;
        if(posts.isEmpty()){
            postBitmapsReady = true;
            userBitmapsReady = true;
        }
        else{
            postBitmapsReady = false;
            userBitmapsReady = false;
            fillPostsWithBitmaps();
            fillUsersWithBitmaps();
        }
        sendDataToPresenter(presenter);
    }

    public void sendNewDataToPresenter(DataPresenter presenter){
        if(newPostsReady && usersReady && postBitmapsReady && userBitmapsReady){
            presenter.setData(posts, users, false);
            newPostsReady = false;
            postBitmapsReady = false;
            usersReady = false;
            userBitmapsReady = false;
        }
    }

}
