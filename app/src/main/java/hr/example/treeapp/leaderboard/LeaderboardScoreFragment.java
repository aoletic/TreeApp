package hr.example.treeapp.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.core.entities.User;

import java.util.ArrayList;
import java.util.List;

import hr.example.treeapp.callbacks.AllUsersCallback;
import hr.example.treeapp.R;
import hr.example.treeapp.repositories.UserRepository;


public class LeaderboardScoreFragment extends Fragment {

    private UserRepository userRepository = new UserRepository();
    private List<User> users = new ArrayList<>();
    View v;
    private RecyclerView myRecyclerView;

    public LeaderboardScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        userRepository.getTopUsers(new AllUsersCallback() {
            @Override
            public void onCallback(List<User> usersList) {
                if (usersList != null) {
                    users = usersList;
                    myRecyclerView=(RecyclerView) v.findViewById(R.id.leaderboard_score_recycler);
                    LeaderboardRecyclerAdapter leaderboardRecyclerAdapter = new LeaderboardRecyclerAdapter(getContext(), users);
                    myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    myRecyclerView.setAdapter(leaderboardRecyclerAdapter);
                    Log.d("Users", users.toString());
                    //  fillLeaderboardProfilePicture(users);
                }
                else{
                    Log.d("dokument", "Nema dokumenta objave.");
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_leaderboardscore, container, false);
        return v;

    }


   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



  /*  public void fillLeaderboardProfilePicture(List<User> userList){
        for (User u : userList) {
            if (!(u.getProfilnaSlika().contains("https://"))) {
                userRepository.getUserImage(u.getProfilnaSlika(), new UserImageCallback() {
                    @Override
                    public void onCallback(Bitmap slika) {
                        u.setSlika(slika);
                        numberOfUsers++;
                        if (numberOfUsers == userList.size()) {
                            userBitmapsReady = true;
                        }
                    }
                });
            } else{
                numberOfUsers++;
                if (numberOfUsers == users.size()) {
                    userBitmapsReady = true;
                }
            }
        }

    }

*/
}

