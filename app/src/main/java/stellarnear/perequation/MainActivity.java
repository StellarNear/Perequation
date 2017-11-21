package stellarnear.perequation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final All_Families all_families = new All_Families(getApplicationContext());

        //final LinearLayout mainLinear = (LinearLayout) findViewById(R.id.linearMain);
        final LinearLayout mainLinear1 = (LinearLayout) findViewById(R.id.linearMain1);
        final LinearLayout mainLinear2 = (LinearLayout) findViewById(R.id.linearMain2);

        final ViewSwitcher panel = (ViewSwitcher) findViewById(R.id.panel);

        buildPage1(mainLinear1,mainLinear2,all_families,panel);

        Family fam_alloc = test_alloc_alim(all_families,getApplicationContext());
        String msg="";
        if (fam_alloc==null) {
            msg="Aucune famille n'a été trouvée dans les paramètres pour organiser le repas.";
        } else {
            msg="La famille "+fam_alloc.getName()+" a été désignée organisatrice du repas.";
        }

        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();


    }

    private void setAnimPanel(ViewSwitcher panel, String mode) {
        if (mode.equals("aller")) {
            Animation outtoLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation de sortie vers la gauche
            outtoLeft.setDuration(500);
            outtoLeft.setInterpolator(new AccelerateInterpolator());

            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation d'entree par la droite
            inFromRight.setDuration(500);
            inFromRight.setInterpolator(new AccelerateInterpolator());

            panel.setInAnimation(inFromRight);
            panel.setOutAnimation(outtoLeft);
        }else {
            Animation outtoRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation de sortie vers la gauche
            outtoRight.setDuration(500);
            outtoRight.setInterpolator(new AccelerateInterpolator());

            Animation inFromLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation d'entree par la droite
            inFromLeft.setDuration(500);
            inFromLeft.setInterpolator(new AccelerateInterpolator());

            panel.setInAnimation(inFromLeft);
            panel.setOutAnimation(outtoRight);
        }
    }

    private Double calculMoneyPerIndiv(All_Families all_families) {
        Integer all_money = all_families.getAllMoney();
        Integer all_pop = all_families.getAllIndiv();

        Family fam_alloc = test_alloc_alim(all_families,getApplicationContext());
        Double money_per_indiv = 0.0;
        if (fam_alloc == null) {
            money_per_indiv = (double) all_money / all_pop;
        } else {

            Double money_per_indiv_ori = (double) all_money / all_pop;

            String money_per_indiv_txt = String.valueOf(money_per_indiv_ori);


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Double money_repas = (double) to_int(prefs.getString("Money_alloc_alim",getResources().getString(R.string.Money_alloc_alim_def)),"Argent repas",getApplicationContext());
            Integer arrondi_budget = to_int(prefs.getString("round_budget",getResources().getString(R.string.round_budget_def)),"Arrondi budget",getApplicationContext());


            Log.d("STATE txt",money_per_indiv_txt+"pos:"+money_per_indiv_txt.indexOf("."));
            String avant_virgule = money_per_indiv_txt.substring(money_per_indiv_txt.indexOf(".")-1);  // dans 154,78  ca donne 4,78
            Log.d("STATE avant virg",avant_virgule);
            if (arrondi_budget==5){
                if (Double.parseDouble(avant_virgule) >=5.0) {
                    money_per_indiv=Double.parseDouble(money_per_indiv_txt.replace(avant_virgule,"5"));
                } else {
                    money_per_indiv=Double.parseDouble(money_per_indiv_txt.replace(avant_virgule,"0"));
                }
            } else if (arrondi_budget==1){

                money_per_indiv= (double) money_per_indiv_ori.intValue(); //arrondi à l'entier inferieur
                Log.d("STATE budg1",String.valueOf(money_per_indiv));
            }


            Double rest = money_per_indiv_ori-money_per_indiv;

            while (rest * all_pop < money_repas) {
                money_per_indiv-=arrondi_budget;
                rest = money_per_indiv_ori-money_per_indiv;
            }

            fam_alloc.setAlimentaire_bool(true);
            fam_alloc.setAlim((int) (rest * all_pop));
        }

        for (Family fam : all_families.asList()){
            fam.calcExed(money_per_indiv);
        }

        return money_per_indiv;
    }

    private Family test_alloc_alim(All_Families all_families, Context mC) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        String alloc_fam = prefs.getString("Alloc_alime",mC.getResources().getString(R.string.Alloc_alime_def));

        for (final Family fam : all_families.asList()){
            if (fam.getName().equals(alloc_fam)) {
                return fam;
            }
        }
        return null;
    }

    private void buildPage1(final LinearLayout mainLinear1, final LinearLayout mainLinear2, final All_Families all_families, final ViewSwitcher panel) {

        final LinearLayout fam_title = new LinearLayout(this);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        fam_title.setWeightSum(2);
        fam_title.setGravity(Gravity.CENTER_VERTICAL);
        mainLinear1.addView(fam_title);

        LinearLayout Colonne1Titre = new LinearLayout(this);
        Colonne1Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne1Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        Colonne1Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        LinearLayout Colonne2Titre = new LinearLayout(this);
        Colonne2Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne2Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne2Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);



        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);


        TextView fam_nam=new TextView(this);
        TextView donation=new TextView(this);


        fam_nam.setTextSize(18);
        fam_nam.setTextColor(Color.DKGRAY);
        fam_nam.setGravity(Gravity.CENTER);
        fam_nam.setText("Famille");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(18);
        donation.setTextColor(Color.DKGRAY);
        donation.setGravity(Gravity.CENTER);
        donation.setText("Donation");
        Colonne2Titre.addView(donation);


        View h_sep = new View(this);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
        h_sep.setBackgroundColor(Color.GRAY);
        mainLinear1.addView(h_sep);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //CHATRON
        if (prefs.getBoolean("Test_switch",getResources().getBoolean(R.bool.Test_switch_def)))  {  //mettre le switch dans les parametre appli en bas
            LinearLayout test_hori = new LinearLayout(this);
            test_hori.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            test_hori.setOrientation(LinearLayout.HORIZONTAL);
            test_hori.setWeightSum(2);
            mainLinear1.addView(test_hori);


            LinearLayout Colonne1Test = new LinearLayout(this);
            Colonne1Test.setOrientation(LinearLayout.VERTICAL);
            Colonne1Test.setGravity(Gravity.CENTER);
            Colonne1Test.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
            LinearLayout Colonne2Test = new LinearLayout(this);
            Colonne2Test.setOrientation(LinearLayout.VERTICAL);
            Colonne2Test.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
            Colonne2Test.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

            test_hori.addView(Colonne1Test);
            test_hori.addView(Colonne2Test);

            Button button_rand = new Button(getApplicationContext());
            button_rand.setText("Random");
            button_rand.setTextSize(18);
            Colonne1Test.addView(button_rand);

            button_rand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (Family fam : all_families.asList()){
                        Random rand = new Random();
                        fam.setDonation(rand.nextInt(1200));
                    }


                    Double money_per_indiv = 0.0;
                    money_per_indiv=calculMoneyPerIndiv(all_families);
                    buildPage2(mainLinear2,all_families,money_per_indiv,panel);
                    switchPanel(panel,"aller");
                }
            });


            final EditText donation_all = new EditText(this);
            donation_all.setTextSize(25);
            donation_all.setGravity(Gravity.CENTER);
            donation_all.setTextColor(Color.DKGRAY);
            donation_all.setInputType(InputType.TYPE_CLASS_NUMBER);
            Colonne2Test.addView(donation_all);

            Button button_all = new Button(getApplicationContext());
            button_all.setText("Set All");
            button_all.setTextSize(18);
            Colonne2Test.addView(button_all);

            button_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (Family fam : all_families.asList()){
                        fam.setDonation(to_int(donation_all.getText().toString(),"Set all money",getApplicationContext()));
                    }

                    Double money_per_indiv = 0.0;
                    money_per_indiv=calculMoneyPerIndiv(all_families);
                    buildPage2(mainLinear2,all_families,money_per_indiv,panel);
                    switchPanel(panel,"aller");
                }
            });


        }


        ScrollView scrolling_fams = new ScrollView(this);
        mainLinear1.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(this);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);





        for (final Family fam : all_families.asList()){
            final LinearLayout fam_lin = new LinearLayout(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {Color.WHITE,Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.GRAY);
            scroll_fams.addView(h_sep2);
            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setWeightSum(2);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout Colonne1 = new LinearLayout(this);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            LinearLayout Colonne2 = new LinearLayout(this);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);


            TextView fam_txt = new TextView(this);
            fam_txt.setText(fam.getName() +" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(20);
            fam_txt.setGravity(Gravity.CENTER);
            fam_lin.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            final EditText donation_picker = new EditText(this);
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
                    fam.setDonation(to_int(donation_picker.getText().toString(),"donation famille : "+fam.getName(),getApplicationContext()));
                }
            });

            Colonne2.addView(donation_picker);

        }

        final Button button = new Button(getApplicationContext());
        button.setText("Enregisterer les donations");
        button.setTextSize(18);
        button.setCompoundDrawablesWithIntrinsicBounds(null,null,changeColor(R.drawable.ic_check_black_24dp,"white"),null);
        scroll_fams.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mainLinear.removeAllViews();

                Double money_per_indiv = 0.0;
                money_per_indiv=calculMoneyPerIndiv(all_families);
                buildPage2(mainLinear2,all_families,money_per_indiv,panel);
                switchPanel(panel,"aller");
            }
        });

    }

    private void switchPanel(ViewSwitcher panel,String mode) {
        if(mode.equals("aller")){
            setAnimPanel(panel,"aller");
            panel.showNext();
        } else {
            setAnimPanel(panel,"retour");
            panel.showPrevious();
        }



    }


    private void buildPage2(final LinearLayout mainLinear, final All_Families all_families, final double money_per_indiv, final ViewSwitcher panel) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        mainLinear.removeAllViews();
        TextView result = new TextView(this);
        String result_txt="Total dons : "+all_families.getAllMoney()+"€, Population : "+ all_families.getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", money_per_indiv)+"€";
        if (all_families.isAlim()) {result_txt+=", Repas : "+all_families.getAlim()+"€";}
        result.setTextSize(18);
        result.setSingleLine(false);
        result.setGravity(Gravity.CENTER);
        result.setTextColor(Color.DKGRAY);
        result.setText(result_txt);

        mainLinear.addView(result);


        View h_sep0 = new View(this);
        h_sep0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,8));
        h_sep0.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep0);

        final LinearLayout fam_title = new LinearLayout(this);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        fam_title.setWeightSum(3);
        fam_title.setGravity(Gravity.CENTER_VERTICAL);
        mainLinear.addView(fam_title);

        LinearLayout Colonne1Titre = new LinearLayout(this);
        Colonne1Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne1Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        Colonne1Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        LinearLayout Colonne2Titre = new LinearLayout(this);
        Colonne2Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne2Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne2Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        LinearLayout Colonne3Titre = new LinearLayout(this);
        Colonne3Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne3Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne3Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);
        fam_title.addView(Colonne3Titre);

        TextView fam_nam=new TextView(this);
        TextView donation=new TextView(this);
        TextView ecart=new TextView(this);

        fam_nam.setTextSize(20);
        fam_nam.setTextColor(Color.DKGRAY);
        fam_nam.setGravity(Gravity.CENTER);
        fam_nam.setText("     Famille     ");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(20);
        donation.setTextColor(Color.DKGRAY);
        donation.setGravity(Gravity.CENTER);
        donation.setText("Donation");
        Colonne2Titre.addView(donation);

        ecart.setTextSize(20);
        ecart.setTextColor(Color.DKGRAY);
        ecart.setGravity(Gravity.CENTER);
        ecart.setText("Excès");
        ecart.setSingleLine(false);
        Colonne3Titre.addView(ecart);

        View h_sep = new View(this);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
        h_sep.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep);

        ScrollView scrolling_fams = new ScrollView(this);
        mainLinear.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(this);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);

        for (final Family fam : all_families.asList()){
            final LinearLayout fam_lin = new LinearLayout(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {Color.WHITE,Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.GRAY);
            scroll_fams.addView(h_sep2);
            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setWeightSum(3);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout Colonne1 = new LinearLayout(this);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            LinearLayout Colonne2 = new LinearLayout(this);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            LinearLayout Colonne3 = new LinearLayout(this);
            Colonne3.setOrientation(LinearLayout.VERTICAL);
            Colonne3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne3.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);
            fam_lin.addView(Colonne3);


            TextView fam_txt = new TextView(this);
            fam_txt.setText(fam.getName()+" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(16);
            fam_txt.setGravity(Gravity.CENTER);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            TextView fam_don = new TextView(this);
            fam_don.setText(String.valueOf(fam.getDonation())+"€");
            fam_don.setTextSize(20);
            fam_don.setGravity(Gravity.CENTER);

            fam_don.setTextColor(Color.DKGRAY);
            Colonne2.addView(fam_don);

            TextView fam_exed_txt = new TextView(this);
            fam_exed_txt.setText(fam.getExed()+"€");
            fam_exed_txt.setTextSize(20);
            fam_exed_txt.setGravity(Gravity.CENTER);
            fam_exed_txt.setTextColor(Color.DKGRAY);
            Colonne3.addView(fam_exed_txt);

        }

        final Button buttonT = new Button(getApplicationContext());
        buttonT.setText("Calculer les transferts");
        buttonT.setTextSize(18);
        buttonT.setCompoundDrawablesWithIntrinsicBounds(null,null,changeColor(R.drawable.ic_swap_vert_black_24dp,"white"),null);
        scroll_fams.addView(buttonT);

        buttonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLinear.removeAllViews();

                calculTransfer(all_families);
                buildPage2_2(mainLinear,all_families,money_per_indiv,panel);
            }
        });

        final Button buttonBack = new Button(getApplicationContext());
        buttonBack.setText("Retour à la saisie des dons");
        buttonBack.setCompoundDrawablesWithIntrinsicBounds(changeColor(R.drawable.ic_arrow_back_black_24dp,"white"),null,null,null);
        buttonBack.setTextSize(18);
        scroll_fams.addView(buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPanel(panel,"retour");
            }
        });

    }

    private Drawable changeColor(int img_id, String color) {
        Drawable img = getResources().getDrawable(img_id);
        int iColor = Color.parseColor(color);

        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(colorFilter);
        return img;
    }

    private void buildPage2_2(final LinearLayout mainLinear, final All_Families all_families, final double money_per_indiv, final ViewSwitcher panel) {

        TextView result = new TextView(this);
        String result_txt="Total dons : "+all_families.getAllMoney()+"€, Population : "+ all_families.getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", money_per_indiv)+"€";
        if (all_families.isAlim()) {result_txt+=", Repas : "+all_families.getAlim()+"€";}
        result.setTextSize(18);
        result.setSingleLine(false);
        result.setGravity(Gravity.CENTER);
        result.setTextColor(Color.DKGRAY);
        result.setText(result_txt);

        mainLinear.addView(result);


        View h_sep0 = new View(this);
        h_sep0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,8));
        h_sep0.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep0);

        final LinearLayout fam_title = new LinearLayout(this);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        fam_title.setWeightSum(3);
        fam_title.setGravity(Gravity.CENTER_VERTICAL);
        mainLinear.addView(fam_title);

        LinearLayout Colonne1Titre = new LinearLayout(this);
        Colonne1Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne1Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        Colonne1Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        LinearLayout Colonne2Titre = new LinearLayout(this);
        Colonne2Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne2Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne2Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        LinearLayout Colonne3Titre = new LinearLayout(this);
        Colonne3Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne3Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne3Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);
        fam_title.addView(Colonne3Titre);

        TextView fam_nam=new TextView(this);
        TextView donation=new TextView(this);
        TextView ecart=new TextView(this);

        fam_nam.setTextSize(20);
        fam_nam.setTextColor(Color.DKGRAY);
        fam_nam.setGravity(Gravity.CENTER);
        fam_nam.setText("     Famille     ");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(20);
        donation.setTextColor(Color.DKGRAY);
        donation.setGravity(Gravity.CENTER);
        donation.setText("Donation");
        Colonne2Titre.addView(donation);

        ecart.setTextSize(20);
        ecart.setTextColor(Color.DKGRAY);
        ecart.setGravity(Gravity.CENTER);
        ecart.setText("Excès");
        ecart.setSingleLine(false);
        Colonne3Titre.addView(ecart);

        View h_sep = new View(this);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
        h_sep.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep);

        ScrollView scrolling_fams = new ScrollView(this);
        mainLinear.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(this);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);

        for (final Family fam : all_families.asList()){
            final LinearLayout fam_lin = new LinearLayout(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {Color.WHITE,Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep2.setBackgroundColor(Color.GRAY);
            scroll_fams.addView(h_sep2);
            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setWeightSum(3);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout Colonne1 = new LinearLayout(this);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            LinearLayout Colonne2 = new LinearLayout(this);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            LinearLayout Colonne3 = new LinearLayout(this);
            Colonne3.setOrientation(LinearLayout.VERTICAL);
            Colonne3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne3.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);
            fam_lin.addView(Colonne3);


            TextView fam_txt = new TextView(this);
            fam_txt.setText(fam.getName()+" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(16);
            fam_txt.setGravity(Gravity.CENTER);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            TextView fam_don = new TextView(this);
            fam_don.setText(String.valueOf(fam.getDonation())+"€");
            fam_don.setTextSize(20);
            fam_don.setGravity(Gravity.CENTER);

            fam_don.setTextColor(Color.DKGRAY);
            Colonne2.addView(fam_don);

            TextView fam_exed_txt = new TextView(this);
            fam_exed_txt.setText(fam.getExed()+"€");
            fam_exed_txt.setTextSize(20);
            fam_exed_txt.setGravity(Gravity.CENTER);
            fam_exed_txt.setTextColor(Color.DKGRAY);
            Colonne3.addView(fam_exed_txt);

        }

        final Button buttonT_display = new Button(getApplicationContext());
        buttonT_display.setText("Afficher les transferts");
        buttonT_display.setTextSize(18);
        buttonT_display.setCompoundDrawablesWithIntrinsicBounds(null,null,changeColor(R.drawable.ic_receipt_black_24dp,"white"),null);
        scroll_fams.addView(buttonT_display);

        buttonT_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLinear.removeAllViews();

                buildPage2_3(mainLinear,all_families,money_per_indiv,panel);
            }
        });

        final Button buttonBack = new Button(getApplicationContext());
        buttonBack.setText("Retour à la saisie des dons");
        buttonBack.setTextSize(18);
        buttonBack.setCompoundDrawablesWithIntrinsicBounds(changeColor(R.drawable.ic_arrow_back_black_24dp,"white"),null,null,null);
        scroll_fams.addView(buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPanel(panel,"retour");
            }
        });
    }

    private void buildPage2_3(final LinearLayout mainLinear, final All_Families all_families, final double money_per_indiv, final ViewSwitcher panel) {

        TextView result = new TextView(this);
        String result_txt="Total dons : "+all_families.getAllMoney()+"€, Population : "+ all_families.getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", money_per_indiv)+"€";
        if (all_families.isAlim()) {result_txt+=", Repas : "+all_families.getAlim()+"€";}
        result.setTextSize(18);
        result.setSingleLine(false);
        result.setGravity(Gravity.CENTER);
        result.setTextColor(Color.DKGRAY);
        result.setText(result_txt);

        mainLinear.addView(result);


        View h_sep0 = new View(this);
        h_sep0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,8));
        h_sep0.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep0);

        final LinearLayout fam_title = new LinearLayout(this);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        fam_title.setWeightSum(3);
        fam_title.setGravity(Gravity.CENTER_VERTICAL);
        mainLinear.addView(fam_title);

        LinearLayout Colonne1Titre = new LinearLayout(this);
        Colonne1Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne1Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        Colonne1Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        LinearLayout Colonne2Titre = new LinearLayout(this);
        Colonne2Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne2Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne2Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        LinearLayout Colonne3Titre = new LinearLayout(this);
        Colonne3Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne3Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne3Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);
        fam_title.addView(Colonne3Titre);

        TextView fam_nam=new TextView(this);
        TextView donation=new TextView(this);
        TextView ecart=new TextView(this);

        fam_nam.setTextSize(20);
        fam_nam.setTextColor(Color.DKGRAY);
        fam_nam.setGravity(Gravity.CENTER);
        fam_nam.setText("Donateur");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(20);
        donation.setTextColor(Color.DKGRAY);
        donation.setGravity(Gravity.CENTER);
        donation.setText(" à ");
        Colonne2Titre.addView(donation);

        ecart.setTextSize(20);
        ecart.setTextColor(Color.DKGRAY);
        ecart.setGravity(Gravity.CENTER);
        ecart.setText("Receveur");
        ecart.setSingleLine(false);
        Colonne3Titre.addView(ecart);

        View h_sep = new View(this);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
        h_sep.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep);

        ScrollView scrolling_fams = new ScrollView(this);
        mainLinear.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(this);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);

        for (final Family fam : all_families.asList()) {
            if (fam.getTransferts().isEmpty()) {
                continue;
            }  //si elle a rien donné

            final LinearLayout fam_lin = new LinearLayout(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[]{Color.WHITE, Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);
            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
            h_sep2.setBackgroundColor(Color.GRAY);
            scroll_fams.addView(h_sep2);
            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setWeightSum(3);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout Colonne1 = new LinearLayout(this);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            LinearLayout Colonne2 = new LinearLayout(this);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            LinearLayout Colonne3 = new LinearLayout(this);
            Colonne3.setOrientation(LinearLayout.VERTICAL);
            Colonne3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            Colonne3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);
            fam_lin.addView(Colonne3);


            for (Map.Entry<String,Integer> transfert : fam.getTransferts().entrySet()) {
                TextView fam_txt = new TextView(this);
                fam_txt.setText(fam.getName());
                fam_txt.setTextSize(15);
                fam_txt.setGravity(Gravity.CENTER);
                fam_txt.setTextColor(Color.DKGRAY);
                Colonne1.addView(fam_txt);

                TextView fleche = new TextView(this);
                fleche.setText(">");
                fleche.setTextSize(15);
                fleche.setGravity(Gravity.CENTER);;

                fleche.setTextColor(Color.DKGRAY);
                Colonne2.addView(fleche);


                TextView fam_rece = new TextView(this);
                fam_rece.setText(transfert.getKey()+" ("+transfert.getValue()+"€)");
                fam_rece.setTextSize(15);
                fam_rece.setGravity(Gravity.CENTER);
                fam_rece.setTextColor(Color.DKGRAY);
                Colonne3.addView(fam_rece);
            }

        }

        final Button buttonBack = new Button(getApplicationContext());
        buttonBack.setText("Retour à la saisie des dons");
        buttonBack.setTextSize(18);
        buttonBack.setCompoundDrawablesWithIntrinsicBounds(changeColor(R.drawable.ic_arrow_back_black_24dp,"white"),null,null,null);
        scroll_fams.addView(buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPanel(panel,"retour");
            }
        });
    }

    private void calculTransfer(All_Families all_families) {
        List<Family> donateurs = new ArrayList<Family>();
        List<Family> receveurs = new ArrayList<Family>();
        for (Family fam : all_families.asList()){
            if (fam.getExed()>0){
                donateurs.add(fam);
            } else if (fam.getExed()<0){
                receveurs.add(fam);
            }
        }

        for (Family fam_don : donateurs){
            Log.d("STATE dona", "---Donneur---:"+fam_don.getName());
            Integer dons = fam_don.getExed();
            Boolean all_rec_all_ok=false;
            while (dons > 1 && !all_rec_all_ok) {
                all_rec_all_ok=true;
                for (Family fam_rec : receveurs){
                    if (Math.abs(fam_rec.getExed())>1){all_rec_all_ok=false;}   //tant que quelqu'un a besoin d'argent
                }

                Log.d("STATE dons","Famille :"+fam_don.getName()+" Dons restant :"+String.valueOf(dons));
                for (Family fam_rec : receveurs){
                    if (dons == 0 ){continue;} //plus d'argent à donner
                    Log.d("STATE dons",String.valueOf(dons));
                    Log.d("STATE rec",fam_rec.getName());
                    Log.d("STATE rec_ex",String.valueOf(fam_rec.getExed()));
                    if(Math.abs(fam_rec.getExed())>1){
                        Log.d("STATE passage",fam_rec.getName()+" encore dans le besoin");
                        if (dons >= Math.abs(fam_rec.getExed())){
                            fam_don.addTransfert(fam_rec.getName(),Math.abs(fam_rec.getExed()));
                            dons -= Math.abs(fam_rec.getExed());
                            fam_don.setExed(dons);
                            fam_rec.setExed(0);
                            Log.d("STATE passage","Le dons couvre");
                        } else {
                            fam_don.addTransfert(fam_rec.getName(),dons);
                            fam_rec.setExed(fam_rec.getExed()+dons);
                            dons = 0;
                            fam_don.setExed(dons);
                            Log.d("STATE passage","Le dons est inferieur");
                        }
                    } else { continue;}
                }

            }

            Log.d("STATE dona","Don final :"+dons);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Integer to_int(String key,String field,Context mC){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){

            if (key.equals("")) {
                Toast toast = Toast.makeText(mC, "Attention il n'y a pas de valeur entrée pour : " + field + "\nValeur mise à 0.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                value = 0;
            } else {
                Toast toast = Toast.makeText(mC, "Attention la valeur : " + key + "\nDu champ : " + field + "\nEst incorrecte, valeur mise à 0.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                value = 0;
            }

        }
        return value;
    }
}