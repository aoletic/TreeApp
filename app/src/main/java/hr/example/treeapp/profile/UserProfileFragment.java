package hr.example.treeapp.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.core.entities.Post;
import com.example.core.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;

import hr.example.treeapp.auth.RegistrationRepository;
import hr.example.treeapp.auth.UsernameAvailabilityCallback;
import hr.example.treeapp.callbacks.GetLikesForPostCallback;
import hr.example.treeapp.repositories.GetPostData;
import hr.example.treeapp.LoginActivity;
import hr.example.treeapp.MainActivity;
import hr.example.treeapp.callbacks.PostImageCallback;
import hr.example.treeapp.callbacks.ProfileImageCallback;
import hr.example.treeapp.R;
import hr.example.treeapp.callbacks.UserCallback;
import hr.example.treeapp.entities.UserImage;
import hr.example.treeapp.repositories.UserRepository;
import hr.example.treeapp.userSearch.UserSearchFragment;
import hr.example.treeapp.callbacks.UsersPostsCallback;
import hr.example.treeapp.notifications.NotificationsActivity;
import hr.example.treeapp.postView.SinglePostViewActivity;
import hr.example.treeapp.managers.DataPresentersManager;

import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends Fragment {
    User selectedUser;
    String userID;
    UserRepository userRepository;
    TextView textViewName;
    TextView textViewUserName;
    ImageView imageViewProfil;
    TextView textViewPoints;
    TextView textViewPosts;
    GetPostData getPostData;
    Context context;
    ImageButton imageButton;
    ImageView changePicture;
    GridView gridView;
    int imageWidth;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private RegistrationRepository registrationRepository;
    MainActivity mainActivity;
    boolean isSearch=false;
    int brojac;
    int brojLajkova;
    public UserProfileFragment(User user) {
        this.selectedUser = user;
    }

    public UserProfileFragment(String userID, boolean isSearch) {
        this.userID = userID;
        this.isSearch=isSearch;
    }

    public UserProfileFragment(String userID) {
        this.userID = userID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        //myRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.UserProfilePostsList);
        gridView=(GridView)inflatedView.findViewById(R.id.gridViewPosts);
        int gridWidth=getResources().getDisplayMetrics().widthPixels;
        imageWidth=gridWidth/3;
        gridView.setColumnWidth(imageWidth);
        return inflatedView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        mainActivity=new MainActivity();
        registrationRepository = new RegistrationRepository(context);
        userInfo();
        getUsersPosts();
        imageButton = view.findViewById(R.id.popUpButtonProfile);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupProfile(view);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP && !isSearch) {
                    DataPresentersManager dataPresentersManager=new DataPresentersManager(context);
                    if(dataPresentersManager.firstPresenter!=null) {
                        //String moduleName = dataPresentersManager.firstPresenter.getModuleName(context);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, dataPresentersManager.firstPresenter.getFragment()).commit();
                        MainActivity.horizontalScrollView.setVisibility(HorizontalScrollView.VISIBLE);
                        MainActivity.myLayout.setVisibility(LinearLayout.VISIBLE);
                        MainActivity.current = 1;
                        MainActivity.bottomNav.setSelectedItemId(R.id.nav_home);
                        MainActivity.chooseLocationButton.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                if( i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP && isSearch) {
                        Fragment selectedFragment = new UserSearchFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                        MainActivity. horizontalScrollView.setVisibility(HorizontalScrollView.INVISIBLE);
                        MainActivity.chooseLocationButton.setVisibility(View.GONE);
                        MainActivity.myLayout.setVisibility(LinearLayout.GONE);
                        MainActivity.current=4;
                        MainActivity.bottomNav.setSelectedItemId(R.id.nav_search);
                        isSearch=false;
                        return true;
                }
                return false;
            }

        });

    }

    private void getUsersPosts() {
        brojac=0;
        brojLajkova=0;
        textViewPoints = (TextView) getView().findViewById(R.id.textViewPoints);
        textViewPosts = (TextView) getView().findViewById(R.id.textViewPosts);
        getPostData = new GetPostData();
        getPostData.getUsersPosts(userID, new UsersPostsCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCallback(List<Post> usersPostsList) {
                if (usersPostsList != null) {
                        textViewPosts.setText(String.valueOf(usersPostsList.size()));
                        usersPostsList.forEach((n)->getPostData.getPostLikes(n.getID_objava(), new GetLikesForPostCallback() {
                            @Override
                            public void onCallback(List<String> listOfLikesByUserID) {
                                brojLajkova+=listOfLikesByUserID.size();
                                textViewPoints.setText(String.valueOf(brojLajkova));
                            }
                        }));
                        usersPostsList.forEach((n) -> getPostData.getPostImage(n.getURL_slike(), new PostImageCallback() {
                            @Override
                            public void onCallback(Bitmap slika) {
                                n.setSlika(slika);
                                brojac++;
                                if(brojac==usersPostsList.size()){
                                    gridView.setAdapter(new ProfilePostAdapter(getActivity(), usersPostsList, imageWidth));
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String postID=usersPostsList.get(position).getID_objava();
                                            Intent open = new Intent(getContext(), SinglePostViewActivity.class);
                                            open.putExtra("postId", postID);
                                            startActivity(open);
                                        }
                                    });
                                }

                            }
                        }));
                }
            }
        });


    }

    private void userInfo() {
        textViewName = (TextView) getView().findViewById(R.id.textViewName);
        textViewUserName = (TextView) getView().findViewById(R.id.textViewUserName);
        imageViewProfil = (ImageView) getView().findViewById(R.id.imageViewProfilePicture);
        
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(fUser.isAnonymous()){
            textViewName.setText(R.string.anonymous);
            textViewUserName.setText(R.string.anonuser);
            Bitmap bm= BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
            imageViewProfil.setImageBitmap(bm);
        }
        //za UserProfileFragment(String userID);
        if (userID != null) {
            userRepository = new UserRepository();
            userRepository.getUser(userID, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    if (user == null) {

                    }
                    else{
                        selectedUser = user;
                        textViewName.setText(selectedUser.getIme() + " " + selectedUser.getPrezime());
                        textViewUserName.setText("@" + selectedUser.getKorisnickoIme());
                        userRepository.getUserImage(selectedUser.getUid(), new ProfileImageCallback() {
                            @Override
                            public void onCallbackList(UserImage userImage) {
                                if(userImage.image!=null && userImage.url==null) {
                                    Glide.with(getActivity()).load(userImage.image).into(imageViewProfil);
                                }
                                if(userImage.url!=null && userImage.image==null){
                                    RequestOptions options = new RequestOptions()
                                            .placeholder(R.mipmap.ic_launcher_round)
                                            .error(R.mipmap.ic_launcher_round);
                                    Glide.with(getActivity()).load(userImage.url).apply(options).into(imageViewProfil);
                                }

                            }

                        });
                    }
                }
            });
        }
        //za UserProfileFragment(User user)
        else {
            userRepository.getUserImage(selectedUser.uid, new ProfileImageCallback() {
                @Override
                public void onCallbackList(UserImage userImage) {
                    imageViewProfil.setImageBitmap(userImage.image);
                }
            });
            textViewName.setText(selectedUser.ime + " " + selectedUser.prezime);
            textViewUserName.setText("@" + selectedUser.korisnickoIme);
            textViewPoints.setText(Long.toString(selectedUser.bodovi));
        }
    }

    public void showPopupProfile(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.userprofilepopupmenu, popup.getMenu());
        if (!getPostData.getCurrentUserID().equals(userID)) {
            popup.getMenu().findItem(R.id.changeprofilepic).setVisible(false);
            popup.getMenu().findItem(R.id.changeuserdata).setVisible(false);
            popup.getMenu().findItem(R.id.changepassword).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.changeprofilepic).setVisible(true);
            popup.getMenu().findItem(R.id.changeuserdata).setVisible(true);
            popup.getMenu().findItem(R.id.changepassword).setVisible(true);
        }
        FirebaseUser fUser=FirebaseAuth.getInstance().getCurrentUser();
        if(fUser.isAnonymous()){
            popup.getMenu().findItem(R.id.changeprofilepic).setVisible(false);
            popup.getMenu().findItem(R.id.changeuserdata).setVisible(false);
            popup.getMenu().findItem(R.id.changepassword).setVisible(false);
            popup.getMenu().findItem(R.id.notifications).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.changeprofilepic:
                        changeProfilePicture();
                        break;
                    case R.id.changeuserdata:
                        changeUserData();
                        break;
                    case R.id.changepassword:
                        changeUserPassword();
                        break;
                    case R.id.notifications:
                        Intent openNotifications = new Intent (context.getApplicationContext(), NotificationsActivity.class);
                        startActivity(openNotifications);
                        break;
                    case R.id.logout:
                        userRepository.logout();
                        startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                }
                return true;
            }
        });
        popup.show();
    }

    private void changeUserData() {
        userRepository.getCurrentUser(new UserCallback() {
            @Override
            public void onCallback(User user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TextView textView = new TextView(context);
                textView.setText(context.getString(R.string.changeuserdata));
                textView.setPadding(20, 30, 20, 30);
                textView.setTextSize(20F);
                textView.setBackgroundColor(getResources().getColor(R.color.baby_green));
                textView.setTextColor(getResources().getColor(R.color.tree_green));

                builder.setCustomTitle(textView);
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_userdata, (ViewGroup) getView(), false);

                final EditText changeuserdataName = (EditText) viewInflated.findViewById(R.id.changeuserdataName);
                changeuserdataName.setText(user.ime);
                final EditText changeuserdataSurname = (EditText) viewInflated.findViewById(R.id.changeuserdataSurname);
                changeuserdataSurname.setText(user.prezime);
                final EditText changeuserdataUsername = (EditText) viewInflated.findViewById(R.id.changeuserdataUsername);
                changeuserdataUsername.setText(user.korisnickoIme);

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ime = changeuserdataName.getText().toString();
                        String prezime = changeuserdataSurname.getText().toString();
                        String korime = changeuserdataUsername.getText().toString();
                        if (changeuserdataName != null && changeuserdataSurname != null && changeuserdataUsername != null) {
                            if (!user.korisnickoIme.equals(korime)) {
                                registrationRepository.checkUsernameAvailability(korime, new UsernameAvailabilityCallback() {
                                    @Override
                                    public void onCallback(String value) {
                                        if (value.equals("Dostupno")) {
                                            user.ime = ime;
                                            user.prezime = prezime;
                                            user.korisnickoIme = korime;
                                            userRepository.changeUserDataFirebase(user);
                                            userRepository.getCurrentUser(new UserCallback() {
                                                @Override
                                                public void onCallback(User user) {
                                                    textViewName.setText(user.ime + " " + user.prezime);
                                                    textViewUserName.setText(user.korisnickoIme);
                                                    dialog.dismiss();
                                                }
                                            });
                                        } else
                                            changeuserdataUsername.setError(context.getString(R.string.username_taken));
                                    }
                                });
                            } else {
                                user.ime = ime;
                                user.prezime = prezime;
                                user.korisnickoIme = korime;
                                userRepository.changeUserDataFirebase(user);
                                userRepository.getCurrentUser(new UserCallback() {
                                    @Override
                                    public void onCallback(User user) {
                                        textViewName.setText(user.ime + " " + user.prezime);
                                        textViewUserName.setText(user.korisnickoIme);
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    private void changeUserPassword() {
        final String[] error = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        TextView textView = new TextView(context);
        textView.setText(context.getString(R.string.changepassword));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(getResources().getColor(R.color.baby_green));
        textView.setTextColor(getResources().getColor(R.color.tree_green));

        builder.setCustomTitle(textView);
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, (ViewGroup) getView(), false);
        final EditText changeuserdataNewPassword = (EditText) viewInflated.findViewById(R.id.changeuserdataNewPassword);
        final EditText changeuserdataNewPasswordRepeat = (EditText) viewInflated.findViewById(R.id.changeuserdataNewPasswordRepeat);

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //asd
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = changeuserdataNewPassword.getText().toString();
                String repeatPass = changeuserdataNewPasswordRepeat.getText().toString();
                if (!newPass.equals("") && !repeatPass.equals("")) {
                    if (registrationRepository.passwordNotCorrectFormat(newPass) == false) {
                        if (newPass.equals(repeatPass)) {
                            userRepository.changeUserPasswordFirebase(repeatPass);
                            dialog.dismiss();
                        } else {
                            changeuserdataNewPasswordRepeat.setError(context.getString(R.string.invalid_password_repeat));
                        }
                    } else {
                        changeuserdataNewPassword.setError(context.getString(R.string.invalid_password));
                    }
                }
            }
        });
    }

    private void changeProfilePicture() {
        userRepository.getCurrentUser(new UserCallback() {
            @Override
            public void onCallback(User user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                TextView textView = new TextView(context);
                textView.setText(context.getString(R.string.changeprofilepic));
                textView.setPadding(20, 30, 20, 30);
                textView.setTextSize(20F);
                textView.setBackgroundColor(getResources().getColor(R.color.baby_green));
                textView.setTextColor(getResources().getColor(R.color.tree_green));

                builder.setCustomTitle(textView);
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_changeprofile_picture, (ViewGroup) getView(), false);
                final ImageView changeProfilePicture = (ImageView) viewInflated.findViewById(R.id.change_imgProfile);
                changePicture = changeProfilePicture;
                changeProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseImage();
                    }
                });

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registrationRepository.UploadPicture(filePath.toString());
                        userRepository.deleteProfilePicture(user.profilnaSlika);
                        user.profilnaSlika = registrationRepository.slikaID;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        userRepository.changeUserProfilePicture(user);
                        userRepository.getCurrentUser(new UserCallback() {
                            @Override
                            public void onCallback(User user) {
                                userRepository.getUserImage(user.uid, new ProfileImageCallback() {
                                    @Override
                                    public void onCallbackList(UserImage userImage) {
                                        imageViewProfil.setImageBitmap(userImage.image);
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                changePicture.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsersPosts();
    }
}