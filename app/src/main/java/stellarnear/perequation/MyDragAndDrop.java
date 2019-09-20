package stellarnear.perequation;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class MyDragAndDrop {
    private Context mC;
    static private Family currentReciever;
    private TransfertManager transfertManager;

    public MyDragAndDrop(Context mC,TransfertManager transfertManager){
        this.mC=mC;
        this.transfertManager=transfertManager;
    }

    public void setTouchListner(View v,Family familyRec){
        v.setOnTouchListener(new MyTouchListner(familyRec));
    }

    public void setDragListner(View v,Family familyDon){  //faire un truc ennemi
        v.setOnDragListener(new MyDragListener(familyDon));
    }


    class MyTouchListner implements View.OnTouchListener {
        private Family family;
        private MyTouchListner(Family family){
            this.family=family;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
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
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);

                    transfertManager.forceTransaction(familyDon,currentReciever);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

}
