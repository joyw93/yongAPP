package com.example.hklist.Model;

public class CalendarDTO {

    private String content;
    private String key;
    private String str_date;
    private long dDay;

    public CalendarDTO(){}
    public CalendarDTO( String content,String key,String str_date) {
        this.content = content;
        this.key=key;
        this.str_date=str_date;
    }

    public long getdDay() {
        return dDay;
    }

    public void setdDay(long dDay) {
        this.dDay = dDay;
    }

    public String getStr_date() {
        return str_date;
    }

    public void setStr_date(String str_date) {
        this.str_date = str_date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
