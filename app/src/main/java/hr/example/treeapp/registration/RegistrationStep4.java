package hr.example.treeapp.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hr.example.treeapp.LoginActivity;
import hr.example.treeapp.R;

public class RegistrationStep4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step4);
    }
    public void OpenLogIn(View view) {
        Intent open = new Intent(RegistrationStep4.this, LoginActivity.class);
        startActivity(open);
    }
}