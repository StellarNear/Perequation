package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Calculation {
    private Tools tools=new Tools();
    private Context mC;
    private double moneyPerIndiv=0.0d;
    private FamilyList familyList;


    public Calculation(Context mC, FamilyList familyList){
        this.mC=mC;
        this.familyList =familyList;
        calculMoneyPerIndiv();
    }

    public double getMoneyPerIndiv() {
        return moneyPerIndiv;
    }

    private void calculMoneyPerIndiv() {
        Integer all_money = familyList.getAllMoney();
        Integer all_pop = familyList.getAllIndiv();

        Family fam_alloc = testAllocAlim();
        Double moneyPerIndivCalcul = 0.0d;
        if (fam_alloc == null) {
            moneyPerIndivCalcul = (double) all_money / all_pop;
        } else {

            Double moneyPerIndivOri = (double) all_money / all_pop;

            String moneyPerIndivTxt = String.valueOf(moneyPerIndivOri);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
            Double money_repas = (double) tools.toInt(prefs.getString("Money_alloc_alim",mC.getResources().getString(R.string.money_alloc_alim_def)));
            Integer arrondi_budget = tools.toInt(prefs.getString("round_budget",mC.getResources().getString(R.string.round_budget_def)));

            String avant_virgule = moneyPerIndivTxt.substring(moneyPerIndivTxt.indexOf(".")-1);  // dans 154,78  ca donne 4,78

            if (arrondi_budget==5){
                if (Double.parseDouble(avant_virgule) >=5.0) {
                    moneyPerIndivCalcul=Double.parseDouble(moneyPerIndivTxt.replace(avant_virgule,"5"));
                } else {
                    moneyPerIndivCalcul=Double.parseDouble(moneyPerIndivTxt.replace(avant_virgule,"0"));
                }
            } else if (arrondi_budget==1){

                moneyPerIndivCalcul= (double) moneyPerIndivOri.intValue(); //arrondi à l'entier inferieur
                Log.d("STATE budg1",String.valueOf(moneyPerIndivCalcul));
            }

            Double rest = moneyPerIndivOri-moneyPerIndivCalcul;

            while (rest * all_pop < money_repas) {
                moneyPerIndivCalcul-=arrondi_budget;
                rest = moneyPerIndivOri-moneyPerIndivCalcul;
            }

            fam_alloc.setAlimentaire_bool(true);
            fam_alloc.setAlim((int) (rest * all_pop));
        }

        for (Family fam : familyList.asList()){
            fam.calcExed(moneyPerIndivCalcul);
        }

        this.moneyPerIndiv=moneyPerIndivCalcul;
    }

    public void calculMoneyPerIndivFixed(Double fixedMoney) {
        Integer all_money = familyList.getAllMoney();
        Integer all_pop = familyList.getAllIndiv();

        Family fam_alloc = testAllocAlim();
        Double moneyPerIndivCalcul = 0.0d;
        if (fam_alloc == null) {
            moneyPerIndivCalcul=fixedMoney;
        } else {
            Double moneyPerIndivOri = (double) all_money / all_pop;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
            Double money_repas = (double) tools.toInt(prefs.getString("Money_alloc_alim",mC.getResources().getString(R.string.money_alloc_alim_def)));
            Integer arrondi_budget = tools.toInt(prefs.getString("round_budget",mC.getResources().getString(R.string.round_budget_def)));

            moneyPerIndivCalcul=fixedMoney;

            Double rest = moneyPerIndivOri-moneyPerIndivCalcul;

            while (rest * all_pop < money_repas) {
                moneyPerIndivCalcul-=arrondi_budget;
                rest = moneyPerIndivOri-moneyPerIndivCalcul;
            }

            fam_alloc.setAlimentaire_bool(true);
            fam_alloc.setAlim((int) (rest * all_pop));
        }

        for (Family fam : familyList.asList()){
            if(fam_alloc!=null){
                fam.calcExed(moneyPerIndivCalcul);
            } else {
                fam.calcExedRefund(this.moneyPerIndiv,moneyPerIndivCalcul);
            }
        }

        this.moneyPerIndiv=moneyPerIndivCalcul;
    }

    private Family testAllocAlim() {
        for (final Family fam : familyList.asList()){
            if (fam.isAlim()) {
                return fam;
            }
        }
        return null;
    }

    public void resetCalculation() {
        calculMoneyPerIndiv();
    }
}
