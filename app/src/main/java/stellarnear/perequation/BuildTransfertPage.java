package stellarnear.perequation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Map;

public class BuildTransfertPage {
    private Context mC;
    private LinearLayout mainLin;
    private Tools tools=new Tools();
    private OnBackRequest mListnerBack;

    public BuildTransfertPage(Context mC,LinearLayout mainLin){
        this.mC=mC;
        this.mainLin=mainLin;
        buildPage3();
    }



    private void addHsep(LinearLayout lay, int color, int h) {
        View h_sep = new View(mC);
        h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,h));
        h_sep.setBackgroundColor(color);
        lay.addView(h_sep);
    }
    
    private void buildPage3() {
        addHsep(mainLin, Color.DKGRAY,4);

        TextView result = new TextView(mC);

        double money_per_indiv=Calculation.getInstance().getMoneyPerIndiv();
        String result_txt="Total dons : "+AllFamilies.getInstance(mC).getAllMoney()+"€, Population : "+ AllFamilies.getInstance(mC).getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", money_per_indiv)+"€";
        if (AllFamilies.getInstance(mC).isAlim()) {result_txt+=", Repas : "+AllFamilies.getInstance(mC).getAlim()+"€";}
        result.setTextSize(18);
        result.setSingleLine(false);
        GradientDrawable gd_res = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.WHITE, Color.parseColor("#99ddff")}); //bleu
        gd_res.setCornerRadius(0f);
        result.setBackground(gd_res);
        result.setGravity(Gravity.CENTER);
        result.setTextColor(Color.DKGRAY);
        result.setText(result_txt);

        mainLin.addView(result);

        addHsep(mainLin,Color.DKGRAY,4);

        TextView transfert_title=new TextView(mC);
        transfert_title.setText("Transferts de fonds");
        transfert_title.setTextSize(20);
        transfert_title.setGravity(Gravity.CENTER);
        GradientDrawable fam_title_gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.WHITE, Color.LTGRAY});
        fam_title_gd.setCornerRadius(0f);
        transfert_title.setBackground(fam_title_gd);
        transfert_title.setTextColor(Color.DKGRAY);
        mainLin.addView(transfert_title);

        addHsep(mainLin,Color.DKGRAY,4);

        ScrollView scrolling_fams = new ScrollView(mC);
        mainLin.addView(scrolling_fams);
        LinearLayout scroll_fams = new LinearLayout(mC);
        scroll_fams.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll_fams.setOrientation(LinearLayout.VERTICAL);
        scrolling_fams.addView(scroll_fams);

        for (final Family fam : AllFamilies.getInstance(mC).asList()) {
            if (fam.getTransferts().isEmpty()) {
                continue;
            }  //si elle a rien donné

            TextView fam_don_name = new TextView(mC);
            fam_don_name.setText(fam.getName());
            fam_don_name.setTextSize(18);
            fam_don_name.setGravity(Gravity.CENTER);
            fam_don_name.setTextColor(Color.DKGRAY);
            GradientDrawable gd_dona = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[]{Color.parseColor("#DAA520"),Color.parseColor("#FFD700"), Color.WHITE});
            gd_dona.setCornerRadius(0f);
            fam_don_name.setBackground(gd_dona);

            scroll_fams.addView(fam_don_name);

            final LinearLayout fam_lin = new LinearLayout(mC);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[]{Color.WHITE, Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.VERTICAL);
            View h_sep2 = new View(mC);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
            h_sep2.setBackgroundColor(Color.GRAY);
            scroll_fams.addView(h_sep2);
            scroll_fams.addView(fam_lin);


            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            for (Map.Entry<Family,Integer> transfert : fam.getTransferts().entrySet()) {

                TextView fam_rece = new TextView(mC);


                SpannableString spannableString = new SpannableString("X "+transfert.getKey()+" ("+transfert.getValue()+"€)"); //X sera remplacé par l'image apres

                Drawable img_recolo=tools.changeColor(mC,R.drawable.ic_send_black_24dp,Color.DKGRAY);
                img_recolo.setBounds(0, 0, Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16,mC.getResources().getDisplayMetrics())), Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16,mC.getResources().getDisplayMetrics())));
                ImageSpan imageSpan = new ImageSpan(img_recolo);

                ImageSpan imageSpan_center = new ImageSpan(img_recolo, ImageSpan.ALIGN_BOTTOM) {   //centre l'image sur la ligne
                    public void draw(Canvas canvas, CharSequence text, int start,
                                     int end, float x, int top, int y, int bottom,
                                     Paint paint) {
                        Drawable b = getDrawable();
                        canvas.save();

                        int transY = bottom - b.getBounds().bottom;
                        // this is the key
                        transY -= paint.getFontMetricsInt().descent / 2;

                        canvas.translate(x, transY);
                        b.draw(canvas);
                        canvas.restore();
                    }
                };

                spannableString.setSpan(imageSpan_center,0,1,0);

                fam_rece.setText(spannableString);
                fam_rece.setTextSize(16);
                fam_rece.setGravity(Gravity.CENTER);
                fam_rece.setTextColor(Color.DKGRAY);
                //fam_rece.setCompoundDrawablesWithIntrinsicBounds(changeColor(R.drawable.ic_send_black_24dp,Color.DKGRAY),null,null,null);
                fam_lin.addView(fam_rece);
            }

            addHsep(scroll_fams,Color.GRAY,4);

        }

        final Button buttonBack = new Button(mC);
        buttonBack.setText("Retour à la saisie des dons");
        buttonBack.setTextSize(18);
        buttonBack.setTextColor(Color.WHITE);
        buttonBack.setCompoundDrawablesWithIntrinsicBounds(tools.changeColor(mC,R.drawable.ic_arrow_back_black_24dp,"white"),null,null,null);
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

}
