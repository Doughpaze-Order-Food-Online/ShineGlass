package com.mg.shineglass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mg.shineglass.OTP_Acitvities.DeliveryBoyOtp_Activity;
import com.mg.shineglass.models.MyOrders;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DeliveryOrderDetails extends Activity {

    private TextView orderId,customer,phone,status,order_date,payment_mode,address,direction;
    private RelativeLayout otp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_order_details);

        Intent intent=getIntent();

        orderId=findViewById(R.id.order_id_value_txt);
        direction=findViewById(R.id.get_directions_btn);
        address=findViewById(R.id.delivery_value_txt);
        payment_mode=findViewById(R.id.mode_value_txt);
        order_date=findViewById(R.id.date_value_txt);
        status=findViewById(R.id.status_value_txt);
        phone=findViewById(R.id.contact_value_txt);
        customer=findViewById(R.id.name_value_txt);
        otp=findViewById(R.id.send_otp_btn);

        Gson gson=new Gson();
        MyOrders orders =new MyOrders();
        Type type=new TypeToken<MyOrders>(){}.getType();
        orders=gson.fromJson(intent.getStringExtra("order"),type);

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");

        orderId.setText(orders.getOrderNo());
        address.setText(orders.getAddress().getAddress());
        payment_mode.setText(orders.getPayment_mode());
        order_date.setText(simpleDateFormat.format(orders.getOrder_date()));
        status.setText(orders.getStatus());
        phone.setText(orders.getUser().getMobile());
        customer.setText(orders.getUser().getUsername());

        MyOrders finalOrders = orders;
        direction.setOnClickListener(v -> {
            Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q="+ finalOrders.getAddress().getAddress()));
            startActivity(i);
        });

        MyOrders finalOrders1 = orders;
        otp.setOnClickListener(v -> {
       Intent intent1=new Intent(DeliveryOrderDetails.this, DeliveryBoyOtp_Activity.class);
       intent1.putExtra("orderId", finalOrders1.getOrderNo());
       startActivity(intent1);
    });



    }
}
