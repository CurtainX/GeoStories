package com.example.shengx.geostories;

/**
 * Created by SHENG.X on 2018-03-22.
 */

public class Geostory {
    String profileImgUrl;
    String username;
    String datePosted;
    String geostory;
    String geostoryImageUrl;

    public Geostory(String username, String datePosted, String geostory) {
        this.username = username;
        this.datePosted = datePosted;
        this.geostory = geostory;
    }

    public Geostory(String profileImgUrl, String username, String datePosted, String geostory, String geostoryImageUrl) {

        this.profileImgUrl = profileImgUrl;
        this.username = username;
        this.datePosted = datePosted;
        this.geostory = geostory;
        this.geostoryImageUrl = geostoryImageUrl;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getGeostory() {
        return geostory;
    }

    public void setGeostory(String geostory) {
        this.geostory = geostory;
    }

    public String getGeostoryImageUrl() {
        return geostoryImageUrl;
    }

    public void setGeostoryImageUrl(String geostoryImageUrl) {
        this.geostoryImageUrl = geostoryImageUrl;
    }
}
