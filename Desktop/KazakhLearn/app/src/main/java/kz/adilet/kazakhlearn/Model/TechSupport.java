package kz.adilet.kazakhlearn.Model;

public class TechSupport {
    private String heading, desc, date, userEmail, answer, uid;

    public TechSupport(){

    }

    public TechSupport(String heading, String desc, String date, String userEmail, String answer, String uid) {
        this.heading = heading;
        this.desc = desc;
        this.date = date;
        this.userEmail = userEmail;
        this.answer = answer;
        this.uid = uid;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
