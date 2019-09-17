package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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


    private void addHsep(LinearLayout lay, int color, int h) {
        View h_sep = new View(mC);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,h));
        h_sep.setBackgroundColor(color);
        lay.addView(h_sep);
    }

    private void buildPage1() {
        addHsep(mainLin, Color.DKGRAY,4);

        final LinearLayout fam_title = new LinearLayout(mC);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        fam_title.setWeightSum(2);
        fam_title.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable fam_title_gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.WHITE, Color.LTGRAY});
        fam_title_gd.setCornerRadius(0f);
        fam_title.setBackground(fam_title_gd);
        mainLin.addView(fam_title);
        addHsep(mainLin,Color.DKGRAY,4);

        LinearLayout Colonne1Titre = new LinearLayout(mC);
        Colonne1Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne1Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        Colonne1Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        LinearLayout Colonne2Titre = new LinearLayout(mC);
        Colonne2Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne2Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne2Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);



        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);


        TextView fam_nam=new TextView(mC);
        TextView donation=new TextView(mC);


        fam_nam.setTextSize(20);
        fam_nam.setTextColor(Color.DKGRAY);
        fam_nam.setGravity(Gravity.CENTER);
        fam_nam.setText("Famille");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(20);
        donation.setTextColor(Color.DKGRAY);
        donation.setGravity(Gravity.CENTER);
        donation.setText("Donation");
        Colonne2Titre.addView(donation);



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);

        if (prefs.getBoolean("Test_switch",mC.getResources().getBoolean(R.bool.Test_switch_def)))  {  //mettre le switch dans les parametre appli en bas
            LinearLayout test_hori = new LinearLayout(mC);
            test_hori.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            test_hori.setOrientation(LinearLayout.HORIZONTAL);
            test_hori.setWeightSum(2);
            mainLin.addView(test_hori);


            LinearLayout Colonne1Test = new LinearLayout(mC);
            Colonne1Test.setOrientation(LinearLayout.VERTICAL);
            Colonne1Test.setGravity(Gravity.CENTER);
            Colonne1Test.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
            LinearLayout Colonne2Test = new LinearLayout(mC);
            Colonne2Test.setOrientation(LinearLayout.VERTICAL);
            Colonne2Test.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
            Colonne2Test.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

            test_hori.addView(Colonne1Test);
            test_hori.addView(Colonne2Test);

            Button button_rand = new Button(mC);
            button_rand.setText("Random");
            button_rand.setTextSize(18);
            button_rand.setTextColor(Color.WHITE);
            Colonne1Test.addView(button_rand);

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


            final EditText donation_all = new EditText(mC);
            donation_all.setTextSize(25);
            donation_all.setGravity(Gravity.CENTER);
            donation_all.setTextColor(Color.DKGRAY);
            donation_all.setInputType(InputType.TYPE_CLASS_NUMBER);
            Colonne2Test.addView(donation_all);

            Button button_all = new Button(mC);
            button_all.setText("Set All");
            button_all.setTextColor(Color.WHITE);
            button_all.setTextSize(18);
            Colonne2Test.addView(button_all);

            button_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (Family fam : AllFamilies.getInstance(mC).asList()){
                        fam.setDonation(tools.toInt(donation_all.getText().toString()));
                    }
                    if(mListner!=null){mListner.onEvent();}
                }
            });


        }


        ScrollView scrolling_fams = new ScrollView(mC);
        mainLin.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(mC);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);





        for (final Family fam : AllFamilies.getInstance(mC).asList()){
            final LinearLayout fam_lin = new LinearLayout(mC);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {Color.WHITE,Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);

            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setWeightSum(2);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout Colonne1 = new LinearLayout(mC);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            LinearLayout Colonne2 = new LinearLayout(mC);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);


            TextView fam_txt = new TextView(mC);
            fam_txt.setText(fam.getName() +" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(20);
            fam_txt.setGravity(Gravity.CENTER);
            fam_lin.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            final EditText donation_picker = new EditText(mC);
            donation_picker.setTextSize(30);
            donation_picker.setGravity(Gravity.CENTER);
            donation_picker.setTextColor(Color.DKGRAY);
            donation_picker.setInputType(InputType.TYPE_CLASS_NUMBER);
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

            Colonne2.addView(donation_picker);

            addHsep(scroll_fams,Color.GRAY,4);
        }

        final Button button = new Button(mC);
        button.setText("Enregistrer les donations");
        button.setTextSize(18);
        button.setTextColor(Color.WHITE);
        button.setCompoundDrawablesWithIntrinsicBounds(null,null,tools.changeColor(mC,R.drawable.ic_check_black_24dp,"white"),null);
        scroll_fams.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListner!=null){mListner.onEvent();}
            }
        });

    }

    public interface OnValidationRequest {
        void onEvent();
    }

    public void setValidationEventListener(OnValidationRequest eventListener) {
        mListner = eventListener;
    }

}
