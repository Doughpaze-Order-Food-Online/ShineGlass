package com.mg.shineglass.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mg.shineglass.My_Address_Activity;
import com.mg.shineglass.Quotation_Activity;
import com.mg.shineglass.R;
import com.mg.shineglass.models.MyQuotation;
import com.mg.shineglass.models.Wallet;

import java.text.SimpleDateFormat;
import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletItemHolder >  {

    private List<Wallet.Transaction> list;

    public  WalletAdapter(List<Wallet.Transaction> list) {
        this.list=list;
    }


    @NonNull
    @Override
    public  WalletAdapter.WalletItemHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new  WalletAdapter.WalletItemHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAdapter.WalletItemHolder  walletItemHolder, int i) {
        Wallet.Transaction transaction=list.get(i);

        if(transaction.getType().equals("credit"))
        {
            walletItemHolder.text.setText("Amount Credited to Wallet");
            walletItemHolder.amount.setText("+ "+transaction.getAmount().toString());
            walletItemHolder.amount.setTextColor(Color.rgb(0,128,0));
        }
        else {
            walletItemHolder.text.setText("Amount Debited from Wallet");
            walletItemHolder.amount.setText("+ "+transaction.getAmount().toString());
            walletItemHolder.amount.setTextColor(Color.rgb(128,0,0));
        }
        walletItemHolder.total.setText(transaction.getTotal().toString());
    }

    @Override
    public int getItemCount() {
        if(list!=null)
        {
            return list.size();
        }
        return 0;

    }

    class  WalletItemHolder extends RecyclerView.ViewHolder {

        TextView text,amount,total;



        WalletItemHolder (View itemView) {
            super(itemView);
           text=itemView.findViewById(R.id.transaction_id_value);
           amount=itemView.findViewById(R.id.previous_amount_value);
           total=itemView.findViewById(R.id.updated_amount_value);

        }
    }












}

