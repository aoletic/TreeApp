package hr.example.treeapp.addTree;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import hr.example.treeapp.repositories.UserRepository;

public class AddTreeLogic {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private UserRepository userRepository = new UserRepository();

    String slikaID;
    int Broj_lajkova = 0;
    long bodovi;

    Context context;

    Date currentTime = Calendar.getInstance().getTime();

    public AddTreeLogic(Context context){
        this.context = context;
    }

    public void uploadPost(Uri image, Double treeLat, Double treeLng, String treeDesc){

        FirebaseUser userID = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = firebaseFirestore.collection("Objave").document();
        Map<String, Object> objava = new HashMap<>();
        bodovi=20;
        userRepository.updatePoints(bodovi);
        objava.put("ID_objava", documentReference.getId());
        objava.put("Longitude", treeLng);
        objava.put("Latitude", treeLat);
        objava.put("Datum_objave", currentTime);
        objava.put("Opis", treeDesc);
        objava.put("Korisnik_ID", userID.getUid());
        if (!TextUtils.isEmpty(image.toString())) {
            UploadPicture(image);
        }
        objava.put("URL_slike", slikaID);
        objava.put("Broj_lajkova", Broj_lajkova);
        documentReference.set(objava);
    }

    private void UploadPicture(Uri Slika) {
        slikaID = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("Objave/" + slikaID);
        Uri myUri = Slika;
        //smanjivanje slike profila
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), myUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();
        riversRef.putBytes(data);
    }

    public void UpdatePostDescription(String postid, String description){
        DocumentReference documentReference = firebaseFirestore.collection("Objave").document(postid);
        documentReference.update("Opis", description);
    }

    public void UpdatePostLocation(String postid, double latitude, double longitude){
        DocumentReference documentReference = firebaseFirestore.collection("Objave").document(postid);
        documentReference.update("Latitude", latitude, "Longitude", longitude);
    }

}
