package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransfertManager {
    private Tools tools=new Tools();
    private Context mC;
    private FamilyList allFamilies;
    private FamilyList donateurs = new FamilyList();
    private FamilyList receveurs = new FamilyList();

    private Map<Family, ArrayList<PairFamilyTranfertSum>> transfertMapDonatorRecivers = new HashMap<Family, ArrayList<PairFamilyTranfertSum>>();

    private boolean transfertAvailable=false;

    public TransfertManager(Context mC, FamilyList allFamilies){
        this.mC=mC;
        this.allFamilies=allFamilies;
        selectDonorsAndRecievers();
    }

    private void selectDonorsAndRecievers() {
        donateurs = new FamilyList();
        receveurs = new FamilyList();
        for (Family fam : allFamilies.asList()){
            if (fam.getExed()>0){
                donateurs.add(fam);
                addDonator(fam);
            } else if (fam.getExed()<0){
                receveurs.add(fam);
            }
        }
    }

    public void invalidateTranferts(){
        clearTranferts();
        this.transfertAvailable=false;
    }

    public void clearTranferts(){
        transfertMapDonatorRecivers =new HashMap<Family, ArrayList<PairFamilyTranfertSum>>();
        selectDonorsAndRecievers();
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
                            addDonation(fam_don,fam_rec,Math.abs(fam_rec.getExed()));
                            dons -= Math.abs(fam_rec.getExed());
                            fam_don.setExed(dons);
                            fam_rec.setExed(0); //le don couvre
                        } else {
                            addDonation(fam_don,fam_rec,dons);
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

        for (Family fam_don : donateurs.asList()){
            Integer dons = fam_don.getExed();
            Boolean familyCompleted=false;
            while (dons > 1 && !familyCompleted) {
                for (Family fam_rec : receveurs.filterBranchID(fam_don.getBranchId()).asList()){
                    if (dons == 0 ){continue;} //plus d'argent à donner
                    if(Math.abs(fam_rec.getExed())>1){ //famille encore dans le besoin
                        if (dons >= Math.abs(fam_rec.getExed())){
                            addDonation(fam_don,fam_rec,Math.abs(fam_rec.getExed()));
                            dons -= Math.abs(fam_rec.getExed());
                            fam_don.setExed(dons);
                            fam_rec.setExed(0); //le don couvre
                        } else {
                            addDonation(fam_don,fam_rec,dons);
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



    private void addDonator(Family famDon) {
        if(!transfertMapDonatorRecivers.keySet().contains(famDon)){
            transfertMapDonatorRecivers.put(famDon,new ArrayList<PairFamilyTranfertSum>());
        }
    }

    private void addDonation(Family famDon, Family famRec, Integer dons) {
        transfertMapDonatorRecivers.get(famDon).add(new PairFamilyTranfertSum(famRec,dons));
    }

    public boolean transfertAvailable() {
        return transfertAvailable;
    }

    public FamilyList getDonateurs() {
        return donateurs;
    }

    public FamilyList getReceveurs() {
        return receveurs;
    }

    public ArrayList<PairFamilyTranfertSum> getReciversForDonator(Family famDon) {
        return transfertMapDonatorRecivers.get(famDon);

    }

    public void forceTransaction(Family familyDon, Family currentReciever,int money) {
        try {
            for(PairFamilyTranfertSum pair:transfertMapDonatorRecivers.get(familyDon)){
                if(pair.getRecivier().equals(currentReciever)){
                    transfertMapDonatorRecivers.get(familyDon).remove(pair);
                }
            }
            transfertMapDonatorRecivers.get(familyDon).add(new PairFamilyTranfertSum(currentReciever,money));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTransfert(Family familyDon, Family familyRec) {
        try {
            for(PairFamilyTranfertSum pair : transfertMapDonatorRecivers.get(familyDon)){
                if(pair.getRecivier()==familyRec){
                    transfertMapDonatorRecivers.get(familyDon).remove(pair);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyTransferts( Map<Family, ArrayList<PairFamilyTranfertSum>> map){
        this.transfertMapDonatorRecivers=map;
    }

    public Map<Family, ArrayList<PairFamilyTranfertSum>> getTransfertsMaps() {
        return transfertMapDonatorRecivers;
    }

    public int getSumDon(Family famDon) {
        int sum=0;
        try {
            for(PairFamilyTranfertSum pair : transfertMapDonatorRecivers.get(famDon)){
                sum+=pair.getSumMoney();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }

    public int getExistingTransfer(Family tempAdditionFamilyDonator, Family tempAdditionFamilyReciever) {
        int money=0;
        try {
            for(PairFamilyTranfertSum pair : transfertMapDonatorRecivers.get(tempAdditionFamilyDonator)){
                if(pair.getRecivier()==tempAdditionFamilyReciever){
                    money=pair.getSumMoney();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return money;
    }

}
