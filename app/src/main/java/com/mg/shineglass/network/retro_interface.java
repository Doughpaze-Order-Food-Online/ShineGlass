package com.mg.shineglass.network;



import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;

import org.json.JSONObject;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;


public interface retro_interface {

    @GET("banner")
    Observable<BasicResponse> IMAGES();

    @POST("auth/register")
    Observable<BasicResponse> register();

    @POST("auth/register_otp")
    Observable<BasicResponse> REGISTER_OTP(@Body User user);

    @POST("auth/login")
    Observable<Response<LoginResponse>> LOGIN(@Body User user);


    @POST("auth/number_login")
    Observable<Response<LoginResponse>> NUMBER_LOGIN(@Body User user);


}
