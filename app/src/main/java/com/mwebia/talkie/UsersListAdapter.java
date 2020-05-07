package com.mwebia.talkie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//creating adapter to add layout tickets to our list view
public class UsersListAdapter extends BaseAdapter {

    private ArrayList<Tweets> tweetsList;
    private Context context;

    UsersListAdapter(Context context, ArrayList<Tweets> tweetsList){
        super();

        this.context = context;
        this.tweetsList = tweetsList;
    }

    @Override
    public int getCount() {
        return tweetsList.size();
    }

    @Override
    public Object getItem(int position) {
        return tweetsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tweets_ticket, parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Todo:add event listeners for like and retweets and user_profile;
        viewHolder.profile_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,FollowingPageActivity.class);
                context.startActivity(i);
            }
        });
        viewHolder.retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //allow user to retweet
            }
        });

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //allow users to like an image;

            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //allow user to comment on a message;
            }
        });

        Tweets tweets = tweetsList.get(position);
        try {

            Picasso.get().load(tweets.userProfile_img).fit().into(viewHolder.userProfile);
            Picasso.get().load(tweets.tweet_img).fit().into(viewHolder.postImage);
            viewHolder.nameTextView.setText(tweets.username);
            viewHolder.dateTextView.setText(tweets.dateOfPost);
            viewHolder.postText.setText(tweets.tweetText);

        }catch (Exception ex) {

            ex.printStackTrace();

        }

        return convertView;
    }


}
