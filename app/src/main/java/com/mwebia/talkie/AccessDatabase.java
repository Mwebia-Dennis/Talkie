package com.mwebia.talkie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccessDatabase extends AsyncTask<String,String,String> {

    private Activity activity;

    AccessDatabase(Activity activity) {

        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection httpURLConnection;
        String result = "";

        try {

            url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(7000);
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            int data = inputStreamReader.read();

            while (data != -1) {

                char character = (char) data;
                result += character;
                data = inputStreamReader.read();
            }
            inputStream.close();
            //publishProgress(result);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {

            JSONObject json = new JSONObject(s);
            Toast.makeText(activity.getApplicationContext(), json.getString("msg"), Toast.LENGTH_SHORT).show();

            //code for sign up
            if (json.getString("msg").equals("successful sign up")) {

                activity.finish();
            }

            //failed sign up
            if (json.getString("msg").equals("failed signup")) {

                activity.findViewById(R.id.signUp_progressbar).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.signUp).setEnabled(true);
            }

            //code for login
            if (json.getString("msg").equals("successful login")) {


                String userInfo = json.getString("user_info");
                try {
                    JSONArray jsonArray = new JSONArray(userInfo);
                    JSONObject user_data = jsonArray.getJSONObject(0);
                    //"id":"8","username":"suzie","email":"suzie@suzie.com","password":"098765","location":"meru",
                    // "picture_path":"https:\/\/firebasestorage.googleapis.com\/v0\/b\/twitter-d155e.appspot.com\/o\/images\/1588433467273.png?alt=media"
                    String user_id = user_data.getString("id");
                    new SaveUserData(activity).saveUserId(Integer.parseInt(user_id));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity.getApplicationContext(),HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
            //failed sign in
            if (json.getString("msg").equals("failed login")) {

                activity.findViewById(R.id.signin_progressbar).setVisibility(View.INVISIBLE);
                activity.findViewById(R.id.signIn).setEnabled(true);
            }

            //code for posting a new tweet updating ui as required;

            if (json.getString("msg").equals("tweet posted")) {
                openHome();
            }

            //code for following and unfollowing users;
            //checking if the user has successfully followed then send true boolean since we are following;
            if(json.getString("msg").equals("followed")) {

                follow_unFollow(true);
            }

            //unfollowing the user we send a false boolean meaning we are unfollowing
            if(json.getString("msg").equals("unfollowed")) {

                follow_unFollow(false);
            }


        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private void openHome() {

        Intent i = new Intent(activity.getApplicationContext(),HomePageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
    }

    void follow_unFollow (Boolean isfollowing) {

        /*
        *we check if the user is following so as we can update ui as needed;
         */
        //todo:change follow button to unfollow and vice versa;
        if (isfollowing) {


        } else {

        }
    }
}

