package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class BuildInputPage {
    private Context mC;
    private LinearLayout mainLin;
    private Tools tools=new Tools();
    private OnValidationRequest mListner;

    public BuildInputPage(Context mC,LinearLayout mainLin){
        this.mC=mC;
        this.mainLin=mainLin;
        buildPage1();
    }


    private void buildPage1() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);

        if (prefs.getBoolean("Test_switch",mC.getResources().getBoolean(R.bool.Test_switch_def)))  {  //mettre le switch dans les parametre appli en bas
            ((LinearLayout)(mainLin.findViewById(R.id.test_lin))).setVisibility(View.VISIBLE);

            Button button_rand = mainLin.findViewById(R.id.random_button);

            button_rand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Family fam : AllFamilies.getInstance(mC).asList()){
                        Random rand = new Random();
                        fam.setDonation(rand.nextInt(1200));
                    }
                    if(mListner!=null){mListner.onEvent();}
                }
            });

            final EditText donation_all = mainLin.findViewById(R.id.edittext_input_setall);
            Button button_all = mainLin.findViewById(R.id.button_setall);
            button_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Family fam : AllFamilies.getInstance(mC).asList()){
                        fam.setDonation(tools.toInt(donation_all.getText().toString()));
                    }
                    if(mListner!=null){mListner.onEvent();}
                }
            });


        } else {
            ((LinearLayout)(mainLin.findViewById(R.id.test_lin))).setVisibility(View.GONE);
        }



        LinearLayout scroll_fams =mainLin.findViewById(R.id.scroll_main_lin);
        scroll_fams.removeAllViews();

        for (final Family fam : AllFamilies.getInstance(mC).asList()){
            final LinearLayout fam_lin = new LinearLayout(mC);
            fam_lin.setBackground(mC.getDrawable(R.drawable.background_family));
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);

            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            fam_lin.setWeightSum(3);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            TextView fam_txt = new TextView(mC);
            fam_txt.setText(fam.getName() +" ("+fam.getPopulation()+")");
            fam_txt.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,2));
            fam_txt.setTextSize(20);
            fam_txt.setGravity(Gravity.CENTER);
            fam_lin.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            fam_txt.setTextColor(Color.DKGRAY);
            fam_lin.addView(fam_txt);


            final EditText donation_picker = new EditText(mC);
            donation_picker.setTextSize(20);
            donation_picker.setGravity(Gravity.CENTER);
            donation_picker.setTextColor(Color.DKGRAY);
            donation_picker.setInputType(InputType.TYPE_CLASS_NUMBER);
            donation_picker.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            donation_picker.setHintTextColor(mC.getColor(R.color.white));
            if(fam.getDonation()>0){donation_picker.setHint(String.valueOf(fam.getDonation()));}
            donation_picker.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fam.setDonation(tools.toInt(donation_picker.getText().toString()));
                }
            });

            fam_lin.addView(donation_picker);
        }

        LinearLayout button = mainLin.findViewById(R.id.validation_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListner!=null){mListner.onEvent();}
            }
        });

    }

    public void refresh() {
        buildPage1();
    }

    public void loadFromHistory(History.Record record) {
        AllFamilies.getInstance(mC).loadFromHistory(record);
        if(mListner!=null){mListner.onEvent();}
    }

    public interface OnValidationRequest {
        void onEvent();
    }

    public void setValidationEventListener(OnValidationRequest eventListener) {
        mListner = eventListener;
    }

}
