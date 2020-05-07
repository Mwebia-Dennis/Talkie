package com.mwebia.talkie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class FollowingPageActivity extends AppCompatActivity {

    /*
    *we get all users that user follows;
    * we will also display all users that user has not followed;
     */
    ArrayList<OtherUser> listOfNewUsers,listOfFolllowedUsers;
    ListView listView;
    NewUserAdapter adapter;
    FollowingListAdapter followingListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_page);

        //setting our toolbar;
        Toolbar toolbar = findViewById(R.id.followingToolbar);
        setSupportActionBar(toolbar);

        //initialising variables;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        listOfNewUsers = new ArrayList<>();
        adapter = new NewUserAdapter(listOfNewUsers);

        //making our recyclerviewer display in horizontal manner;
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //setting adapter;
        recyclerView.setAdapter(adapter);

        //setting adapter to our listview;

        listView = findViewById(R.id.following_listView);
        listOfFolllowedUsers = new ArrayList<>();
        followingListAdapter = new FollowingListAdapter(this,listOfFolllowedUsers);
        listView.setAdapter(followingListAdapter);

        prepareNewUserData();
        prepareListviewdata();

    }

    private void prepareNewUserData() {
        //we will get all unfollowed users info from db and store in list view for displaying;
        listOfNewUsers.clear();
        listOfNewUsers.add(new OtherUser("images/img.jpeg","Denoh"));
        listOfNewUsers.add(new OtherUser("images/img.jpeg","Mark"));
        listOfNewUsers.add(new OtherUser("images/img.jpeg","Irene"));
        adapter.notifyDataSetChanged();

    }

    private void prepareListviewdata() {

        listOfFolllowedUsers.clear();
        listOfFolllowedUsers.add(new OtherUser("images/img.jpeg","denoh"));
        listOfFolllowedUsers.add(new OtherUser("images/img.jpeg","Mark"));
        listOfFolllowedUsers.add(new OtherUser("images/img.jpeg","Irene"));
        followingListAdapter.notifyDataSetChanged();
    }

    public static class FollowingListAdapter extends BaseAdapter {

        private ArrayList<OtherUser> userList;
        private Context context;

        FollowingListAdapter(Context context, ArrayList<OtherUser> userList){
            super();

            this.context = context;
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.users_ticket, parent,false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }else {
               holder = (Holder) convertView.getTag();
            }

            OtherUser otherUser= userList.get(position);
            try{

                Picasso.get().load(otherUser.getNew_userProf()).into(holder.userProf);
                holder.followedUser_name.setText(otherUser.getUserName());

            }catch (Exception ex){

                ex.printStackTrace();

            }

            return convertView;
        }

        class Holder{
            ImageView userProf;
            Button follow;
            TextView followedUser_name;

            Holder(View view) {
                followedUser_name = view.findViewById(R.id.user_name_ticket);
                follow = view.findViewById(R.id.follow);
                userProf = view.findViewById(R.id.user_prof);
            }
        }
    }

}
