package com.mwebia.talkie;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class ViewHolder {
     ImageView userProfile;
     TextView nameTextView;
     TextView dateTextView;
     TextView postText;
    ImageView postImage;
    ImageView like;
    ImageView retweet;
    ImageView comment;
    LinearLayout profile_linearLayout;

    ViewHolder(View view){

         userProfile = view.findViewById(R.id.tweet_userProfile);
         nameTextView = view.findViewById(R.id.name);
         postImage = view.findViewById(R.id.post_image);
         like = view.findViewById(R.id.like);
         retweet = view.findViewById(R.id.retweet);
         comment = view.findViewById(R.id.comment);
         dateTextView = view.findViewById(R.id.tweet_post_date);
         postText = view.findViewById(R.id.post_text);
        profile_linearLayout = view.findViewById(R.id.profile_linearLayout);

    }
}
