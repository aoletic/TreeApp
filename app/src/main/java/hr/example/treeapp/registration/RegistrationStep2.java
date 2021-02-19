package hr.example.treeapp.registration;

import androidx.appcompat.app.AppCompatActivity;
import hr.example.treeapp.repositories.AuthRepository;
import hr.example.treeapp.auth.RegistrationRepository;
import hr.example.treeapp.auth.UsernameAvailabilityCallback;
import hr.example.treeapp.LoginActivity;
import hr.example.treeapp.R;
import hr.example.treeapp.callbacks.RegistrationCallback;
import hr.example.treeapp.repositories.UserRepository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegistrationStep2 extends AppCompatActivity {
    EditText email, korIme, password, repeatedPassword;
    String Ime, Prezime, Slika;
    int day, month, year;
    String datumRodenja;

    AuthRepository authRepository;
    RegistrationRepository registrationRepository;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step2);

        authRepository= new AuthRepository(this);
        registrationRepository = new RegistrationRepository(this);
        userRepository = new UserRepository();

        Intent intent = getIntent();
        Ime = intent.getStringExtra("name_key");
        Prezime = intent.getStringExtra("surname_key");

        Bundle extras = getIntent().getExtras();
        day = extras.getInt("day_key");
        month = extras.getInt("month_key");
        year = extras.getInt("year_key");

        datumRodenja = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);

        String extraStr = extras.getString("image_key");
        if (extraStr == null) {
            Log.d("Poruka", "Nema slike");
        } else {
            Log.d("Poruka", "Ima slike");
            Slika = extraStr;
            Log.d("Poruka", Slika);
        }


        //dohvacanje upisanih podataka kao objekte
        email = (EditText) findViewById(R.id.txtBoxStep2Email);
        korIme = (EditText) findViewById(R.id.txtBoxStep2Username);
        password = (EditText) findViewById(R.id.txtBoxStep2Password);
        repeatedPassword = (EditText) findViewById(R.id.txtBoxStep2PasswordRepeat);

    }

    public void OpenRegistrationStep1(View view) {
        Intent open = new Intent(RegistrationStep2.this, RegistrationStep1.class);
        startActivity(open);
        overridePendingTransition(R.anim.slideleft, R.anim.stayinplace);
    }

    public boolean korimeDostupno;

    public void OpenRegistrationStep3(View view) {
        String KorIme = korIme.getText().toString().trim();

        if (registrationRepository.usernameEmpty(KorIme)) {
            korIme.setError(getString(R.string.no_username));
            return;
        }

        registrationRepository.checkUsernameAvailability(KorIme, new UsernameAvailabilityCallback() {
            @Override
            public void onCallback(String value) {
                if (value == "Dostupno") {
                    korimeDostupno = true;
                    OtvoriRegistrationStep3(KorIme);
                }
                else{
                    korimeDostupno = false;
                    korIme.setError(getString(R.string.username_taken));
                }
            }
        });
    }

    public void OtvoriRegistrationStep3(String KorIme){
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String RepeatedPassword = repeatedPassword.getText().toString().trim();
        Integer Bodovi = 0;
        Integer UlogaID = 2;


        //provjerava je li upisan e-mail
        if (registrationRepository.emailEmpty(Email)) {
            email.setError(getString(R.string.no_email));
            return;
        }

        //provjerava je li struktura e-maila točna
        if (registrationRepository.emailNotCorrectFormat(Email)) {
            email.setError(getString(R.string.invalid_email));
            return;
        }

        //provjerava je li upisana lozinka
        if (registrationRepository.passwordEmpty(Password)) {
            password.setError(getString(R.string.no_password));
            return;
        }

        //provjerava sadrži li lozinka između 6 i 20 znakova, barem 1 veliko slovo i jedan broj
        if (registrationRepository.passwordNotCorrectFormat(Password)) {
            password.setError(getString(R.string.invalid_password));
            return;
        }


        //provjerava je li točno upisana ponovljena lozinka
        if (registrationRepository.repeatedPasswordEmptyOrIncorrect(Password, RepeatedPassword)) {
            repeatedPassword.setError(getString(R.string.invalid_password_repeat));
            return;
        }

        //upisuje korisnika u Authentication i Database kolekciju korisnici ukoliko nema pogreške
        registrationRepository.firebaseCreateUser(Email, Password, Ime, Prezime, Bodovi, KorIme, Slika, datumRodenja, UlogaID, new RegistrationCallback() {
            @Override
            public void onCallback(int result) {
                if(result == 1){
                    Intent open = new Intent(RegistrationStep2.this, RegistrationStep3.class);
                    open.putExtra("email", Email);
                    startActivity(open);
                } else {
                    email.setError(getString(R.string.email_exist));
                }
                userRepository.logout();
            }
        });
    }

    public void OpenLogIn(View view) {
        Intent open = new Intent(RegistrationStep2.this, LoginActivity.class);
        startActivity(open);
        overridePendingTransition(R.anim.slideleft, R.anim.stayinplace);
    }
}
