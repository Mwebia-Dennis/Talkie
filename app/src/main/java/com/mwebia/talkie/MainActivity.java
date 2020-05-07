package com.mwebia.talkie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private ImageView profilePic;
    private EditText userName,email,password,location;
    private Button signUp;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profilePic = findViewById(R.id.profile_pic);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.emailTV);
        password = findViewById(R.id.passwordTV);
        location = findViewById(R.id.location);
        signUp = findViewById(R.id.signUp);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images");

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpToFirebase();
            }
        });
    }

    private void signUpToFirebase() {

        findViewById(R.id.signUp_progressbar).setVisibility(View.VISIBLE);
        signUp.setEnabled(false);
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                saveImageToFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                findViewById(R.id.signUp_progressbar).setVisibility(View.INVISIBLE);
                signUp.setEnabled(true);
            }
        });
    }

    public void signUp_user(final Uri downloadUri) {


        //login user to database;
        if (email.getText().toString().trim().isEmpty()) {

            email.setError("Email is required");

        }else if (userName.getText().toString().trim().isEmpty()) {

            userName.setError("user name is required");

        }else if (password.getText().toString().trim().isEmpty()) {

            password.setError("Enter Your password");

        }else if (location.getText().toString().trim().isEmpty()) {

            userName.setError("user name is required");

        }else {

            //encoding users details to avoid errors when loading url;
            String user_name = "";
            String user_email = "";
            String user_password = "";
            String user_location = "";

            try {

                user_name = URLEncoder.encode(userName.getText().toString().trim(),"UTF-8");
                user_email = URLEncoder.encode(email.getText().toString().trim(),"UTF-8");
                user_password = URLEncoder.encode(password.getText().toString().trim(),"UTF-8");
                user_location = URLEncoder.encode(location.getText().toString().trim(),"UTF-8");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            //register user to database
            //https://penguinsocialappdemo.000webhostapp.com/listPost.php?user_id=5&startFrom=0&operationType=2
            String url = "https://penguinsocialappdemo.000webhostapp.com/signup.php?username="+user_name
                    +"&email="+user_email+ "&password="+user_password+
                    "&location="+user_location+"&picture_path="+downloadUri.toString();
            try {

                new AccessDatabase(MainActivity.this).execute(url);

            }catch (Exception ex){

                ex.printStackTrace();
            }

        }


    }

    //asking for permission from user to acess their images;
    int PICK_IMAGE_CODE = 123;
    public  void checkPermission() {

        if(Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PICK_IMAGE_CODE);
            }
        }
        loadImage();
    }

    //checking the result from checkpermision;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PICK_IMAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            loadImage();
        }else{

            Toast.makeText(MainActivity.this, "Cannot Access Your Images", Toast.LENGTH_SHORT).show();
        }
    }

    int LOAD_IMG_CODE = 200;
    public void loadImage(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,LOAD_IMG_CODE);
    }

    //getting the image from device;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == LOAD_IMG_CODE) && (resultCode == RESULT_OK) && (data != null)&& (data.getData() != null)){

            Uri imageUri = data.getData();
            try {

                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                profilePic.setImageBitmap(imageBitmap);


            } catch (IOException e) {

                e.printStackTrace();

            }


        }
    }

    protected void saveImageToFirebase() {

        String imagePath = System.currentTimeMillis() + (".png");
        final StorageReference imageRef = storageReference.child(imagePath);

        BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray();


        imageRef.putBytes(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i("imageStorageError",e.getMessage());

                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot.getMetadata().getReference() != null){
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            Log.i("download url:",downloadUrl.toString());

                            //sign up the user

                            signUp_user(downloadUrl);
                        }
                    });

                }
            }
        });

    }

    //opening loginpage

    public  void openLoginPage(View view) {

        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
    }
}
