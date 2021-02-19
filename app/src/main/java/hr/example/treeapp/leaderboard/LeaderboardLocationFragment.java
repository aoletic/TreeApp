package hr.example.treeapp.leaderboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.core.entities.User;

import java.util.ArrayList;
import java.util.List;

import hr.example.treeapp.R;


public class LeaderboardLocationFragment extends Fragment{

    private List<User> users;
    HorizontalScrollView mainLinearLayout;
    private Button button;
    private static final int MY_REQUEST_CODE = 0xe110;
    List<User> leaderboardKorisnici= new ArrayList<>();
    private RecyclerView myRecyclerView;
    View view;


    public LeaderboardLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_leaderboardlocation, container, false);
        return view;

    }


   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button=view.findViewById(R.id.button_location_leaderboard);
        button.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               leaderboardKorisnici.clear();
               Intent open = new Intent(getActivity(), LeaderboardLocationMapview.class);
               open.putExtra("requestCode", MY_REQUEST_CODE);
               startActivityForResult(open, MY_REQUEST_CODE);


               Activity thisActivity=getActivity();


           }
       });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (resultCode == requestCode) {
                    leaderboardKorisnici = (List<User>) data.getSerializableExtra("USERLIST");
                }

        myRecyclerView=(RecyclerView)view.findViewById(R.id.leaderboard_location_recycler);
        LeaderboardRecyclerAdapter leaderboardRecyclerAdapter = new LeaderboardRecyclerAdapter(getContext(), leaderboardKorisnici);
        myRecyclerView.setAdapter(leaderboardRecyclerAdapter);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecyclerView.setAdapter(leaderboardRecyclerAdapter);

    }

}

