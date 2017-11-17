package stellarnear.perequation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final All_Families all_families = new All_Families(getApplicationContext());

        final LinearLayout mainLinear = (LinearLayout) findViewById(R.id.linearMain);

        buildPage1(mainLinear,all_families);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLinear.removeAllViews();

                Double money_per_indiv = 0.0;
                money_per_indiv=calculMoneyPerIndiv(all_families);

                buildPage2(mainLinear,all_families,money_per_indiv);
                Snackbar.make(view, "Argent par personne : "+String.format("%.2f", money_per_indiv), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private Double calculMoneyPerIndiv(All_Families all_families) {
        Integer all_money = all_families.getAllMoney();
        Integer all_pop = all_families.getAllIndiv();

        Family fam_alloc = test_alloc_alim(all_families,getApplicationContext());
        Double money_per_indiv = 0.0;
        if (fam_alloc == null) {
            money_per_indiv = (double) all_money / all_pop;
        } else {

            money_per_indiv = (double) all_money / all_pop;

            Integer money_per_indiv_part_int= (int) (money_per_indiv/10.0)*10;

            Double rest = money_per_indiv-money_per_indiv_part_int;

            fam_alloc.setAlimentaire_bool(true);
            fam_alloc.setAlim((int) (rest * all_pop));

            money_per_indiv = (double) money_per_indiv_part_int;

        }

        for (Family fam : all_families.asList()){
            fam.setExed(money_per_indiv);
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

    private void buildPage1(LinearLayout mainLinear,All_Families all_families) {

        final LinearLayout fam_title = new LinearLayout(this);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        fam_title.setWeightSum(2);
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



        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);


        TextView fam_nam=new TextView(this);
        TextView donation=new TextView(this);


        fam_nam.setTextSize(18);
        fam_nam.setTextColor(Color.DKGRAY);
        fam_nam.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        fam_nam.setText("Famille");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(20);
        donation.setTextColor(Color.DKGRAY);
        donation.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        donation.setText("Donation");
        Colonne2Titre.addView(donation);


        View h_sep = new View(this);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
        h_sep.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep);

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
            mainLinear.addView(h_sep2);
            mainLinear.addView(fam_lin);


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


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);


            TextView fam_txt = new TextView(this);
            fam_txt.setText(fam.getName() +" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(20);
            fam_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fam_lin.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            final EditText donation_picker = new EditText(this);
            donation_picker.setTextSize(30);
            donation_picker.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
    }

    private void buildPage2(LinearLayout mainLinear,All_Families all_families,double money_per_indiv) {

        TextView result = new TextView(this);
        String result_txt="Total : "+all_families.getAllMoney() +"€, Budget/pers : "+String.format("%.2f", money_per_indiv)+"€";
        result.setTextSize(18);
        result.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
        fam_nam.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        fam_nam.setText("Famille");
        Colonne1Titre.addView(fam_nam);

        donation.setTextSize(20);
        donation.setTextColor(Color.DKGRAY);
        donation.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        donation.setText("Donation");
        Colonne2Titre.addView(donation);

        ecart.setTextSize(20);
        ecart.setTextColor(Color.DKGRAY);
        ecart.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ecart.setText("Excès");
        ecart.setSingleLine(false);
        Colonne3Titre.addView(ecart);

        View h_sep = new View(this);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
        h_sep.setBackgroundColor(Color.GRAY);
        mainLinear.addView(h_sep);

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
            mainLinear.addView(h_sep2);
            mainLinear.addView(fam_lin);


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
            fam_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            TextView fam_don = new TextView(this);
            fam_don.setText(String.valueOf(fam.getDonation()));
            fam_don.setTextSize(22);
            fam_don.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            fam_don.setTextColor(Color.DKGRAY);
            Colonne2.addView(fam_don);

            TextView fam_exed_txt = new TextView(this);
            fam_exed_txt.setText(String.format("%.2f",fam.getExed()));
            fam_exed_txt.setTextSize(22);
            fam_exed_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fam_exed_txt.setTextColor(Color.DKGRAY);
            Colonne3.addView(fam_exed_txt);

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