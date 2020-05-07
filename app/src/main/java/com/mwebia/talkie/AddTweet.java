package com.mwebia.talkie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddTweet extends AppCompatActivity {

    //ListView listView;
    //Adapter adapter;
    EditText tweet_text_content;
    ImageView postTweet,loadImage;
    Bitmap imageBitmap;
    StorageReference storageReference;
    String downloadUri;
    //ArrayList<String> ticketType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tweet);

        tweet_text_content = findViewById(R.id.tweet_text);
        postTweet = findViewById(R.id.post_tweet);
        loadImage = findViewById(R.id.attach_picture);
        imageBitmap = null;
        downloadUri = "";
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
                String imagePath;
                if (imageBitmap != null) {

                    downloadUri = saveImageToFirebase(imageBitmap).toString();
                }

                //encoding users tweets to avoid errors when loading url;
                String post_text = null;

                try {

                    post_text = URLEncoder.encode(tweet_text_content.getText().toString(),"UTF-8");

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();

                }

                //https://penguinsocialappdemo.000webhostapp.com/addTweet.php?user_id=1&tweetText=Godisgood&tweetImage_path=images.bazu.jpg
                String url = "https://penguinsocialappdemo.000webhostapp.com/addTweet.php?user_id="+new SaveUserData(AddTweet.this).loadUserId()+
                        "&tweetText="+post_text+"&tweetImage_path="+downloadUri;
                new AccessDatabase(AddTweet.this).execute(url);
            }
        });
    }



    /*
    public class Adapter extends BaseAdapter{

        Context context;
        ArrayList<String> ticketType1;
        public Adapter(Context context, ArrayList<String> ticketType2) {
            this.context = context;
            this.ticketType1 = ticketType2;
        }
        @Override
        public int getCount() {
            return ticketType.size();
        }

        @Override
        public Object getItem(int position) {
            return ticketType1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Holder holder;


            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.tweets_ticket, parent,false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }


            holder.attachImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //selecting image from device storage;
                    checkPermission();
                }
            });

            holder.postTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //post tweet to our db;

                    if (imageBitmap != null){

                        downloadUri = saveImageToFirebase(imageBitmap).toString();
                    }

                    //encoding users tweets to avoid errors when loading url;
                    String post_text = null;

                    try {

                        post_text = URLEncoder.encode(holder.editText.getText().toString(),"UTF-8");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                    String url = "https://penguinsocialappdemo.000webhostapp.com/addTweet.php?user_id="+new SaveUserData(AddTweet.this).loadUserId()+
                            "&tweetText="+post_text+"&tweetImage_path="+downloadUri;
                    new AccessDatabase(AddTweet.this).execute(url);
                }
            });
            return convertView;
        }
    }

    public class Holder{
        EditText editText;
        ImageView postTweet;
        ImageView attachImg;

        public Holder(View view){

            editText = view.findViewById(R.id.tweet_text);
            postTweet = view.findViewById(R.id.post_tweet);
            attachImg = view.findViewById(R.id.attach_picture);
        }
    }


     */
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

            Toast.makeText(AddTweet.this, "Cannot Access Your Images", Toast.LENGTH_SHORT).show();
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

                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);


            } catch (IOException e) {

                e.printStackTrace();

            }


        }
    }

    protected Uri saveImageToFirebase(Bitmap bitmap) {

        Uri downloadUrl = null;
        String imagePath = System.currentTimeMillis() + (".png");
        final StorageReference imageRef = storageReference.child(imagePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray();


        imageRef.putBytes(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.i("imageStorageError",e.getMessage());

                Toast.makeText(getApplicationContext(),"sorry could not upload your profile picture",Toast.LENGTH_LONG).show();
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

                        }
                    });

                }
            }
        });

        return downloadUrl;
    }
}
