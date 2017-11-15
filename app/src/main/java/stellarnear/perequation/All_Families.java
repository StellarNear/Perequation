package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import stellarnear.periquation.R;

public class All_Families extends AppCompatActivity {

    private Context mContext;
    public List<Family> All_Families = new ArrayList<Family>();

    public All_Families(Context mC) {
        mContext = mC;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);

            if (prefs.getBoolean("chatron_jacques_switch",mC.getResources().getBoolean(R.bool.chatron_jacques_switch_def)))  {
                All_Families.add(new Family("Chatron Père",500));
            }
            if (prefs.getBoolean("chatron_jeremie_switch",mC.getResources().getBoolean(R.bool.chatron_jeremie_switch_def)))  {
                All_Families.add(new Family("Chatron Fils",100));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}