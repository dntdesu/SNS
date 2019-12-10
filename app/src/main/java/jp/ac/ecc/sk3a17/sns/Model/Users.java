package jp.ac.ecc.sk3a17.sns.Model;

public class Users {
    public String fullName, userName, profileImage, uid;

    public Users() {

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Users(String fullName, String userName, String profileImage, String uid) {

        this.fullName = fullName;
        this.userName = userName;
        this.profileImage = profileImage;
        this.uid = uid;
    }
}
