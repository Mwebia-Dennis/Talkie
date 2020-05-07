package com.mwebia.talkie;

public class OtherUser {

    private String new_userProf,new_userName;
    OtherUser(String new_userProf,String new_userName) {
        this.new_userName = new_userName;
        this.new_userProf = new_userProf;
    }

    public String getUserName() {
        return this.new_userName;
    }

    public String getNew_userProf() {
        return  this.new_userProf;
    }
}
