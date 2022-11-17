package stellarnear.perequation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.GregorianCalendar;

public class BuildTransfertPage {
    private Activity mA;
    private Context mC;
    private LinearLayout mainLin;
    private Tools tools = new Tools();

    private TransfertManager currentTransfertManager = null;
    private TransfertManager tempTransfertManager = null;

    private OnBackRequest mListnerBack;

    public BuildTransfertPage(Activity mA, Context mC, LinearLayout mainLin) {
        this.mA = mA;
        this.mC = mC;
        this.mainLin = mainLin;
        this.currentTransfertManager = AllFamilies.getInstance(mC).getTransfertManager();
        buildPage3();
    }

    private void buildPage3() {

        LinearLayout result = mainLin.findViewById(R.id.resume_info_header);
        result.removeAllViews();

        String info1 = "Total dons : " + AllFamilies.getInstance(mC).getFamList().getAllMoney() + "€, Population : " + AllFamilies.getInstance(mC).getFamList().getAllIndiv();
        result.addView(getTextInfo(info1));
        String info2="Budget cadeau : " + String.format("%.2f", AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv()) + "€";
        if (AllFamilies.getInstance(mC).getFamList().hasAlim()) {
            info2 += ", Repas : " + AllFamilies.getInstance(mC).getFamList().getAlim() + "€";
        }
        result.addView(getTextInfo(info2));

        LinearLayout scroll_fams = mainLin.findViewById(R.id.scroll_main_lin);
        scroll_fams.removeAllViews();

        populateTranferts(scroll_fams);

        LinearLayout buttonSave = mainLin.findViewById(R.id.save_history);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    History history = new History(mC);
                    history.addRecord(new History.Record(new GregorianCalendar(), AllFamilies.getInstance(mC).getFamList(), AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv()));
                    tools.customToast(mC, "Péréquation enregistrée !");
                } catch (Exception e) {
                    tools.customToast(mC, "Erreur durant la sauvegarde de la péréquation");
                }

            }
        });

        LinearLayout buttonEdit = mainLin.findViewById(R.id.edit_transferts);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startManualEdition();
            }
        });

        LinearLayout buttonBack = mainLin.findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListnerBack != null) {
                    mListnerBack.onEvent();
                }
            }
        });
    }

    private TextView getTextInfo(String info_txt) {
        TextView info = new TextView(mC);
        info.setText(info_txt);
        info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        info.setTypeface(Typeface.DEFAULT_BOLD);
        info.setTextSize(18);
        info.setTextColor(Color.DKGRAY);
        return info;
    }


    private void startManualEdition() {
        FamilyList tempList = new FamilyList(AllFamilies.getInstance(mC).getFamList());
        tempList.calcExed(AllFamilies.getInstance(mC).getCalculation().getMoneyPerIndiv()); //remet les exced à l'etat avant transfert
        tempTransfertManager = new TransfertManager(mC, tempList);
        tempTransfertManager.copyTransferts(AllFamilies.getInstance(mC).getTransfertManager().getTransfertsMaps());

        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.manual_edition_transferts, null);
        CustomAlertDialog dialog = new CustomAlertDialog(mA, mC, mainView);
        dialog.setPermanent(true);
        dialog.addCancelButton("Annuler");
        dialog.addConfirmButton("Valider");
        dialog.showAlert();
        populateTranfertsEdition((LinearLayout) mainView.findViewById(R.id.main_linear_edition_tranferts));

        dialog.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                currentTransfertManager = tempTransfertManager;
                buildPage3();
            }
        });

    }

    private void populateTranferts(LinearLayout scrollMainLin) {
        for (final Family famDon : currentTransfertManager.getDonateurs().asList()) {
            if (currentTransfertManager.getReciversForDonator(famDon).size() <= 0) {
                continue;
            }
            TextView fam_don_name = new TextView(mC);

            SpannableString spannableTitleString = new SpannableString(famDon.getName() + " X"); //X sera remplacé par l'image apres

            Drawable img_mail = tools.changeColor(mC, R.drawable.ic_baseline_mail_outline_24, Color.DKGRAY);
            img_mail.setBounds(0, 0, Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, mC.getResources().getDisplayMetrics())), Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, mC.getResources().getDisplayMetrics())));

            ImageSpan imageSpanMail_center = getImageSpan(img_mail);
            spannableTitleString.setSpan(imageSpanMail_center, spannableTitleString.length()-1, spannableTitleString.length(), 0);

            fam_don_name.setText(spannableTitleString);
            fam_don_name.setTextSize(18);
            fam_don_name.setGravity(Gravity.CENTER);
            fam_don_name.setTextColor(Color.DKGRAY);
            GradientDrawable gd_dona = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[]{Color.parseColor("#DAA520"), Color.parseColor("#FFD700"), Color.WHITE});
            gd_dona.setCornerRadius(0f);
            fam_don_name.setBackground(gd_dona);

            fam_don_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder b = new AlertDialog.Builder(mA);
                    b.setTitle("Adresse(s) mails");
                    final EditText input = new EditText(mC);
                    input.setTextColor(Color.BLACK);
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    b.setView(input);
                    b.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            Email sendMail = new Email();
                            sendMail.execute(input.getText().toString() ,famDon,currentTransfertManager.getReciversForDonator(famDon));
                            sendMail.onPostExecute(mA);
                        }
                    });
                    b.setNegativeButton("Annuler", null);
                    b.create().show();
                }
            });

            scrollMainLin.addView(fam_don_name);

            final LinearLayout fam_lin = new LinearLayout(mC);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[]{Color.WHITE, Color.LTGRAY});
            gd.setCornerRadius(0f);
            fam_lin.setBackground(gd);
            fam_lin.setOrientation(LinearLayout.VERTICAL);

            scrollMainLin.addView(fam_lin);

            fam_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            fam_lin.setGravity(Gravity.CENTER_VERTICAL);

            for (PairFamilyTranfertSum pairFamilyTranfertSum : currentTransfertManager.getReciversForDonator(famDon)) {

                TextView fam_rece = new TextView(mC);

                SpannableString spannableString = new SpannableString("X " + pairFamilyTranfertSum.getRecivier().getName() + " (" + pairFamilyTranfertSum.getSumMoney() + "€)"); //X sera remplacé par l'image apres

                Drawable img_recolo = tools.changeColor(mC, R.drawable.ic_send_black_24dp, Color.DKGRAY);
                img_recolo.setBounds(0, 0, Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, mC.getResources().getDisplayMetrics())), Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, mC.getResources().getDisplayMetrics())));

                ImageSpan imageSpan_center = getImageSpan(img_recolo);
                spannableString.setSpan(imageSpan_center, 0, 1, 0);

                fam_rece.setText(spannableString);
                fam_rece.setTextSize(16);
                fam_rece.setGravity(Gravity.CENTER);
                fam_rece.setTextColor(Color.DKGRAY);
                //fam_rece.setCompoundDrawablesWithIntrinsicBounds(changeColor(R.drawable.ic_send_black_24dp,Color.DKGRAY),null,null,null);
                fam_lin.addView(fam_rece);
            }
        }
    }

    private ImageSpan getImageSpan(Drawable img) {
        return new ImageSpan(img, ImageSpan.ALIGN_BOTTOM) {   //centre l'image sur la ligne
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
    }

    private void populateTranfertsEdition(final LinearLayout scrollMainLin) {
        MyDragAndDrop myDragAndDrop = new MyDragAndDrop(mA, mC, tempTransfertManager);
        myDragAndDrop.setOnRefreshListner(new MyDragAndDrop.OnRefreshListner() {
            @Override
            public void onEvent() {
                populateTranfertsEdition(scrollMainLin);
            }
        });
        scrollMainLin.removeAllViews();
        for (final Family famDon : tempTransfertManager.getDonateurs().asList()) {
            LinearLayout fram = new LinearLayout(mC);
            fram.setGravity(Gravity.CENTER);
            fram.setOrientation(LinearLayout.VERTICAL);
            fram.setPadding(5, 50, 5, 50);
            fram.setBackground(mC.getDrawable(R.drawable.edit_basic_gradient));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 0);
            fram.setLayoutParams(params);
            scrollMainLin.addView(fram);

            LinearLayout framDom = new LinearLayout(mC);
            framDom.setGravity(Gravity.CENTER);
            framDom.setOrientation(LinearLayout.HORIZONTAL);
            fram.setPadding(5, 0, 5, 0);
            LinearLayout.LayoutParams paramsDom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            framDom.setLayoutParams(paramsDom);

            fram.addView(framDom);


            TextView fam_don_name = new TextView(mC);
            fam_don_name.setText(famDon.getName() + " [" + famDon.getExed() + "€] " + "(" + tempTransfertManager.getSumDon(famDon) + "€)");
            fam_don_name.setTextSize(16);
            fam_don_name.setTypeface(null, Typeface.BOLD);
            fam_don_name.setGravity(Gravity.CENTER);
            fam_don_name.setTextColor(mC.getColor(R.color.gold));//fam_don_name.setTextColor(Color.DKGRAY);

            framDom.addView(fam_don_name);
            myDragAndDrop.setDragListner(fram, famDon);

            ImageView addButton = new ImageView(mC);
            addButton.setImageDrawable(mC.getDrawable(R.drawable.ic_add_box_addition_24dp));
            myDragAndDrop.setAddRecieverOnclickListner(addButton, famDon);
            framDom.addView(addButton);


            for (PairFamilyTranfertSum pairFamilyTranfertSum : tempTransfertManager.getReciversForDonator(famDon)) {

                LinearLayout framRece = new LinearLayout(mC);
                framRece.setGravity(Gravity.CENTER);
                framRece.setOrientation(LinearLayout.HORIZONTAL);
                fram.setPadding(5, 0, 5, 0);
                LinearLayout.LayoutParams paramsRece = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                framRece.setLayoutParams(paramsRece);

                fram.addView(framRece);

                TextView famReceTextView = new TextView(mC);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                famReceTextView.setLayoutParams(params2);

                famReceTextView.setText(pairFamilyTranfertSum.getRecivier().getName() + " [" + pairFamilyTranfertSum.getRecivier().getExed() + "€] " + "(" + pairFamilyTranfertSum.getSumMoney() + "€)");
                famReceTextView.setTextSize(14);
                famReceTextView.setGravity(Gravity.CENTER);
                famReceTextView.setTextColor(Color.DKGRAY);
                myDragAndDrop.setTouchListner(famDon, famReceTextView, framRece, pairFamilyTranfertSum.getRecivier());
                framRece.addView(famReceTextView);

                ImageView removeButton = new ImageView(mC);
                removeButton.setImageDrawable(mC.getDrawable(R.drawable.ic_indeterminate_check_box_remove_24dp));
                myDragAndDrop.setRemoveOnclickListner(removeButton, framRece, famDon, pairFamilyTranfertSum.getRecivier());
                framRece.addView(removeButton);
            }
        }
    }

    public interface OnBackRequest {
        void onEvent();
    }

    public void setBackEventListener(OnBackRequest eventListener) {
        mListnerBack = eventListener;
    }

}
