package com.mg.shineglass.network;



import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.Quotation;
import com.mg.shineglass.models.Rates;
import com.mg.shineglass.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;


public interface retro_interface {

    @GET("rates")
    Observable<Response<List<Rates>>> GET_RATES();

    @GET("category")
    Observable<List<Category>> GET_CATEGORY();

    @GET("banner")
    Observable<Response<List<Banners>>> GET_IMAGES();

    @POST("auth/register")
    Observable<LoginResponse> REGISTER(@Body User user);

    @POST("auth/register_otp")
    Observable<BasicResponse> REGISTER_OTP(@Body User user);

    @POST("auth/login")
    Observable<LoginResponse> LOGIN(@Body User user);


    @POST("auth/google")
    Observable<LoginResponse> GOOGLE_LOGIN(@Body User user);

    @POST("auth/facebook")
    Observable<LoginResponse> FACEBOOK_LOGIN(@Body User user);

    @POST("auth/google_otp")
    Observable<LoginResponse> GOOGLE_FACEBOOK_OTP(@Body User user);

    @POST("auth/number_save")
    Observable<LoginResponse> NUMBER_SAVE(@Body User user);


    @POST("auth/number_login")
    Observable<LoginResponse> NUMBER_LOGIN(@Body User user);

    @Multipart
    @POST("quotation")
    Observable<Response<LoginResponse>> REQUEST_QUOTATION(@Part List<MultipartBody.Part> files, @Part("quotation") List<Quotation> quotation, @Query("mobile") String mobile);


}
