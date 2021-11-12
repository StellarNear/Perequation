package stellarnear.perequation;


import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HistoryFragment extends Preference {

    private Context mC;
    private View mainView;
    private LinearLayout list;


    public HistoryFragment(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public HistoryFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public HistoryFragment(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        this.mC = getContext();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mainView = inflater.inflate(R.layout.history_list, null);

        refreshData();

        return mainView;
    }


    private void refreshData() {
        /*
        list = mainView.findViewById(R.id.history_list);
        list.removeAllViews();
        for (final FameEntry fame : pj.getHallOfFame().getHallOfFameList()) {
            LinearLayout statLine = new LinearLayout(mC);
            statLine.setOrientation(LinearLayout.HORIZONTAL);
            statLine.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            para.setMargins(10, 10, 10, 10);
            para.gravity = Gravity.CENTER;
            statLine.setLayoutParams(para);
            statLine.setMinimumHeight(150);
            statLine.setBackground(mC.getDrawable(R.drawable.background_border_fame));

            statLine.addView(newTextInfo(fame.getSumDmg() + " dégâts"));
            statLine.addView(newTextInfo(fame.getFoeName()));
            statLine.addView(newTextInfo(fame.getLocation()));

            statLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);
                    tools.customToast(mC, formater.format(fame.getStat().getDate()) + "\n" + fame.getDetails(), "center");
                }
            });

            statLine.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    updateFame(fame);
                    return false;
                }
            });

            list.addView(statLine, 0);

         */
    }
}


