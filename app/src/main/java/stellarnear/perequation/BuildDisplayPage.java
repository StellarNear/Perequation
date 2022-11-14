package stellarnear.perequation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

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
        buildPage2(AllFamilies.getInstance(mC).getFamList(),AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv());
    }

    public BuildDisplayPage(Activity mA, Context mC, LinearLayout mainLin, History.Record record){
        this.mA=mA;
        this.mC=mC;
        this.mainLin=mainLin;
        buildPage2(record.getFamilies(), record.getMoneyPerIndiv(),record.getCalendar());
    }


    private void buildPage2(FamilyList famList, double moneyPerIndiv, GregorianCalendar... loadedFromHistoryArg) {

        GregorianCalendar loadedFromHistory=null;
        if(loadedFromHistoryArg.length>0){
            loadedFromHistory=loadedFromHistoryArg[0];
        }
        TextView result = mainLin.findViewById(R.id.resume_info_header);
        String result_txt="Total dons : "+famList.getAllMoney()+"€, Population : "+ AllFamilies.getInstance(mC).getFamList().getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", moneyPerIndiv)+"€";
        if (famList.hasAlim()) {result_txt+=", Repas : "+famList.getAlim()+"€";}
        if(loadedFromHistory!=null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            result_txt= dateFormat.format(loadedFromHistory.getTime())+ "\n"+result_txt;

        }
        result.setText(result_txt);

        if(loadedFromHistory==null) {
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder b = new AlertDialog.Builder(mA);
                    b.setTitle("Changement du budget cadeau (<" + moneyPerIndiv + "€)");
                    final EditText input = new EditText(mC);
                    input.setTextColor(Color.BLACK);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    b.setView(input);
                    b.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int newBudget = tools.toInt(input.getText().toString());
                            if (newBudget < moneyPerIndiv) {
                                AllFamilies.getInstance(mC).getCalculation().calculMoneyPerIndivFixed((double) newBudget);
                                AllFamilies.getInstance(mC).getTransfertManager().invalidateTranferts();
                                buildPage2(AllFamilies.getInstance(mC).getFamList(), AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv());
                            } else {
                                tools.customToast(mC, "Le nouveau budget doit etre inferieur à " + AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv() + "€", "center");
                            }
                        }
                    });
                    b.setNegativeButton("Annuler", null);
                    b.create().show();
                }
            });
        }

        LinearLayout scroll_fams = mainLin.findViewById(R.id.scroll_main_lin);
        scroll_fams.removeAllViews();

        for (final Family fam : famList.asList()){
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

        if(loadedFromHistory==null) {
            LinearLayout buttonT = mainLin.findViewById(R.id.validation_button);
            buttonT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
                    if (settings.getBoolean("switch_anim_calcul", mC.getResources().getBoolean(R.bool.switch_anim_calcul_def))) {
                        calculationAnimation();
                    } else {
                        calculAndTurnPage();
                    }
                }
            });

            LinearLayout buttonDisplayTranfert = mainLin.findViewById(R.id.display_tranferts);
            buttonDisplayTranfert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListner != null) {
                        mListner.onEvent();
                    }
                }
            });

            if(AllFamilies.getInstance(mC).getTransfertManager().transfertAvailable()){
                buttonT.setVisibility(View.GONE);buttonDisplayTranfert.setVisibility(View.VISIBLE);
            } else { buttonDisplayTranfert.setVisibility(View.GONE); buttonT.setVisibility(View.VISIBLE);}
        } else {
            mainLin.findViewById(R.id.display_tranferts).setVisibility(View.GONE);
            mainLin.findViewById(R.id.validation_button).setVisibility(View.GONE);
        }

        LinearLayout buttonBack = mainLin.findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListnerBack!=null){mListnerBack.onEvent();}
            }
        });
    }

    private void calculAndTurnPage() {
        AllFamilies.getInstance(mC).getTransfertManager().calculTransfer();
        tools.customToast(mC, "Solution trouvée !", "center");
        buildPage2(AllFamilies.getInstance(mC).getFamList(), AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv());
    }


    private void calculationAnimation() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View layoutRecordVideo = inflater.inflate(R.layout.full_drawable_animation, null);
        final CustomAlertDialog customVideo = new CustomAlertDialog(mA, mC, layoutRecordVideo);
        customVideo.setPermanent(true);
        final FrameLayout drawFrame = layoutRecordVideo.findViewById(R.id.fullscreen_drawable);
        customVideo.showAlert();

        int timePerSlide=200;
        final Animation aniFade = AnimationUtils.loadAnimation(mC,R.anim.fadein_drawable_animation);
        for (int i = 0; i <= 9; i++) {
            final ImageView img = new ImageView(mC);
            img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            int drawableId=mC.getResources().getIdentifier("ic_vector_"+i, "drawable", mC.getPackageName());
            img.setImageDrawable(mC.getDrawable(drawableId));
            img.setBackgroundColor(Color.TRANSPARENT);

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawFrame.addView(img);
                    img.startAnimation(aniFade);
                }
            }, (i+1)*timePerSlide);
        }
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                 Animation aniFadeOut =  AnimationUtils.loadAnimation(mC,R.anim.fadeout_drawable_animation);
                 aniFadeOut.setAnimationListener(new Animation.AnimationListener() {
                     @Override
                     public void onAnimationStart(Animation animation) {

                     }

                     @Override
                     public void onAnimationEnd(Animation animation) {
                         customVideo.dismissAlert();
                     }

                     @Override
                     public void onAnimationRepeat(Animation animation) {

                     }
                 });
                drawFrame.removeViews(0,drawFrame.getChildCount()-1);
                drawFrame.getChildAt(0).startAnimation(aniFadeOut);

                calculAndTurnPage();
            }
        }, 12*timePerSlide);

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
