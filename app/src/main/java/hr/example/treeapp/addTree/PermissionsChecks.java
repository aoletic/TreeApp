package hr.example.treeapp.addTree;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionsChecks {

    private Activity activity;

    public PermissionsChecks(Activity activity){
        this.activity=activity;
    }
    public boolean checkLocationPermissions(){
        return ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkCameraAndStoragePermissions(){
        return ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkAllPermissions(){
        return checkCameraAndStoragePermissions() && checkLocationPermissions();
    }


    public boolean checkAnyCameraAndStoragePermission (){
        return ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkBuildVersion(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public boolean checkAnyImportantPermission (){
        return ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
