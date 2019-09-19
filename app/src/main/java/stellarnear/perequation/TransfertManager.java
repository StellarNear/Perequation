package stellarnear.perequation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TransfertManager {
    private Tools tools=new Tools();
    private Context mC;
    private AllFamilies allFamilies;

    private Map<Family, ArrayList<PairFamilyTranfertSum>> transfertMapDonatorRecivers = new HashMap<Family, ArrayList<PairFamilyTranfertSum>>();

    private boolean transfertAvailable=false;

    public TransfertManager(Context mC,AllFamilies allFamilies){
        this.mC=mC;
        this.allFamilies=allFamilies;
    }

    public void invalidateTranferts(){
        clearTranferts();
        this.transfertAvailable=false;
    }

    private void clearTranferts(){
        transfertMapDonatorRecivers =new HashMap<Family, ArrayList<PairFamilyTranfertSum>>();
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
                addDonator(fam);
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
        FamilyList donateurs = new FamilyList();
        FamilyList receveurs = new FamilyList();
        for (Family fam : allFamilies.asList()){
            if (fam.getExed()>0){
                donateurs.add(fam);
                addDonator(fam);
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

    public Set<Family> getDonators() {
        return transfertMapDonatorRecivers.keySet();
    }

    public ArrayList<PairFamilyTranfertSum> getReciversForDonator(Family famDon) {
        return transfertMapDonatorRecivers.get(famDon);

    }
}
