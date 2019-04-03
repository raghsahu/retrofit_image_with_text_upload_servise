package net.simplifiedcoding.retrofitexample.models;

import com.google.gson.annotations.SerializedName;

public class Result {


//    @SerializedName("img")
//    private String img;

    @SerializedName("responce")
    private String responce;

    @SerializedName("massage")
    private Massage massage;

    @SerializedName("error")
    private String error;


    public Result(String responce,String error,Massage massage) {
        this.responce = responce;
        this.massage=massage;
        this.error=error;

    }

//    public Massage getdata() {
//       return massage;
//    }
//    public Result(String img) {
//        this.img = img;
//
//    }

//    public String getImg() {
//        return img;
//    }

    public String getResponce() {
        return responce;
    }

    public Object getMassage() {
        return massage;
    }

    public String getError() {
        return error;
    }


    public class Massage{

    }
}
