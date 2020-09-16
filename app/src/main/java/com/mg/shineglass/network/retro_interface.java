package com.mg.shineglass.network;



import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.Qoutation;
import com.mg.shineglass.models.Rates;
import com.mg.shineglass.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;


public interface retro_interface {

    @GET("rates")
    Observable<Response<List<Rates>>> GET_RATES();

    @GET("category")
    Observable<Response<Category>> GET_CATEGORY();

    @GET("banner")
    Observable<Response<List<Banners>>> GET_IMAGES();

    @POST("auth/register")
    Observable<Response<LoginResponse>> REGISTER(@Body User user);

    @POST("auth/register_otp")
    Observable<Response<BasicResponse>> REGISTER_OTP(@Body User user);

    @POST("auth/login")
    Observable<Response<LoginResponse>> LOGIN(@Body User user);


    @POST("auth/number_login")
    Observable<Response<LoginResponse>> NUMBER_LOGIN(@Body User user);

    @Multipart
    @POST("quotation")
    Observable<Response<LoginResponse>> REQUEST_QUOTATION(@Part List<MultipartBody.Part> files,@Part("quotation") List<Qoutation> quotation);


}
