package com.bento.easyway;

public class Worked {

    String worked_day;
    String worked_year;
    String worked_month;
    String worked_time;
    String user;

    Worked(){}

    Worked(String day,String month, String year, String time, String user){
        this.worked_day = day;
        this.worked_month = month;
        this.worked_year = year;
        this.worked_time = time;
        this.user = user;
    }

    public String getWorked_day() {
        return worked_day;
    }

    public String getWorked_time() {
        return worked_time;
    }

    public String getUser() {
        return user;
    }

    public String getWorked_year() {
        return worked_year;
    }

    public String getWorked_month() {
        return worked_month;
    }
}
