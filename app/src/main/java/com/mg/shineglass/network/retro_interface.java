package com.mg.shineglass.network;



import com.mg.shineglass.models.Address;
import com.mg.shineglass.models.Banners;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.Category;
import com.mg.shineglass.models.City;
import com.mg.shineglass.models.FinalOrder;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.MyOrders;
import com.mg.shineglass.models.MyQuotation;
import com.mg.shineglass.models.OrderDeliver;
import com.mg.shineglass.models.PaymentDetails;
import com.mg.shineglass.models.Quotation;
import com.mg.shineglass.models.Rates;
import com.mg.shineglass.models.User;
import com.mg.shineglass.models.Wallet;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("city")
    Observable<Response<List<City>>> GET_CITY();

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

    @POST("auth/reset_password")
    Observable<BasicResponse> RESET_PASSWORD(@Body User user);

    @POST("auth/save_profile")
    Observable<LoginResponse> SAVE_PROFILE_DETAILS(@Body User user);

    @Multipart
    @POST("quotation")
    Observable<Response<LoginResponse>> REQUEST_QUOTATION(@Part List<MultipartBody.Part> files, @Part("quotation") ArrayList<Quotation> quotation, @Query("mobile") String mobile,@Part("city") String city);

    @GET("quotation")
    Observable<List<MyQuotation>> GET_QUOTATION();

    @GET("address")
    Observable<BasicResponse> GET_ADDRESS();

    @DELETE("address")
    Observable<BasicResponse> DELETE_ADDRESS(@Query("id")  String id);

    @POST("address")
    Observable<BasicResponse> SAVE_ADDRESS(@Body  Address address);

    @POST("reject")
    Observable<BasicResponse> REJECT_QUOTATION(@Query("quotation") String Quotation);

    @Multipart
    @POST("order")
    Observable<Integer> PLACE_OFFLINE_ORDER(@Part MultipartBody.Part payment, @Part("QuotationNo") String QuotationNo,@Part("address") Address address,@Part("tid") String tid);

    @Multipart
    @POST("order")
    Observable<Integer> PLACE_OFFLINE_ORDER(@Part("QuotationNo") String QuotationNo,@Part("address") Address address,@Part("tid") String tid);

    @POST("online_order")
    Observable<Integer> PLACE_ONLINE_ORDER(@Query("mid") String mid, @Query("orderNo") String Quotation);


    @GET("wallet")
    Observable<Wallet> GET_WALLET();


    @GET("order")
    Observable<List<MyOrders>> GET_ORDERS();

    @POST("token")
    Observable<BasicResponse> GET_TOKEN(@Query("mid") String mid,@Body FinalOrder finalOrder);


    @GET("current")
    Observable<List<MyOrders>> GET_CURRENT_ORDERS();

    @GET("previous")
    Observable<List<MyOrders>> GET_PREVIOUS_ORDERS();

    @POST("verify_otp")
    Observable<Integer> VERIFY_OTP(@Body OrderDeliver orderDeliver);

    @POST("auth/forgot_password_otp")
    Observable<LoginResponse> FORGOT_PASSWORD_OTP(@Body User user);

    @POST("auth/forgot_password")
    Observable<BasicResponse> FORGOT_PASSWORD(@Body User user);





}
