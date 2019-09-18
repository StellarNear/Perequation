package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Calculation {
    private Tools tools=new Tools();
    private Context mC;
    private double moneyPerIndiv=0.0d;
    private AllFamilies allFamilies;
    private boolean transfertAvailable=false;

    private static Calculation instance=null;

    public static Calculation getInstance() {  //pour eviter de relire le xml à chaque fois
        return instance;
    }

    public static void computeInstance(Context mC, AllFamilies allFamilies) {
        instance=new Calculation(mC, allFamilies);
    }

    private Calculation(Context mC, AllFamilies allFamilies){
        this.mC=mC;
        this.allFamilies=allFamilies;
        this.transfertAvailable=false;
        calculMoneyPerIndiv();
    }

    public double getMoneyPerIndiv() {
        return moneyPerIndiv;
    }

    private void calculMoneyPerIndiv() {
        this.transfertAvailable=false;
        Integer all_money = allFamilies.getAllMoney();
        Integer all_pop = allFamilies.getAllIndiv();

        Family fam_alloc = testAllocAlim(allFamilies);
        Double moneyPerIndiv = 0.0;
        if (fam_alloc == null) {
            moneyPerIndiv = (double) all_money / all_pop;
        } else {

            Double moneyPerIndivOri = (double) all_money / all_pop;

            String moneyPerIndivTxt = String.valueOf(moneyPerIndivOri);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
            Double money_repas = (double) tools.toInt(prefs.getString("Money_alloc_alim",mC.getResources().getString(R.string.money_alloc_alim_def)));
            Integer arrondi_budget = tools.toInt(prefs.getString("round_budget",mC.getResources().getString(R.string.round_budget_def)));

            String avant_virgule = moneyPerIndivTxt.substring(moneyPerIndivTxt.indexOf(".")-1);  // dans 154,78  ca donne 4,78

            if (arrondi_budget==5){
                if (Double.parseDouble(avant_virgule) >=5.0) {
                    moneyPerIndiv=Double.parseDouble(moneyPerIndivTxt.replace(avant_virgule,"5"));
                } else {
                    moneyPerIndiv=Double.parseDouble(moneyPerIndivTxt.replace(avant_virgule,"0"));
                }
            } else if (arrondi_budget==1){

                moneyPerIndiv= (double) moneyPerIndivOri.intValue(); //arrondi à l'entier inferieur
                Log.d("STATE budg1",String.valueOf(moneyPerIndiv));
            }

            Double rest = moneyPerIndivOri-moneyPerIndiv;

            while (rest * all_pop < money_repas) {
                moneyPerIndiv-=arrondi_budget;
                rest = moneyPerIndivOri-moneyPerIndiv;
            }

            fam_alloc.setAlimentaire_bool(true);
            fam_alloc.setAlim((int) (rest * all_pop));
        }

        for (Family fam : allFamilies.asList()){
            fam.calcExed(moneyPerIndiv);
        }

        this.moneyPerIndiv=moneyPerIndiv;
    }

    public void calculMoneyPerIndivFixed(Double fixedMoney) {
        this.transfertAvailable=false;
        Integer all_money = allFamilies.getAllMoney();
        Integer all_pop = allFamilies.getAllIndiv();

        Family fam_alloc = testAllocAlim(allFamilies);
        Double moneyPerIndiv = 0.0;
        if (fam_alloc == null) {
            moneyPerIndiv=fixedMoney;
        } else {
            Double moneyPerIndivOri = (double) all_money / all_pop;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
            Double money_repas = (double) tools.toInt(prefs.getString("Money_alloc_alim",mC.getResources().getString(R.string.money_alloc_alim_def)));
            Integer arrondi_budget = tools.toInt(prefs.getString("round_budget",mC.getResources().getString(R.string.round_budget_def)));

            moneyPerIndiv=fixedMoney;

            Double rest = moneyPerIndivOri-moneyPerIndiv;

            while (rest * all_pop < money_repas) {
                moneyPerIndiv-=arrondi_budget;
                rest = moneyPerIndivOri-moneyPerIndiv;
            }

            fam_alloc.setAlimentaire_bool(true);
            fam_alloc.setAlim((int) (rest * all_pop));
        }

        for (Family fam : allFamilies.asList()){
            fam.calcExed(moneyPerIndiv);
        }

        this.moneyPerIndiv=moneyPerIndiv;
    }

    private Family testAllocAlim(AllFamilies AllFamilies) {
        for (final Family fam : AllFamilies.asList()){
            if (fam.isAlim()) {
                return fam;
            }
        }
        return null;
    }

    private void clearTranferts(){
        for (Family fam : allFamilies.asList()){
            fam.clearTransfert();
        }
    }

    public void calculTransfer(){
        clearTranferts();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("family_mode_calcul", mC.getResources().getBoolean(R.bool.family_mode_calcul_def))) {
            calculTransferFamilyBranch(); //famille d'abord
            calculTransferAll();            // apres on fais les restant
        } else {
         calculTransferAll();
        }
        this.transfertAvailable=true;
    }

    private void calculTransferAll() {
        FamilyList donateurs = new FamilyList();
        FamilyList receveurs = new FamilyList();
        for (Family fam : allFamilies.asList()){
            if (fam.getExed()>0){
                donateurs.add(fam);
            } else if (fam.getExed()<0){
                receveurs.add(fam);
            }
        }

        for (Family fam_don : donateurs.asList()){
            Integer dons = fam_don.getExed();
            Boolean allRecAllOk=false;
            while (dons > 1 && !allRecAllOk) {
                allRecAllOk=true;
                for (Family fam_rec : receveurs.asList()){
                    if (Math.abs(fam_rec.getExed())>1){allRecAllOk=false;}   //tant que quelqu'un a besoin d'argent
                }
                for (Family fam_rec : receveurs.asList()){
                    if (dons == 0 ){continue;} //plus d'argent à donner
                    if(Math.abs(fam_rec.getExed())>1){ //famille receveur encore dans le besoin
                        if (dons >= Math.abs(fam_rec.getExed())){
                            fam_don.addTransfert(fam_rec,Math.abs(fam_rec.getExed()));
                            dons -= Math.abs(fam_rec.getExed());
                            fam_don.setExed(dons);
                            fam_rec.setExed(0); //le don couvre
                        } else {
                            fam_don.addTransfert(fam_rec,dons);
                            fam_rec.setExed(fam_rec.getExed()+dons);
                            dons = 0;
                            fam_don.setExed(dons); //don est inf
                        }
                    } else { continue;}
                }
            }
        }
    }

    private void calculTransferFamilyBranch() {
        FamilyList donateurs = new FamilyList();
        FamilyList receveurs = new FamilyList();
        for (Family fam : allFamilies.asList()){
            fam.clearTransfert(); //en cas d'aller retour p2>p1
            if (fam.getExed()>0){
                donateurs.add(fam);
            } else if (fam.getExed()<0){
                receveurs.add(fam);
            }
        }

        for (Family fam_don : donateurs.asList()){
            Integer dons = fam_don.getExed();
            Boolean familyCompleted=false;
            while (dons > 1 && !familyCompleted) {
                for (Family fam_rec : receveurs.filterBranchID(fam_don.getBranchId()).asList()){
                    if (dons == 0 ){continue;} //plus d'argent à donner
                    if(Math.abs(fam_rec.getExed())>1){ //famille encore dans le besoin
                        if (dons >= Math.abs(fam_rec.getExed())){
                            fam_don.addTransfert(fam_rec,Math.abs(fam_rec.getExed()));
                            dons -= Math.abs(fam_rec.getExed());
                            fam_don.setExed(dons);
                            fam_rec.setExed(0); //le don couvre
                        } else {
                            fam_don.addTransfert(fam_rec,dons);
                            fam_rec.setExed(fam_rec.getExed()+dons);
                            dons = 0;
                            fam_don.setExed(dons); //le don est inf
                        }
                    } else { continue;}
                }
                familyCompleted=true;//on a fait toute la branche
            }
        }
    }


    public boolean transfertAvailable() {
        return transfertAvailable;
    }
}
