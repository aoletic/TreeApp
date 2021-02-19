package hr.example.treeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import hr.example.treeapp.repositories.AuthRepository;
import hr.example.treeapp.auth.LogInStatusCallback;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    AuthRepository authRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        authRepository= new AuthRepository(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                authRepository.checkIfUserIsLoggedIn(new LogInStatusCallback() {
                    @Override
                    public void onCallback(String value) {
                        if (value == "user_is_logged_in") {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }
                });
                finish();
            }
        }, 1500);
    }

}