package hr.example.treeapp.repositories;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import hr.example.treeapp.MainActivity;
import hr.example.treeapp.R;
import hr.example.treeapp.auth.LogInStatusCallback;
import hr.example.treeapp.auth.RegistrationRepository;
import hr.example.treeapp.auth.UsernameAvailabilityCallback;

public class AuthRepository {

    public static final int RC_SIGN_IN = 123;
    public static FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private GoogleSignInAccount account;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private Context context;

    public String googleSignInUsername;
    public boolean googleSignInUserNameAvailable;
    public String returnValue;
    public AuthRepository(Context context) {
        this.context = context;
    }

    public void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf("207643504221-mtkrdhkctrvcpq18cr7n8m8a0vldi4a1.apps.googleusercontent.com"))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void requestCheck(int requestCode, Intent data) throws ApiException {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                throw e;
            }
        }
    }

    public Intent getSignInIntent() {
        mGoogleSignInClient.signOut();
        return mGoogleSignInClient.getSignInIntent();

    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (account != null) {
                                DocumentReference documentReference = firebaseFirestore.collection("Korisnici").document(user.getUid());
                                Map<String, Object> korisnik = new HashMap<>();
                                korisnik.put("Korisnik_ID", user.getUid());
                                korisnik.put("Ime", account.getGivenName());
                                korisnik.put("Prezime", account.getFamilyName());
                                korisnik.put("E-mail", account.getEmail());
                                korisnik.put("Bodovi", 0);
                                korisnik.put("Uloga_ID", 2);
                                korisnik.put("Datum_rodenja", "1/1/2000");
                                Uri photoUri = account.getPhotoUrl();
                                korisnik.put("Profilna_slika_ID", photoUri.toString());
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (!document.exists()) {
                                                googleSignInUserNameInput(new LogInStatusCallback() {
                                                    @Override
                                                    public void onCallback(String value) {
                                                        if(value=="ok"){
                                                            korisnik.put("Korisnicko_ime", googleSignInUsername);
                                                            googleSignInFirebaseInsertUser(documentReference, korisnik);
                                                            Intent i1=new Intent(context, MainActivity.class);
                                                            context.startActivity(i1);
                                                        }

                                                    }
                                                });
                                            }
                                            else{
                                                //googleSignInFirebaseUpdateUser(documentReference, korisnik);
                                                Intent i1=new Intent(context, MainActivity.class);
                                                context.startActivity(i1);
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            //Toast.makeText(MainActivity.this, getText(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                        }
                    }


                });
    }

    private void googleSignInFirebaseInsertUser(DocumentReference documentReference, Map<String, Object> korisnik) {
        documentReference.set(korisnik).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("info123", "Uspjesno upisano!");
            }
        });
    }
    private void googleSignInFirebaseUpdateUser(DocumentReference documentReference, Map<String, Object> korisnik) {
        documentReference.update("Ime", korisnik.get("Ime"));
        documentReference.update("Prezime", korisnik.get("Prezime"));
        documentReference.update("Profilna_slika_ID", korisnik.get("Profilna_slika_ID"));
        documentReference.update("Datum_rodenja", korisnik.get("Datum_rodenja"));
        Log.i("info123", "Uspjesno azurirano!");
    }

    public void googleSignInUserNameInput(final LogInStatusCallback logInStatusCallback){
        googleSignInUsername="";
        googleSignInUserNameAvailable=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.enter_username));
        final EditText input=new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputVal=input.getText().toString();
                RegistrationRepository registrationRepository = new RegistrationRepository(context);
                registrationRepository.checkUsernameAvailability(inputVal, new UsernameAvailabilityCallback() {
                    @Override
                    public void onCallback(String value) {
                        if (value == "Dostupno") {
                            googleSignInUserNameAvailable = true;
                            googleSignInUsername=inputVal;
                        }
                        else{
                            googleSignInUserNameAvailable = false;
                            input.setError(context.getResources().getString(R.string.username_taken));
                        }
                        if(inputVal.isEmpty()) input.setError(context.getResources().getString(R.string.no_username));
                        else if(!googleSignInUserNameAvailable){
                            setValueMethod("not_available");
                            logInStatusCallback.onCallback(returnValue);
                        }
                        else{
                            setValueMethod("ok");
                            logInStatusCallback.onCallback(returnValue);
                            dialog.dismiss();
                        }
                    }
                });

            }
        });
    }

    public void login(String emailVal, String passwordVal, final LogInStatusCallback loginCallback) {
        String value;
        firebaseAuth.signInWithEmailAndPassword(emailVal, passwordVal).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success
                    user = firebaseAuth.getCurrentUser();
                    if (!user.isEmailVerified()) {
                        setValueMethod("notVerified");
                    } else {
                        setValueMethod("ok");
                    }
                    loginCallback.onCallback(returnValue);
                } else {
                    // Sign in fail
                    setValueMethod("error");
                    loginCallback.onCallback(returnValue);
                }

            }
        });
    }

    public void setValueMethod(String value) {
        returnValue = value;
    }

    public void guest(SharedPreferences sharedPreferences, final LogInStatusCallback loginCallback) {
        String guest_uid=sharedPreferences.getString("guest_uid", "undefined");
        Log.i("citanje", guest_uid);
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            user = firebaseAuth.getCurrentUser();
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("guest_uid", user.getUid());
                            editor.apply();
                            setValueMethod("ok");
                            loginCallback.onCallback(returnValue);
                        } else {
                            // Sign in fail
                            setValueMethod("error");
                            loginCallback.onCallback(returnValue);
                        }
                    }
                });

    }
    public void checkIfUserIsLoggedIn(final LogInStatusCallback logInCallback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            setValueMethod("user_is_logged_in");
            logInCallback.onCallback(returnValue);
        }
        else{
            setValueMethod("user_is_not_logged_in");
            logInCallback.onCallback(returnValue);
        }
    }
    public boolean inputValidation(String emailVal, String passwordVal){
        return !TextUtils.isEmpty(emailVal) && !TextUtils.isEmpty(passwordVal);
    }
}


