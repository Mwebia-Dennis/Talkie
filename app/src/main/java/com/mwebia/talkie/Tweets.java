package com.mwebia.talkie;

class Tweets {

    String userProfile_img;
    String username;
    String dateOfPost;
    String tweetText;
    String tweet_img;

    Tweets(String userProfile_img, String username, String dateOfPost, String tweetText, String tweet_img) {

        this.userProfile_img = userProfile_img;
        this.username = username;
        this.dateOfPost = dateOfPost;
        this.tweetText = tweetText;
        this.tweet_img = tweet_img;
    }
}
