package jp.ac.ecc.sk3a17.sns.Model;

public class Comments {
    public String comment, date, time, uid, userName;

    public Comments() {

    }

    public Comments(String comment, String date, String time, String uid, String userName) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
