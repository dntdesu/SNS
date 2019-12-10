package jp.ac.ecc.sk3a17.sns.Model;

public class Posts {
    public String uid, time, date, description, postImage, profileImage, fullName;

    public Posts() {

    }

    public Posts(String uid, String time, String date, String description, String postImage, String profileImage, String fullName, String postCounter) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.description = description;
        this.postImage = postImage;
        this.profileImage = profileImage;
        this.fullName = fullName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
