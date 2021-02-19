package hr.example.treeapp.addTree;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.example.treeapp.R;

public class ImageManipulation {

    private final Activity activity;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCroppImg";
    public String currentPhotoPath;

    public ImageManipulation(Activity act){
        activity = act;
    }

    public void startCrop(@NonNull Uri uri){
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName +=".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(activity.getCacheDir(),destinationFileName)));

        uCrop.withAspectRatio(1,1); //omjer fotografije

        uCrop.withOptions(getcroppOptions());

        uCrop.start(activity);
    }

    private UCrop.Options getcroppOptions(){
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(70); //kompresija fotografije
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG); //format fotografije

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false); //zaklučan omjer

        options.setStatusBarColor(activity.getResources().getColor(R.color.baby_green));
        options.setToolbarColor(activity.getResources().getColor(R.color.baby_green));

        options.setToolbarTitle(activity.getString(R.string.crop_image));

        return options;
    }

    /**
     * Metoda služi za kreiranje .jpg file-a u memoriji uređaja
     * @String currentPhotoPath sadrži lokaciju .jpg u memoriji uređaja
     */
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
