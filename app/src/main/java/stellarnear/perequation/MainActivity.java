package stellarnear.perequation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.MessagePattern;
import android.os.Bundle;
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

        LinearLayout mainLinear = (LinearLayout) findViewById(R.id.linearMain);



        for (final Family fam : all_families.asList()){
            final LinearLayout fam_lin = new LinearLayout(this);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);
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
            View v_sep = new View(this);
            v_sep.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep.setBackgroundColor(Color.GRAY);
            fam_lin.addView(v_sep);
            fam_lin.addView(Colonne2);
            View v_sep2 = new View(this);
            v_sep2.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep2.setBackgroundColor(Color.GRAY);
            fam_lin.addView(v_sep2);
            fam_lin.addView(Colonne3);



            TextView fam_txt = new TextView(this);
            fam_txt.setText(fam.getName());
            fam_txt.setTextSize(22);
            fam_txt.setGravity(1);
            fam_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fam_lin.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            final EditText donation_picker = new EditText(this);
            donation_picker.setTextSize(30);
            donation_picker.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            donation_picker.setTextColor(Color.DKGRAY);
            donation_picker.setInputType(InputType.TYPE_CLASS_NUMBER);
            donation_picker.setGravity(1);
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



            TextView fam_exed_txt = new TextView(this);
            fam_exed_txt.setText(String.format("%.2f",(double)fam.getDonation()/fam.getPopulation()));
            fam_exed_txt.setTextSize(30);
            fam_exed_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            fam_exed_txt.setTextColor(Color.DKGRAY);
            fam_exed_txt.setGravity(1);
            Colonne3.addView(fam_exed_txt);



        }




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer all_money = all_families.getAllMoney();
                Integer all_pop = all_families.getAllIndiv();

                Double money_per_indiv = (double)all_money/all_pop;
                Snackbar.make(view, "Argent total :"+all_money+" Argent par personne : "+String.format("%.2f", money_per_indiv), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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