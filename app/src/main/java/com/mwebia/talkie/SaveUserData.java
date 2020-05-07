package com.mwebia.talkie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

class SaveUserData {

    private SharedPreferences sharedPreferences;
    Context context;
    SaveUserData(Context context){

        this.context = context;
        sharedPreferences = context.getSharedPreferences("myRef",Context.MODE_PRIVATE);
    }

    void saveUserId(int user_id){
        //save user_id to shared preferences;
        sharedPreferences.edit().putInt("user_id", user_id).apply();

        //check if id is saved;
        int saved_id = loadUserId();
        if (saved_id == 0) {
            Intent intent = new Intent(context,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    int loadUserId(){

        return sharedPreferences.getInt("user_id",0);

    }

    void deleteUserId(){

        sharedPreferences.edit().remove("user_id").apply();
    }
}
