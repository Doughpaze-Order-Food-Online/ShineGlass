package com.mg.shineglass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mg.shineglass.Interface.DeleteCartItem;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Quotation;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemHolder>  {

    private List<Quotation> list;
    private DeleteCartItem deleteCartItem;

    public CartAdapter(List<Quotation> list,DeleteCartItem deleteCartItem) {
        this.list=list;
        this.deleteCartItem=deleteCartItem;

    }


    @NonNull
    @Override
    public CartAdapter.CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartAdapter.CartItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartItemHolder CartItemHolder, int i) {
        Quotation quotations=list.get(i);
        CartItemHolder.thickness.setText(quotations.getThickness());
        CartItemHolder.width.setText(quotations.getWidth());
        CartItemHolder.category.setText(quotations.getCategory());
        if(quotations.getSubcategory()!=null){CartItemHolder.subcategory.setText(quotations.getSubcategory());} else{CartItemHolder.subcategory.setText("-");}
        CartItemHolder.height.setText(quotations.getHeight());
        CartItemHolder.quantity.setText(quotations.getQuantity());
        CartItemHolder.scale.setText(quotations.getScale());

        CartItemHolder.remove.setOnClickListener(v -> {
                deleteCartItem.removeCartItem(i);
        });



    }

    @Override
    public int getItemCount() {
        if(list!=null)
        {
            return list.size();
        }
        return 0;

    }

    class CartItemHolder extends RecyclerView.ViewHolder {
        private TextView category,thickness,width,height,quantity,subcategory,scale;
      private Button remove;

        CartItemHolder (View itemView) {
            super(itemView);
            category=itemView.findViewById(R.id.category);
            subcategory=itemView.findViewById(R.id.subcategory);
            thickness=itemView.findViewById(R.id.thickness);
            width=itemView.findViewById(R.id.width);
            height=itemView.findViewById(R.id.height);
            quantity=itemView.findViewById(R.id.quantity);
            remove=itemView.findViewById(R.id.remove_btn);
            scale=itemView.findViewById(R.id.scale);

        }
    }












}

