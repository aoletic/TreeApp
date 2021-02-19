package hr.example.treeapp.userSearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.core.entities.User;

import java.util.ArrayList;
import java.util.List;

import hr.example.treeapp.callbacks.AllUsersCallback;
import hr.example.treeapp.MainActivity;
import hr.example.treeapp.R;
import hr.example.treeapp.repositories.UserRepository;
import hr.example.treeapp.profile.UserProfileFragment;
import hr.example.treeapp.managers.DataPresentersManager;


public class UserSearchFragment extends Fragment {

    private UserRepository userRepository=new UserRepository();
    private List<User> users=new ArrayList<>();
    View v;
    private RecyclerView myRecyclerView;
    EditText editTextUsername;
    TextView textViewInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView=inflater.inflate(R.layout.fragment_user_search_test, container, false);
        myRecyclerView=(RecyclerView) inflatedView.findViewById(R.id.UserList);
        return inflatedView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextUsername=(EditText)getView().findViewById(R.id.editTextUserSearch);
        textViewInfo=(TextView)getView().findViewById(R.id.textViewInfo);
        Button searchButton=(Button) getView().findViewById(R.id.buttonUserSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if( i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    DataPresentersManager dataPresentersManager=new DataPresentersManager(getContext());
                    if(dataPresentersManager.firstPresenter!=null) {
                        //String moduleName = dataPresentersManager.firstPresenter.getModuleName(context);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, dataPresentersManager.firstPresenter.getFragment()).commit();
                        MainActivity.horizontalScrollView.setVisibility(HorizontalScrollView.VISIBLE);
                        MainActivity.myLayout.setVisibility(LinearLayout.VISIBLE);
                        MainActivity.current = 1;
                        MainActivity.bottomNav.setSelectedItemId(R.id.nav_home);
                        MainActivity.chooseLocationButton.setVisibility(View.VISIBLE);
                        //mainActivity.showHideChooseLocationButtonTimeline(moduleName);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void Search(){
        String usernameSearch=editTextUsername.getText().toString();
        if(!usernameSearch.isEmpty()){
            editTextUsername.setError(null);
            userRepository.getUsersSearch(usernameSearch, new AllUsersCallback() {
                @Override
                public void onCallback(List<User> usersList) {
                    if (usersList != null) {
                        users = usersList;
                        UserSearchRecyclerAdapter userSearchRecyclerAdapter = new UserSearchRecyclerAdapter(getContext(), users, new UserSearchRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(User user) {
                                //open user prof
                                Fragment selectedFragment =new UserProfileFragment(user.getUid(),true);
                                //Fragment selectedFragment =new UserProfileFragment(user);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                        });
                        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        myRecyclerView.setAdapter(userSearchRecyclerAdapter);
                        textViewInfo.setText(null);

                    }
                    if(usersList.size()==0){
                        textViewInfo.setText(getResources().getText(R.string.search_not_found));

                    }

                }
            });
        }
        else{
            editTextUsername.setError(getResources().getString(R.string.search_no_username));
        }


    }

}