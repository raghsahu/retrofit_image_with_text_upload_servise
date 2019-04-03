package net.simplifiedcoding.retrofitexample.api;

import net.simplifiedcoding.retrofitexample.models.MessageResponse;
import net.simplifiedcoding.retrofitexample.models.Messages;
import net.simplifiedcoding.retrofitexample.models.Result;
import net.simplifiedcoding.retrofitexample.models.User;
import net.simplifiedcoding.retrofitexample.models.Users;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by Belal on 14/04/17.
 */

public interface APIService {

    @FormUrlEncoded
    @POST("register")
    Call<Result> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("gender") String gender);


//    @FormUrlEncoded
//    @POST("login")
//    Call<Result> userLogin(
//            @Field("email") String email,
//            @Field("password") String password
//    );

//    @Multipart
//    @POST("posts")
//    Call<Result> userLogin(
//            @Part("Share_update_post") RequestBody email,
//            @Part("file") RequestBody reqBody,
//            @Part("id") RequestBody id);

//    @Multipart
//    @POST("posts")
//    Call<Result> userLogin(@Part ("id") RequestBody id,
//                          @Part ("Share_update_post")RequestBody email,
//                          @Part MultipartBody.Part body);


    @Multipart
    @POST("Ragistration")
    Call<Result> userLogin(@Part("email") RequestBody email,
                           @Part("name") RequestBody name,
                           @Part("mobile") RequestBody mobile,
                           @Part("password") RequestBody password,
                           @Part ("passout_year")RequestBody passout_year,
                           @Part("collage_name") RequestBody collage_name,
                           @Part ("address")RequestBody address,
                           @Part MultipartBody.Part body,
                           @Part MultipartBody.Part body1);


    //*******************************************************
    @GET("users")
    Call<Users> getUsers();

    @FormUrlEncoded
    @POST("sendmessage")
    Call<MessageResponse> sendMessage(
            @Field("from") int from,
            @Field("to") int to,
            @Field("title") String title,
            @Field("message") String message);

    @FormUrlEncoded
    @POST("update/{id}")
    Call<Result> updateUser(
            @Path("id") int id,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("gender") String gender
    );

    //getting messages
    @GET("messages/{id}")
    Call<Messages> getMessages(@Path("id") int id);
}
