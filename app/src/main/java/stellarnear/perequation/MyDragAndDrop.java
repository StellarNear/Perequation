package stellarnear.perequation;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class MyDragAndDrop {

    private Tools tools=new Tools();
    private Activity mA;
    private Context mC;
    static private Family currentReciever;
    static private LinearLayout currentFamilyLine;
    static private Family currentPreviousDonator;
    private Family tempAdditionFamilyDonator =null;
    private Family tempAdditionFamilyReciever =null;
    private TransfertManager transfertManager;
    private OnRefreshListner mListner;

    public MyDragAndDrop(Activity mA,Context mC,TransfertManager transfertManager){
        this.mA=mA;
        this.mC=mC;
        this.transfertManager=transfertManager;
    }


    public void setTouchListner(Family previousDon,View v,LinearLayout familyRecLine,Family familyRec){
        v.setOnTouchListener(new MyTouchListner(previousDon,familyRecLine,familyRec));
    }

    public void setDragListner(View v,Family familyDon){  //faire un truc ennemi
        v.setOnDragListener(new MyDragListener(familyDon));
    }

    public void setRemoveOnclickListner(final ImageView removeButton,final LinearLayout famRecLine,final Family familyDon,final Family familyRec) {
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mA)
                        .setTitle("Confirmation de suppression de transfert")
                        .setMessage("Confirmes tu enelver la famille "+familyRec.getName()+" des receveurs de la famille "+familyDon.getName())
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ViewGroup owner = (ViewGroup) famRecLine.getParent();
                                owner.removeView(famRecLine);
                                transfertManager.removeTransfert(familyDon,familyRec);
                                if(mListner!=null){mListner.onEvent();}
                            }
                        })
                        .setNegativeButton("non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).show();
            }
        });
    }

    public void setAddRecieverOnclickListner(ImageView addButton, final Family famDon) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempAdditionFamilyDonator=famDon;
                pickNewReciever();
            }
        });
    }

    private void pickNewReciever() {
        // setup the alert builder
        tempAdditionFamilyReciever=null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mA);
        builder.setTitle("Choix de la famille");
        // add a radio button list
        final ArrayList<String> familiesNames=new ArrayList<>();

        for(Family fam : this.transfertManager.getReceveurs().asList()){
            familiesNames.add(fam.getName());
        }

        int checkedItem = -1;
        String[] familiesArray = familiesNames.toArray(new String[familiesNames.size()]);
        builder.setSingleChoiceItems(familiesArray, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempAdditionFamilyReciever = transfertManager.getReceveurs().asList().get(which);
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                if(tempAdditionFamilyReciever!=null) {
                    askForNewTransfert(tempAdditionFamilyDonator,tempAdditionFamilyReciever);
                }
            }
        });
        builder.setNegativeButton("Annuler", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void askForNewTransfert(final Family newTransfertDon,final Family newTransfertRec) {
        final EditText input = new EditText(mC);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        int previousMoneyTransfert = transfertManager.getExistingTransfer(newTransfertDon,newTransfertRec);
        if(previousMoneyTransfert>0) {
            input.setHint("Précédant don : "+previousMoneyTransfert+" €");
        }
        new AlertDialog.Builder(mA)
                .setView(input)
                .setTitle("Montant du transfert")
                .setMessage("La somme que la famille " + newTransfertDon.getName() + " doit donner à " + newTransfertRec.getName())
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        transfertManager.forceTransaction(newTransfertDon, newTransfertRec, tools.toInt(input.getText().toString()));
                        if (mListner != null) {
                            mListner.onEvent();
                        }
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
        if (mListner != null) {
            mListner.onEvent();
        }
    }


    class MyTouchListner implements View.OnTouchListener {
        private LinearLayout familyRecLine;
        private Family family;
        private Family previousDon;
        private MyTouchListner(Family previousDon,LinearLayout familyRecLine,Family family){
            this.previousDon=previousDon;
            this.familyRecLine=familyRecLine;
            this.family=family;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                currentPreviousDonator =previousDon;
                currentFamilyLine=familyRecLine;
                currentReciever=family;
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = mC.getResources().getDrawable(R.drawable.edit_select_gradient);
        Drawable normalShape = mC.getResources().getDrawable(R.drawable.edit_basic_gradient);
        private Family familyDon;
        private MyDragListener(Family familyDon){
            this.familyDon=familyDon;
        }


        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    ViewGroup owner = (ViewGroup) currentFamilyLine.getParent();
                    owner.removeView(currentFamilyLine);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(currentFamilyLine);
                    currentFamilyLine.setVisibility(View.VISIBLE);
                    askForNewTransfert(familyDon,currentReciever);
                    transfertManager.removeTransfert(currentPreviousDonator,currentReciever);
                    if (mListner != null) {
                        mListner.onEvent();
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    public interface OnRefreshListner {
        void onEvent();
    }

    public void setOnRefreshListner(OnRefreshListner eventListener) {
        mListner = eventListener;
    }

}
