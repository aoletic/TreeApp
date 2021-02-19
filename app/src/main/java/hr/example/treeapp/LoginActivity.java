package hr.example.treeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;

import hr.example.treeapp.repositories.AuthRepository;
import hr.example.treeapp.auth.LogInStatusCallback;
import hr.example.treeapp.auth.PassReset;
import hr.example.treeapp.registration.RegistrationStep1;

public class     LoginActivity extends AppCompatActivity {

    EditText email, password;
    AuthRepository authRepository;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authRepository= new AuthRepository(this);
        authRepository.createRequest();
        sharedPreferences=this.getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void signIn(View view) {
        Intent signInIntent = authRepository.getSignInIntent();
        startActivityForResult(signInIntent, authRepository.RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            authRepository.requestCheck(requestCode,data);
        }
        catch (ApiException e){
            Toast.makeText(this, "Error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    public void login(View view) throws InterruptedException {
        email=(EditText)findViewById(R.id.txtEmail3);
        password=(EditText)findViewById(R.id.txtPassword3);

        String emailVal=email.getText().toString().trim();
        String passwordVal=password.getText().toString();

        if(authRepository.inputValidation(emailVal,passwordVal)) {
            authRepository.login(emailVal, passwordVal, new LogInStatusCallback() {
                @Override
                public void onCallback(String value) {
                    if (value == "ok") {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else if (value == "notVerified") {
                        email.setError(getString(R.string.email_unconfirmed));
                        password.getText().clear();
                    } else {
                        password.getText().clear();
                        password.setError(getText(R.string.invalid_password_login));

                    }
                }
            });

        }
        if(TextUtils.isEmpty(emailVal)){
            email.setError(getString(R.string.no_email));
        }
        if(TextUtils.isEmpty(passwordVal)){
            password.setError(getString(R.string.no_password));
        }
    }


    public void OpenRegistration(View view) {
        Intent open = new Intent(LoginActivity.this, RegistrationStep1.class);
        startActivity(open);
    }

    public void OpenReset(View view){
        Intent open= new Intent(LoginActivity.this, PassReset.class);
        startActivity(open);
    }


    public void guest(View view) {
        authRepository.guest(sharedPreferences, new LogInStatusCallback(){
            @Override
            public void onCallback(String value) {
                if(value=="ok") {
                    Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, getText(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onBackPressed() {
        System.exit(1);
    }
}