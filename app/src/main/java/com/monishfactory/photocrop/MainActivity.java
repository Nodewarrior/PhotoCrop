package com.monishfactory.photocrop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Toolbar toolbar;
    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent;
    final int RequestPermissionCode = 1;
    DisplayMetrics displayMetrics;
    int width, height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PhotoCrop");
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            RequestRuntimePermission();
        }
    }

    private void RequestRuntimePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "CAMERA permission allows us to access CAMERA app", Toast.LENGTH_SHORT).show();

        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btn_camera)
            CameraOpen();
        else if(item.getItemId() == R.id.btn_gallery)
            GalleryOpen();
            return true;
    }

    private void GalleryOpen() {
        GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image from Gallery"), 2);

    }

    private void CameraOpen() {
        CamIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(),
                "file"+ String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        CamIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        CamIntent.putExtra("return-date", true);
        startActivityForResult(CamIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            PhotoCrop();
        }
        else if(requestCode == 2) {
            if(data != null) {
                uri = data.getData();
                PhotoCrop();
            }
        }
        else if(requestCode == 1) {
            if(data != null) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void PhotoCrop() {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", "true");
            CropIntent.putExtra("aspectY", "true");
            CropIntent.putExtra("scaleUpIfNeeded", "true");
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);
        }

        catch (ActivityNotFoundException ex) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case RequestPermissionCode:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
