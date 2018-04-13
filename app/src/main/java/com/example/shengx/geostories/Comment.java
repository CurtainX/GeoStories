package com.example.shengx.geostories;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by SHENG.X on 2018-04-12.
 */

public class Comment {
    String clientID;
    String storyID;
    String commnetContent;
    String commentDate;
    String clientName;
    public Comment(String clientID, String clientName, String storyID, String commnetContent, String commentDate) {
        this.clientID = clientID;
        this.clientName=clientName;
        this.storyID = storyID;
        this.commnetContent = commnetContent;
        this.commentDate = commentDate;
    }


    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getStoryID() {
        return storyID;
    }

    public void setStoryID(String storyID) {
        this.storyID = storyID;
    }

    public String getCommnetContent() {
        return commnetContent;
    }

    public void setCommnetContent(String commnetContent) {
        this.commnetContent = commnetContent;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
