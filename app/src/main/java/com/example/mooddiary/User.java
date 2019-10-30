package com.example.mooddiary;

import java.util.ArrayList;

/**
 * user class can keep track of user's information
 *
 */


public class User {
    private String username;
    private ArrayList<String> friends;

    public User(String username){
        this.username = username;
        friends = new ArrayList<String>();
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(){
        this.username = username;
    }

    public ArrayList<String> getFriends(){
        return this.friends;
    }

    public void setFriends(ArrayList<String> friends){
        this.friends = friends;
    }

    public void addFriends(String username){
        if(!this.friends.contains(username)){
            this.friends.add(username);
        }
    }

    public void removeFriends(String username){
        this.friends.remove(username);
    }

}