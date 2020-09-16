package com.mg.shineglass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

public class NewRequestActivity  extends Fragment {
    private String category,subcategory;
    private TextView type;
    private RecyclerView rvItem;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextInputLayout thickness,width,height,quantity;

    private EditText Ethickness,Ewidth,Eheight,Equantity;

    public NewRequestActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.category_description_fragment, container, false);
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            subcategory= bundle.getString("subcategory");
            category=bundle.getString("category");
        }

        type=view.findViewById(R.id.item_name_txt);
        String s=category+"\n"+subcategory;
        type.setText(s);

        thickness= view.findViewById(R.id.thickness);
        width=view.findViewById(R.id.width);
        height=view.findViewById(R.id.height);
        quantity=view.findViewById(R.id.quantity);


        Ethickness= view.findViewById(R.id.thickness_edt);
        Ewidth=view.findViewById(R.id.width_edt);
        Eheight=view.findViewById(R.id.height_edt);
        Equantity=view.findViewById(R.id.quantity_edt);





        return view;

    }

}
