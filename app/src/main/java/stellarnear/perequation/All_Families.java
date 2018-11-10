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

            Integer n_memberJacques_Dominique=to_int(prefs.getString("Jacques_Dominique_n",mC.getResources().getString(R.string.Jacques_Dominique_n_def)),"Jacques et Dominique",mC);
            if(n_memberJacques_Dominique>0){All_Families.add(new Family("Jacques et Dominique",n_memberJacques_Dominique));}

            Integer n_memberJeremie_Marjorie=to_int(prefs.getString("Jeremie_Marjorie_n",mC.getResources().getString(R.string.Jeremie_Marjorie_n_def)),"Jeremie et Marjorie",mC);
            if(n_memberJeremie_Marjorie>0){All_Families.add(new Family("Jérémie et Marjorie",n_memberJeremie_Marjorie));}


        //GOUILLON
            Integer n_memberBernard_Chantal=to_int(prefs.getString("Bernard_Chantal_n",mC.getResources().getString(R.string.Bernard_Chantal_n_def)),"Bernard et Chantal",mC);
            if(n_memberBernard_Chantal>0){All_Families.add(new Family("Bernard et Chantal",n_memberBernard_Chantal));}

            Integer n_memberChristophe_Florence=to_int(prefs.getString("Christophe_Florence_n",mC.getResources().getString(R.string.Christophe_Florence_n_def)),"Christophe et Florence",mC);
            if(n_memberChristophe_Florence>0){All_Families.add(new Family("Christophe et Florence",n_memberChristophe_Florence));}

            Integer n_memberRomain=to_int(prefs.getString("Romain_n",mC.getResources().getString(R.string.Romain_n_def)),"Romain",mC);
            if(n_memberRomain>0){All_Families.add(new Family("Romain",n_memberRomain));}


            Integer n_memberAntony_Sophie=to_int(prefs.getString("Antony_Sophie_n",mC.getResources().getString(R.string.Antony_Sophie_n_def)),"Antony et Sophie",mC);
            if(n_memberAntony_Sophie>0){All_Families.add(new Family("Antony et Sophie",n_memberAntony_Sophie));}


            Integer n_memberNicolas_Delphine=to_int(prefs.getString("Nicolas_Delphine_n",mC.getResources().getString(R.string.Nicolas_Delphine_n_def)),"Nicolas et Delphine",mC);
            if(n_memberNicolas_Delphine>0){All_Families.add(new Family("Nicolas et Delphine",n_memberNicolas_Delphine));}


        //MAESTRE
                  Integer n_memberRobert_Mireille=to_int(prefs.getString("Robert_Mireille_n",mC.getResources().getString(R.string.Robert_Mireille_n_def)),"Robert et Mireille",mC);
            if(n_memberRobert_Mireille>0){All_Families.add(new Family("Robert et Mireille",n_memberRobert_Mireille));}


            Integer n_memberAurelie_Annabel=to_int(prefs.getString("Aurelie_Annabel_n",mC.getResources().getString(R.string.Aurelie_Annabel_n_def)),"Aurélie et Annabel",mC);
            if(n_memberAurelie_Annabel>0){All_Families.add(new Family("Aurélie et Annabel",n_memberAurelie_Annabel));}


            Integer n_memberRaphael_Florence=to_int(prefs.getString("Raphael_Florence_n",mC.getResources().getString(R.string.Raphael_Florence_n_def)),"Raphael et Florence",mC);
            if(n_memberRaphael_Florence>0){All_Families.add(new Family("Raphael et Florence",n_memberRaphael_Florence));}


        //RIBIERE

            Integer n_memberClaude_Francoise=to_int(prefs.getString("Claude_Francoise_n",mC.getResources().getString(R.string.Claude_Francoise_n_def)),"Claude et Francoise",mC);
            if(n_memberClaude_Francoise>0){All_Families.add(new Family("Claude et Francoise",n_memberClaude_Francoise));}

            Integer n_memberBenjamin_Claire=to_int(prefs.getString("Benjamin_Claire_n",mC.getResources().getString(R.string.Benjamin_Claire_n_def)),"Benjamin et Claire",mC);
            if(n_memberBenjamin_Claire>0){All_Families.add(new Family("Benjamin et Claire",n_memberBenjamin_Claire));}

            Integer n_memberSam_Perrine=to_int(prefs.getString("Sam_Perrine_n",mC.getResources().getString(R.string.Sam_Perrine_n_def)),"Sam et Perrine",mC);
            if(n_memberSam_Perrine>0){All_Families.add(new Family("Sam et Perrine",n_memberSam_Perrine));}

            Integer n_memberSimon_Anais=to_int(prefs.getString("Simon_Anais_n",mC.getResources().getString(R.string.Simon_Anais_n_def)),"Simon et Anaïs",mC);
            if(n_memberSimon_Anais>0){All_Families.add(new Family("Simon et Anaïs",n_memberSimon_Anais));}


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