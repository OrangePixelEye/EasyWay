package com.bento.easyway;

public class Worked {

    String worked_date;
    String worked_time;
    String user;

    Worked(){}

    Worked(String date, String time, String user){
        this.worked_date = date;
        this.worked_time = time;
        this.user = user;
    }

    public String getWorked_date() {
        return worked_date;
    }

    public String getWorked_time() {
        return worked_time;
    }

    public String getUser() {
        return user;
    }
}
