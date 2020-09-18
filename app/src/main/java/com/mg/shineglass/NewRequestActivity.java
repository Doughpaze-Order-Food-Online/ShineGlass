package com.mg.shineglass;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class NewRequestActivity  extends Fragment implements NumberPicker.OnValueChangeListener {
    private String category,subcategory;
    private TextView type;
    private RecyclerView rvItem;
    private RadioGroup radioGroup;
    private RadioButton MmRadioButton;
    private RadioButton InchRadioButton;
    private TextInputLayout thickness,width,height,quantity;

    private EditText Ethickness,Ewidth,Eheight,Equantity;

    private String[] floatNum;

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

        MmRadioButton=view.findViewById(R.id.mm_radio_btn);
        InchRadioButton=view.findViewById(R.id.inch_radio_btn);

        thickness= view.findViewById(R.id.thickness);
        width=view.findViewById(R.id.width);
        height=view.findViewById(R.id.height);
        quantity=view.findViewById(R.id.quantity);


        Ethickness= view.findViewById(R.id.thickness_edt);
        Ewidth=view.findViewById(R.id.width_edt);
        Eheight=view.findViewById(R.id.height_edt);
        Equantity=view.findViewById(R.id.quantity_edt);

        floatNum = new String[]{"0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10"};

        Ethickness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MmRadioButton.isChecked())
                show(Ethickness, MmRadioButton);
                else if(InchRadioButton.isChecked()){
                    show(Ethickness,InchRadioButton);
                }
                else{
                    Toast.makeText(getContext(), "Please select the measurement type in INCH OR MM", Toast.LENGTH_LONG).show();
                }
            }
        });

        Ewidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MmRadioButton.isChecked())
                    show(Ewidth, MmRadioButton);
                else if(InchRadioButton.isChecked()){
                    show(Ewidth,InchRadioButton);
                }
                else{
                    Toast.makeText(getContext(), "Please select the measurement type in INCH OR MM", Toast.LENGTH_LONG).show();
                }
            }
        });

        Eheight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MmRadioButton.isChecked())
                    show(Eheight, MmRadioButton);
                else if(InchRadioButton.isChecked()){
                    show(Eheight,InchRadioButton);
                }
                else{
                    Toast.makeText(getContext(), "Please select the measurement type in INCH or MM", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;

    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public void show(EditText edt, RadioButton rd)
    {
        final Dialog d = new Dialog(Objects.requireNonNull(this.getContext()));
        Objects.requireNonNull(d.getWindow()).setLayout(240, ViewGroup.LayoutParams.WRAP_CONTENT);
        d.setContentView(R.layout.number_picker_dialogue);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        TextView title=d.findViewById(R.id.title);
        title.setText(edt.getHint()+" in "+ rd.getText());
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(floatNum.length-1);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(floatNum);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(v -> {
            edt.setText(String.valueOf(floatNum[np.getValue()]));
            d.dismiss();
        });
        b2.setOnClickListener(v -> d.dismiss());
        d.show();
    }
}
