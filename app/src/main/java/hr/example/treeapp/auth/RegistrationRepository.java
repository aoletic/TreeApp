package hr.example.treeapp.auth;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;


import androidx.annotation.NonNull;

import hr.example.treeapp.R;
import hr.example.treeapp.callbacks.RegistrationCallback;

public class RegistrationRepository {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    public FirebaseUser user;
    public String dostupno, userID, slikaID;
    List<String> listaKorisnickihImena = new ArrayList<String>();
    public Context context;

    public RegistrationRepository(Context context){
        this.context=context;
    }

    public void checkUsernameAvailability(String korime, final UsernameAvailabilityCallback usernameAvailabilityCallback) {
        firebaseFirestore.collection("Korisnici")
                .whereEqualTo("Korisnicko_ime", korime)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listaKorisnickihImena.add(document.get("Korisnicko_ime").toString());
                            }
                            if(listaKorisnickihImena.contains(korime)){
                                dostupno = "Zauzeto";
                                usernameAvailabilityCallback.onCallback(dostupno);
                            }
                            else{
                                dostupno = "Dostupno";
                                usernameAvailabilityCallback.onCallback(dostupno);
                            }
                        } else {
                            usernameAvailabilityCallback.onCallback(dostupno);
                        }
                    }
                });
    }

    public void firebaseCreateUser(String Email, String Password, String Ime, String Prezime, int Bodovi, String KorIme, String Slika , String datumRodenja, int UlogaID, final RegistrationCallback registrationCallback) {
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("Korisnici").document(userID);
                    Map<String, Object> korisnik = new HashMap<>();
                    korisnik.put("Korisnik_ID", userID);
                    korisnik.put("Ime", Ime);
                    korisnik.put("Prezime", Prezime);
                    korisnik.put("E-mail", Email);
                    korisnik.put("Bodovi", Bodovi);
                    korisnik.put("Korisnicko_ime", KorIme);
                    if (!TextUtils.isEmpty(Slika)) {
                        UploadPicture(Slika);
                        korisnik.put("Profilna_slika_ID", slikaID);
                    }
                    else{
                        korisnik.put("Profilna_slika_ID", context.getString(R.string.registration_default_profile_picture));
                    }
                    korisnik.put("Datum_rodenja", datumRodenja);
                    korisnik.put("Uloga_ID", UlogaID);
                    documentReference.set(korisnik);
                    user=firebaseAuth.getCurrentUser();
                    user.sendEmailVerification();
                    registrationCallback.onCallback(1);
                } else {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        registrationCallback.onCallback(0);
                    }
                    catch (Exception e)
                    {
                        registrationCallback.onCallback(0);
                    }
                }
            }
        });
    }

    public void UploadPicture(String Slika) {
        slikaID = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("Profilne_slike/" + slikaID);
        Uri myUri = Uri.parse(Slika);
        //smanjivanje slike profila
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), myUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        riversRef.putBytes(data);
    }

    public boolean nameEmpty(String Name){
        if(TextUtils.isEmpty(Name)){
            return true;
        }
        return false;
    }

    public boolean surnameEmpty(String Surname){
        if(TextUtils.isEmpty(Surname)){
            return true;
        }
        return false;
    }

    public boolean usernameEmpty(String Username){
        if(TextUtils.isEmpty(Username)){
            return true;
        }
        return false;
    }

    public boolean emailEmpty(String Email){
        if(TextUtils.isEmpty(Email)){
            return true;
        }
        return false;
    }

    public boolean emailNotCorrectFormat(String Email){
        if ((Pattern.compile("^[a-zA-Z0-9.-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,4}$").matcher(Email).matches()) == false){
            return true;
        }
        return false;
    }

    public boolean passwordEmpty(String Password){
        if(TextUtils.isEmpty(Password)){
            return true;
        }
        return false;
    }

    public boolean passwordNotCorrectFormat(String Password){
        if ((Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{6,20}$").matcher(Password).matches()) == false) {
            return true;
        }
        return false;
    }

    public boolean repeatedPasswordEmptyOrIncorrect(String Password, String RepeatedPassword){
        if(TextUtils.isEmpty(RepeatedPassword) || !RepeatedPassword.equals(Password)){
            return true;
        }
        return false;
    }
}
