package com.mwebia.talkie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    EditText tweet_text_content;
    ImageView postTweet,loadImage,post_image;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        tweet_text_content = findViewById(R.id.tweet_text);
        postTweet = findViewById(R.id.post_tweet);
        loadImage = findViewById(R.id.attach_picture);
        post_image = findViewById(R.id.image_toBePosted);

        storageReference = FirebaseStorage.getInstance().getReference("tweets_images");
        //listView = findViewById(R.id.addTweetLV);
        //ticketType = new ArrayList<>();
        //ticketType.add("add");
        //adapter = new Adapter(AddTweet.this,ticketType);
        //listView.setAdapter(adapter);

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {checkPermission();
            }
        });
        postTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveImageToFirebase();

            }
        });

        findViewById(R.id.fab_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToFirebase();
            }
        });
    }

    private void postTweet(Uri downloadUri) {

        //encoding users tweets to avoid errors when loading url;
        String post_text = null;

        try {
            //encoding the tweet
            post_text = URLEncoder.encode(tweet_text_content.getText().toString(),"UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        //adding the post to our database via our php web-service;
        String url = "https://penguinsocialappdemo.000webhostapp.com/addTweet.php?user_id="+new SaveUserData(AddPostActivity.this).loadUserId()+
                "&tweetText="+post_text+"&tweetImage_path="+downloadUri.toString();

        new AccessDatabase(AddPostActivity.this).execute(url);
    }

    int PICK_IMAGE_CODE = 127;
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

            Toast.makeText(AddPostActivity.this, "Cannot Access Your Images", Toast.LENGTH_SHORT).show();
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

                if (post_image.getVisibility() == View.GONE) {

                    post_image.setVisibility(View.VISIBLE);

                }

                post_image.setImageBitmap(imageBitmap);



            } catch (IOException e) {

                e.printStackTrace();

            }


        }
    }


    private void saveImageToFirebase() {

        String imagePath = System.currentTimeMillis() + (".png");
        final StorageReference imageRef = storageReference.child(imagePath);

        BitmapDrawable drawable = (BitmapDrawable) post_image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray();


        imageRef.putBytes(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i("imageStorageError",e.getMessage());

                Toast.makeText(getApplicationContext(),"sorry could not upload image",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                if (Objects.requireNonNull(taskSnapshot.getMetadata()).getReference() != null){
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.i("download url:", uri.toString());
                            Toast.makeText(getApplicationContext(),"image uploaded",Toast.LENGTH_LONG).show();
                            postTweet(uri);
                        }
                    });

                }
            }
        });

    }
}