package stellarnear.perequation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BuildDisplayPage {

    private Context mC;
    private LinearLayout mainLin;
    private Tools tools=new Tools();
    private OnValidationRequest mListner;
    private OnBackRequest mListnerBack;

    public BuildDisplayPage(Context mC,LinearLayout mainLin){
        this.mC=mC;
        this.mainLin=mainLin;
        buildPage2();
    }


    private void addHsep(LinearLayout lay, int color, int h) {
        View h_sep = new View(mC);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,h));
        h_sep.setBackgroundColor(color);
        lay.addView(h_sep);
    }



    private void buildPage2() {
        mainLin.removeAllViews();
        addHsep(mainLin,Color.DKGRAY,4);

        TextView result = new TextView(mC);
        Double money_per_indiv=Calculation.getInstance().getMoneyPerIndiv();
        String result_txt="Total dons : "+AllFamilies.getInstance(mC).getAllMoney()+"€, Population : "+ AllFamilies.getInstance(mC).getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", money_per_indiv)+"€";
        if (AllFamilies.getInstance(mC).isAlim()) {result_txt+=", Repas : "+AllFamilies.getInstance(mC).getAlim()+"€";}
        result.setTextSize(18);
        result.setSingleLine(false);
        GradientDrawable gd_res = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.WHITE, Color.parseColor("#99ddff")});
        gd_res.setCornerRadius(0f);
        result.setBackground(gd_res);
        result.setGravity(Gravity.CENTER);
        result.setTextColor(Color.DKGRAY);
        result.setText(result_txt);

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                AlertDialog.Builder b = new AlertDialog.Builder(mC);
                b.setTitle("Changement du budget cadeau (<"+money_per_indiv+"€)");
                final EditText input = new EditText(mC);
                input.setTextColor(Color.BLACK);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                b.setView(input);
                b.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int newBudget = tools.toInt(input.getText().toString());
                        if (newBudget<money_per_indiv) {
                            calculMoneyPerIndiv(AllFamilies,(double) newBudget);
                            buildPage2(mainLin, AllFamilies, newBudget, panel);
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this,"Le nouveau budget doit etre inferieur à "+money_per_indiv+"€",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }
                });
                b.setNegativeButton("Annuler", null);
                b.create().show();

                 */
            }
        });


        mainLin.addView(result);

        addHsep(mainLin,Color.DKGRAY,4);

        final LinearLayout fam_title = new LinearLayout(mC);
        fam_title.setOrientation(LinearLayout.HORIZONTAL);
        fam_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        fam_title.setWeightSum(3);
        fam_title.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable fam_title_gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.WHITE, Color.LTGRAY});
        fam_title_gd.setCornerRadius(0f);
        fam_title.setBackground(fam_title_gd);

        mainLin.addView(fam_title);

        LinearLayout Colonne1Titre = new LinearLayout(mC);
        Colonne1Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne1Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        Colonne1Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        LinearLayout Colonne2Titre = new LinearLayout(mC);
        Colonne2Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne2Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne2Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        LinearLayout Colonne3Titre = new LinearLayout(mC);
        Colonne3Titre.setOrientation(LinearLayout.VERTICAL);
        Colonne3Titre.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        Colonne3Titre.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


        fam_title.addView(Colonne1Titre);
        fam_title.addView(Colonne2Titre);
        fam_title.addView(Colonne3Titre);

        TextView fam_nam=new TextView(mC);
        TextView donation=new TextView(mC);
        TextView ecart=new TextView(mC);

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

        addHsep(mainLin,Color.DKGRAY,4);

        ScrollView scrolling_fams = new ScrollView(mC);
        mainLin.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(mC);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);

        for (final Family fam : AllFamilies.getInstance(mC).asList()){
            final LinearLayout fam_lin = new LinearLayout(mC);

            int end_color=Color.LTGRAY;

            if (fam.getExed()>1) { end_color=Color.parseColor("#48eabf");}
            if (fam.getExed()<-1)  { end_color=Color.parseColor("#F5A9A9");}

            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {Color.WHITE,end_color});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.HORIZONTAL);

            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            fam_lin.setWeightSum(3);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);
            scroll_fams.addView(fam_lin);

            LinearLayout Colonne1 = new LinearLayout(mC);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            LinearLayout Colonne2 = new LinearLayout(mC);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            LinearLayout Colonne3 = new LinearLayout(mC);
            Colonne3.setOrientation(LinearLayout.VERTICAL);
            Colonne3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne3.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);


            fam_lin.addView(Colonne1);
            fam_lin.addView(Colonne2);
            fam_lin.addView(Colonne3);


            TextView fam_txt = new TextView(mC);
            fam_txt.setText(fam.getName()+" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(16);
            fam_txt.setGravity(Gravity.CENTER);
            fam_txt.setTextColor(Color.DKGRAY);
            Colonne1.addView(fam_txt);

            TextView fam_don = new TextView(mC);
            fam_don.setText(String.valueOf(fam.getDonation())+"€");
            fam_don.setTextSize(20);
            fam_don.setGravity(Gravity.CENTER);

            fam_don.setTextColor(Color.DKGRAY);
            Colonne2.addView(fam_don);

            TextView fam_exed_txt = new TextView(mC);
            fam_exed_txt.setText(fam.getExed()+"€");
            fam_exed_txt.setTextSize(20);
            fam_exed_txt.setGravity(Gravity.CENTER);
            fam_exed_txt.setTextColor(Color.DKGRAY);
            Colonne3.addView(fam_exed_txt);

            addHsep(scroll_fams,Color.GRAY,4);

        }

        final Button buttonT = new Button(mC);
        buttonT.setText("Calculer les transferts");
        buttonT.setTextSize(18);
        buttonT.setTextColor(Color.WHITE);
        buttonT.setCompoundDrawablesWithIntrinsicBounds(null,null,tools.changeColor(mC,R.drawable.ic_swap_vert_black_24dp,"white"),null);
        scroll_fams.addView(buttonT);

        buttonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculation.getInstance().calculTransfer();
                mainLin.removeAllViews();
                buildPage2();
            }
        });

        final Button buttonBack = new Button(mC);
        buttonBack.setText("Retour à la saisie des dons");
        buttonBack.setCompoundDrawablesWithIntrinsicBounds(tools.changeColor(mC,R.drawable.ic_arrow_back_black_24dp,"white"),null,null,null);
        buttonBack.setTextSize(18);
        buttonBack.setTextColor(Color.WHITE);
        scroll_fams.addView(buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListnerBack!=null){mListnerBack.onEvent();}
            }
        });

    }



    public interface OnBackRequest {
        void onEvent();
    }

    public void setBackEventListener(OnBackRequest eventListener) {
        mListnerBack = eventListener;
    }

    public interface OnValidationRequest {
        void onEvent();
    }

    public void setValidationEventListener(OnValidationRequest eventListener) {
        mListner = eventListener;
    }
}
