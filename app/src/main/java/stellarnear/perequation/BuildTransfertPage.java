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
import android.widget.LinearLayout;
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



    private void buildPage3() {

        TextView result = mainLin.findViewById(R.id.resume_info_header);

        String result_txt="Total dons : "+AllFamilies.getInstance(mC).getAllMoney()+"€, Population : "+ AllFamilies.getInstance(mC).getAllIndiv() +"\nBudget cadeau : "+String.format("%.2f", AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv())+"€";
        if (AllFamilies.getInstance(mC).hasAlim()) {result_txt+=", Repas : "+AllFamilies.getInstance(mC).getAlim()+"€";}
        result.setText(result_txt);

        LinearLayout scroll_fams = mainLin.findViewById(R.id.scroll_main_lin);
        scroll_fams.removeAllViews();

        for (final Family famDon : AllFamilies.getInstance(mC).getTransfertManager().getDonators()) {

            TextView fam_don_name = new TextView(mC);
            fam_don_name.setText(famDon.getName());
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

            scroll_fams.addView(fam_lin);

            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            for (PairFamilyTranfertSum pairFamilyTranfertSum : AllFamilies.getInstance(mC).getTransfertManager().getReciversForDonator(famDon)) {

                TextView fam_rece = new TextView(mC);

                SpannableString spannableString = new SpannableString("X "+pairFamilyTranfertSum.getRecivier().getName()+" ("+pairFamilyTranfertSum.getSumMoney()+"€)"); //X sera remplacé par l'image apres

                Drawable img_recolo=tools.changeColor(mC,R.drawable.ic_send_black_24dp,Color.DKGRAY);
                img_recolo.setBounds(0, 0, Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16,mC.getResources().getDisplayMetrics())), Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16,mC.getResources().getDisplayMetrics())));

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



        }

        LinearLayout buttonBack = mainLin.findViewById(R.id.back_button);
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
