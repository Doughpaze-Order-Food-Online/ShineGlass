package com.mg.shineglass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mg.shineglass.models.BasicResponse;
import com.mg.shineglass.models.LoginResponse;
import com.mg.shineglass.models.User;
import com.mg.shineglass.network.networkUtils;
import com.mg.shineglass.utils.ViewDialog;
import com.mg.shineglass.utils.constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginSignUpActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";
    private ViewDialog viewDialog;


    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN = 9001;
    private CompositeSubscription mSubscriptions;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);



        mSubscriptions = new CompositeSubscription();

        RelativeLayout login = findViewById(R.id.login_btn);
        Button otp_login = findViewById(R.id.login_with_otp_btn);
        TextView sign_up = findViewById(R.id.signup_txt);

        RelativeLayout google=findViewById(R.id.login_with_google_btn);
        RelativeLayout facebook=findViewById(R.id.login_with_facebook_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginSignUpActivity.this,Login_Activity.class);
                startActivity(i);
            }
        });

        otp_login.setOnClickListener(v -> {
            Intent i=new Intent(LoginSignUpActivity.this,Otp_Login_Activity.class);
            startActivity(i);
        });

        sign_up.setOnClickListener(v -> {
            Intent i=new Intent(LoginSignUpActivity.this,Register_Activity.class);
            startActivity(i);
        });

        facebook.setOnClickListener(view->{
            viewDialog.showDialog();
            FACEBOOK();
        });
        google.setOnClickListener(view->{
            viewDialog.showDialog();
            GOOGLE();
        });

        //google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginSignUpActivity.this, gso);

        setUpFacebookCallBack();

        viewDialog = new ViewDialog(this);


    }



    private void GOOGLE()
    {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
        viewDialog.hideDialog();

    }

    private void FACEBOOK()
    {



        LoginManager.getInstance().logInWithReadPermissions(LoginSignUpActivity.this, Collections.singleton("email"));

        LoginManager.getInstance().setAuthType(AUTH_TYPE);
        // Register a callback to respond to the user

        viewDialog.hideDialog();
    }


    private void setUpFacebookCallBack(){

        callbackManager =CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        FACEBOOK_LOGIN(loginResult.getAccessToken().getToken(),loginResult.getAccessToken().getUserId());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginSignUpActivity.this, "Facebook Login Failed", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(FacebookException e) {
                        // Handle exception
                        Toast.makeText(LoginSignUpActivity.this, "Facebook Login Failed", Toast.LENGTH_LONG).show();


                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }




    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Google_Login(idToken);

            // TODO(developer): send ID Token to server and validate

            //updateUI(account);
        } catch (ApiException e) {
            Log.w("TAG", "handleSignInResult:error", e);
           // progressDialog.dismiss();
            //updateUI(null);
        }
    }

    private void Google_Login(String idToken){
       User user=new User();
       user.setIdToken(idToken);
       viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit().GOOGLE_LOGIN(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private  void FACEBOOK_LOGIN(String token,String userid) {

        User user=new User();
        user.setAccessToken(token);
        user.setUserID(userid);

        viewDialog.showDialog();
        mSubscriptions.add(networkUtils.getRetrofit().FACEBOOK_LOGIN(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(LoginResponse response) {

        viewDialog.hideDialog();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if(response.getUser().getMobile().equals("-1"))
        {
            Intent intent=new Intent(LoginSignUpActivity.this,Google_Enter_Number.class);
            intent.putExtra("type",response.getType());
            intent.putExtra("token",response.getToken());
            intent.putExtra("email",response.getUser().getEmail());
            intent.putExtra("name",response.getUser().getUsername());

            startActivity(intent);
            finish();

        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("type",response.getType());

            editor.putString(constants.TOKEN,response.getToken());
            editor.putString(constants.EMAIL,response.getUser().getEmail());
            editor.putString(constants.USERNAME,response.getUser().getUsername());
            editor.putString(constants.PHONE,response.getUser().getMobile());
            editor.putString(constants.USER_TYPE,response.getUser().getUserType());
            editor.apply();
            Toast.makeText(this, "sign in success!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginSignUpActivity.this,response.getUser().getUserType().equals("user")?MainActivity.class:DeliveryBoyMainActivity.class);
            startActivity(intent);
            finish();
        }





    }

    private void handleError(Throwable error) {


        viewDialog.hideDialog();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                BasicResponse response = gson.fromJson(errorBody, BasicResponse.class);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHome() {
        Intent intent = new Intent(LoginSignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent i=new Intent(LoginSignUpActivity.this,MainActivity.class);
//        startActivity(i);
        finish();
    }




}