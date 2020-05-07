package com.mwebia.talkie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

public class HomePageActivity extends AppCompatActivity {

    ArrayList<Tweets> listOfTweets;
    UsersListAdapter adapter;
    Toolbar toolbar;
    ListView listView;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FloatingActionButton fab_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //checking if the user is logged in or they are new users;
        new SaveUserData(this).loadUserId();

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar1);
        listView = findViewById(R.id.listView);
        mAuth = FirebaseAuth.getInstance();

        fab_main = findViewById(R.id.fab_main);


        //onclick listener for main floating button;
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open add new tweet page;
                Intent intent = new Intent(HomePageActivity.this,FollowingPageActivity.class);
                startActivity(intent);

                //todo we will be used to open other fabs;
            }
        });


        /*
        *initialising our arraylist and adapter;
        * setting our adapter to our list view;
         */
        listOfTweets = new ArrayList<>();

        adapter = new UsersListAdapter(this,listOfTweets);
        listView.setAdapter(adapter);


        //load tweets from the talkie_users that this app_user follows plus app_users tweets;
        loadTweets(new SaveUserData(this).loadUserId(), 0,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        SearchView sv  = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        sv.setIconified(true);
        sv.setSubmitButtonEnabled(true);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                try {

                    query = URLEncoder.encode(query,"UTF-8");

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();

                }

                loadTweets(query,0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.isEmpty()){
                    try {

                        newText = URLEncoder.encode(newText,"UTF-8");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                    loadTweets(newText,0);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                //logging the user out;
                /*
                 * deleting users_id from device;
                 * sign out from firebase;
                 * open sign up activity and clear all activities on top of hierarchy;

                 */
                new SaveUserData(this).deleteUserId();
                mAuth.signOut();
                Intent i = new Intent(HomePageActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

                break;
            case R.id.post_new_tweet:
                //open add new tweet page;
                Intent intent = new Intent(HomePageActivity.this,AddPostActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadTweets(String query, int startFrom) {

        /*
        *Operation 3 : loading all tweets according to query submitted;
        * todo:variable startFrom will be used to load more tweets;
         */
        progressBar.setVisibility(View.VISIBLE);
        String url;



        url = "https://penguinsocialappdemo.000webhostapp.com/listPost.php?query="+query+"&startFrom="+startFrom+"&operationType=3";
        try {

            DoInBackground doInBackground = new DoInBackground();
            doInBackground.execute(url);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void loadTweets(int user_id, int startFrom,int operationType) {

        progressBar.setVisibility(View.VISIBLE);
        /*
        *if operation is 1 we get tweets from following and user's tweets;
        * if operation is 2 we get tweets from a specific user that user has clicked to view;
         */




        String url = "https://penguinsocialappdemo.000webhostapp.com/listPost.php?user_id="+user_id+"&startFrom="+startFrom+
                "&operationType="+operationType;
        try {

            DoInBackground doInBackground = new DoInBackground();
            doInBackground.execute(url);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    //thread to do in background;
    private class DoInBackground extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            HttpURLConnection httpURLConnection;
            URL url;

            try{

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(7000);
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader in = new InputStreamReader(inputStream);

                int data = in.read();

                while (data != -1){

                    char i = (char) data;
                    result += i;
                    data = in.read();
                }
                inputStream.close();
                publishProgress(result);;

            }catch (Exception ex){
                ex.printStackTrace();
            }

            return " ";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {

                JSONObject jsonObject = new JSONObject(values[0]);
                String msg = jsonObject.getString("msg");

                if (msg.equals("has tweets")){

                    JSONArray user_tweets = new JSONArray(jsonObject.getString("user_tweets"));
                    listOfTweets.clear();
                    for (int i = 0;i < user_tweets.length(); i++){

                        JSONObject individual_tweets = user_tweets.getJSONObject(i);
                        listOfTweets.add(new Tweets(individual_tweets.getString("picture_path"),
                                individual_tweets.getString("username"),
                                individual_tweets.getString("tweet_date"),
                                individual_tweets.getString("tweet_text"),
                                individual_tweets.getString("tweetImage_path")));
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
