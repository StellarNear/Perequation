package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class All_Families extends AppCompatActivity {

    private Context mContext;
    public List<Family> All_Families = new ArrayList<Family>();

    public All_Families(Context mC) {
        mContext = mC;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);

            if (prefs.getBoolean("Jacques_Dominique_switch",mC.getResources().getBoolean(R.bool.Jacques_Dominique_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Jacques_Dominique_n",mC.getResources().getString(R.string.Jacques_Dominique_n_def)),"Nombre Jacques et Dominique",mC);
                All_Families.add(new Family("Jacques et Dominique",n_member));
            }
            if (prefs.getBoolean("Jeremie_Marjorie_switch",mC.getResources().getBoolean(R.bool.Jeremie_Marjorie_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Jeremie_Marjorie_n",mC.getResources().getString(R.string.Jeremie_Marjorie_n_def)),"Nombre Jeremie et Marjorie",mC);
                All_Families.add(new Family("Jérémie et Marjorie",n_member));
            }
            if (prefs.getBoolean("Bernard_Chantal_switch",mC.getResources().getBoolean(R.bool.Bernard_Chantal_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Bernard_Chantal_n",mC.getResources().getString(R.string.Bernard_Chantal_n_def)),"Nombre Bernard et Chantal",mC);
                All_Families.add(new Family("Bernard et Chantal",n_member));
            }
            if (prefs.getBoolean("Robert_Mireille_switch",mC.getResources().getBoolean(R.bool.Robert_Mireille_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Robert_Mireille_n",mC.getResources().getString(R.string.Robert_Mireille_n_def)),"Nombre Robert et Mireille",mC);
                All_Families.add(new Family("Robert et Mireille",n_member));
            }
            if (prefs.getBoolean("Claude_Francoise_switch",mC.getResources().getBoolean(R.bool.Claude_Francoise_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Claude_Francoise_n",mC.getResources().getString(R.string.Claude_Francoise_n_def)),"Claude et Francoise",mC);
                All_Families.add(new Family("Claude et Francoise",n_member));
            }






        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public Integer to_int(String key,String field,Context mC){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            Toast toast = Toast.makeText(mC, "Attention la valeur : "+key+"\nDu champ : "+field+"\nEst incorrecte, valeur mise à 0.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            value=0;
        }
        return value;
    }

    public Integer getAllMoney() {
        Integer allMoney=0;
        for (Family fam : All_Families){
            allMoney+=fam.getDonation();
        }
        return allMoney;
    }


    public Integer getAllIndiv() {
        Integer allPop=0;
        for (Family fam : All_Families){
            allPop+=fam.getPopulation();
        }
        return allPop;
    }

    public List<Family> asList() {
        return All_Families;
    }
}