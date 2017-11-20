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

            //CHATRON
            if (prefs.getBoolean("Jacques_Dominique_switch",mC.getResources().getBoolean(R.bool.Jacques_Dominique_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Jacques_Dominique_n",mC.getResources().getString(R.string.Jacques_Dominique_n_def)),"Jacques et Dominique",mC);
                All_Families.add(new Family("Jacques et Dominique",n_member));
            }
            if (prefs.getBoolean("Jeremie_Marjorie_switch",mC.getResources().getBoolean(R.bool.Jeremie_Marjorie_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Jeremie_Marjorie_n",mC.getResources().getString(R.string.Jeremie_Marjorie_n_def)),"Jeremie et Marjorie",mC);
                All_Families.add(new Family("Jérémie et Marjorie",n_member));
            }

            //GOUILLON
            if (prefs.getBoolean("Bernard_Chantal_switch",mC.getResources().getBoolean(R.bool.Bernard_Chantal_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Bernard_Chantal_n",mC.getResources().getString(R.string.Bernard_Chantal_n_def)),"Bernard et Chantal",mC);
                All_Families.add(new Family("Bernard et Chantal",n_member));
            }
            if (prefs.getBoolean("Christophe_Florence_switch",mC.getResources().getBoolean(R.bool.Christophe_Florence_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Christophe_Florence_n",mC.getResources().getString(R.string.Christophe_Florence_n_def)),"Christophe et Florence",mC);
                All_Families.add(new Family("Christophe et Florence",n_member));
            }
            if (prefs.getBoolean("Antony_Sophie_switch",mC.getResources().getBoolean(R.bool.Antony_Sophie_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Antony_Sophie_n",mC.getResources().getString(R.string.Antony_Sophie_n_def)),"Antony et Sophie",mC);
                All_Families.add(new Family("Antony et Sophie",n_member));
            }
            if (prefs.getBoolean("Nicolas_Delphine_switch",mC.getResources().getBoolean(R.bool.Nicolas_Delphine_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Nicolas_Delphine_n",mC.getResources().getString(R.string.Nicolas_Delphine_n_def)),"Nicolas et Delphine",mC);
                All_Families.add(new Family("Nicolas et Delphine",n_member));
            }

            //MAESTRE
            if (prefs.getBoolean("Robert_Mireille_switch",mC.getResources().getBoolean(R.bool.Robert_Mireille_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Robert_Mireille_n",mC.getResources().getString(R.string.Robert_Mireille_n_def)),"Robert et Mireille",mC);
                All_Families.add(new Family("Robert et Mireille",n_member));
            }
            if (prefs.getBoolean("Aurelie_Annabel_switch",mC.getResources().getBoolean(R.bool.Aurelie_Annabel_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Aurelie_Annabel_n",mC.getResources().getString(R.string.Aurelie_Annabel_n_def)),"Aurélie et Annabel",mC);
                All_Families.add(new Family("Aurélie et Annabel",n_member));
            }
            if (prefs.getBoolean("Raphael_Florence_switch",mC.getResources().getBoolean(R.bool.Raphael_Florence_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Raphael_Florence_n",mC.getResources().getString(R.string.Raphael_Florence_n_def)),"Raphael et Florence",mC);
                All_Families.add(new Family("Raphael et Florence",n_member));
            }

            //RIBIERE
            if (prefs.getBoolean("Claude_Francoise_switch",mC.getResources().getBoolean(R.bool.Claude_Francoise_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Claude_Francoise_n",mC.getResources().getString(R.string.Claude_Francoise_n_def)),"Claude et Francoise",mC);
                All_Families.add(new Family("Claude et Francoise",n_member));
            }


            if (prefs.getBoolean("Benjamin_Claire_switch",mC.getResources().getBoolean(R.bool.Benjamin_Claire_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Benjamin_Claire_n",mC.getResources().getString(R.string.Benjamin_Claire_n_def)),"Benjamin et Claire",mC);
                All_Families.add(new Family("Benjamin et Claire",n_member));
            }
            if (prefs.getBoolean("Sam_Perrine_switch",mC.getResources().getBoolean(R.bool.Sam_Perrine_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Sam_Perrine_n",mC.getResources().getString(R.string.Sam_Perrine_n_def)),"Sam et Perrine",mC);
                All_Families.add(new Family("Sam et Perrine",n_member));
            }
            if (prefs.getBoolean("Simon_Anais_switch",mC.getResources().getBoolean(R.bool.Simon_Anais_switch_def)))  {
                Integer n_member=to_int(prefs.getString("Simon_Anais_n",mC.getResources().getString(R.string.Simon_Anais_n_def)),"Simon et Anaïs",mC);
                All_Families.add(new Family("Simon et Anaïs",n_member));
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

    public boolean isAlim() {
        boolean Alim=false;
        for (Family fam : All_Families){
            if (fam.isAlim()){Alim=true;}
        }
        return Alim;
    }

    public Integer getAlim() {
        for (Family fam : All_Families){
            if (fam.isAlim()){return fam.getAlim();}
        }
        return 0;
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