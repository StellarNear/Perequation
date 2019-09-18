package stellarnear.perequation;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BuildDisplayPage {
    private Activity mA;
    private Context mC;
    private LinearLayout mainLin;
    private Tools tools=new Tools();
    private OnValidationRequest mListner;
    private OnBackRequest mListnerBack;

    public BuildDisplayPage(Activity mA,Context mC, LinearLayout mainLin){
        this.mA=mA;
        this.mC=mC;
        this.mainLin=mainLin;
        buildPage2();
    }


    private void buildPage2() {
        TextView result = mainLin.findViewById(R.id.resume_info_header);
        String result_txt="Total dons : "+AllFamilies.getInstance(mC).getAllMoney()+"€, Population : "+ AllFamilies.getInstance(mC).getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", Calculation.getInstance().getMoneyPerIndiv())+"€";
        if (AllFamilies.getInstance(mC).hasAlim()) {result_txt+=", Repas : "+AllFamilies.getInstance(mC).getAlim()+"€";}
        result.setText(result_txt);

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(mA);
                b.setTitle("Changement du budget cadeau (<"+Calculation.getInstance().getMoneyPerIndiv()+"€)");
                final EditText input = new EditText(mC);
                input.setTextColor(Color.BLACK);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                b.setView(input);
                b.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int newBudget = tools.toInt(input.getText().toString());
                        if (newBudget<Calculation.getInstance().getMoneyPerIndiv()) {
                            Calculation.getInstance().calculMoneyPerIndivFixed((double) newBudget);
                            buildPage2();
                        } else {
                            tools.customToast(mC,"Le nouveau budget doit etre inferieur à "+Calculation.getInstance().getMoneyPerIndiv()+"€","center");
                        }
                    }
                });
                b.setNegativeButton("Annuler", null);
                b.create().show();

            }
        });

        LinearLayout scroll_fams = mainLin.findViewById(R.id.scroll_main_lin);
        scroll_fams.removeAllViews();

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
            fam_lin.setWeightSum(5);
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);
            scroll_fams.addView(fam_lin);


            TextView fam_txt = new TextView(mC);
            fam_txt.setText(fam.getName()+" ("+fam.getPopulation()+")");
            fam_txt.setTextSize(16);
            fam_txt.setGravity(Gravity.CENTER);
            fam_txt.setTextColor(Color.DKGRAY);
            fam_txt.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,3));
            fam_lin.addView(fam_txt);

            TextView fam_don = new TextView(mC);
            fam_don.setText(String.valueOf(fam.getDonation())+"€");
            fam_don.setTextSize(20);
            fam_don.setGravity(Gravity.CENTER);
            fam_don.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            fam_don.setTextColor(Color.DKGRAY);
            fam_lin.addView(fam_don);

            TextView fam_exed_txt = new TextView(mC);
            fam_exed_txt.setText(fam.getExed()+"€");
            fam_exed_txt.setTextSize(20);
            fam_exed_txt.setGravity(Gravity.CENTER);
            fam_exed_txt.setTextColor(Color.DKGRAY);
            fam_exed_txt.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            fam_lin.addView(fam_exed_txt);
        }

        LinearLayout buttonT = mainLin.findViewById(R.id.validation_button);
        buttonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calculation.getInstance().calculTransfer();
                buildPage2();
            }
        });

        LinearLayout buttonDisplayTranfert = mainLin.findViewById(R.id.display_tranferts);
        buttonDisplayTranfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListner!=null){mListner.onEvent();}
            }
        });

        LinearLayout buttonBack = mainLin.findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListnerBack!=null){mListnerBack.onEvent();}
            }
        });

        if(Calculation.getInstance().transfertAvailable()){
            buttonT.setVisibility(View.GONE);buttonDisplayTranfert.setVisibility(View.VISIBLE);
        } else { buttonDisplayTranfert.setVisibility(View.GONE); buttonT.setVisibility(View.VISIBLE);}

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
