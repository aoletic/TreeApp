package hr.example.treeapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import hr.example.treeapp.R;

public class PassReset extends AppCompatActivity {

    EditText email;
    private static final String TAG = "PasswordReset";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);
        email=(EditText)findViewById(R.id.txtBoxEmailReset);

    }

        public void sendPasswordReset(View view) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = email.getText().toString().trim();

            TextView msg=findViewById(R.id.txtMsg);
            Button send=findViewById(R.id.btnPasswordResetAction);
            Button login=findViewById(R.id.btnLoginPassReset);

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                send.setVisibility(View.INVISIBLE);
                                login.setVisibility(View.VISIBLE);
                                msg.setVisibility(View.VISIBLE);

                            }

                        }
                    });

        }
        public void finish(View view){
            finish();
        }



}