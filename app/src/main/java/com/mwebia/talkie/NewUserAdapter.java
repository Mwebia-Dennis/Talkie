package com.mwebia.talkie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class NewUserAdapter extends RecyclerView.Adapter<NewUserAdapter.MyViewHolder> {

    private ArrayList<OtherUser> listOfNewUsers;

    static class  MyViewHolder extends RecyclerView.ViewHolder{

        ImageView new_userProf;
        Button follow;
        TextView new_userName;
        MyViewHolder(View view) {
            super(view);
            new_userName = view.findViewById(R.id.new_user_name);
            follow = view.findViewById(R.id.new_user_follow);
            new_userProf = view.findViewById(R.id.new_user_prof);
        }
    }

    NewUserAdapter(ArrayList<OtherUser> listOfNewUsers) {
        this.listOfNewUsers = listOfNewUsers;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_user_ticket,parent,false);
        return new MyViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        OtherUser otherUser = listOfNewUsers.get(position);
        try{

            Picasso.get().load(otherUser.getNew_userProf()).into(holder.new_userProf);
            holder.new_userName.setText(otherUser.getUserName());

        }catch (Exception ex) {

            ex.printStackTrace();

        }
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:follow or unfollow user;


            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfNewUsers.size();
    }
}
