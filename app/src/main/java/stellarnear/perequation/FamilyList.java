package stellarnear.perequation;

import java.util.ArrayList;

public class FamilyList {
    private ArrayList<Family> list=new ArrayList<>();

    public FamilyList(ArrayList<Family> list){
        this.list=list;
    }

    public FamilyList(FamilyList famList){
        this.list=new ArrayList<>(famList.asList()); //copy
    }

    public FamilyList(){
        this.list=new ArrayList<>();
    }

    public void add(Family fam){
        this.list.add(fam);
    }

    public int size(){
        return this.list.size();
    }

    public ArrayList<Family> asList(){
        return this.list;
    }

    public FamilyList filterBranchID(String branchID){
        FamilyList resultList=new FamilyList();
        for (Family fam : list){
            if (fam.getBranchId().equalsIgnoreCase(branchID)){
                resultList.add(fam);
            }
        }
        return resultList;
    }


    public Integer getAllMoney() {
        Integer allMoney=0;
        for (Family fam : list){
            allMoney+=fam.getDonation();
        }
        return allMoney;
    }

    public boolean hasAlim() {
        boolean alim=false;
        for (Family fam : list){
            if (fam.isAlim()){alim=true;}
        }
        return alim;
    }

    public Integer getAlim() {
        for (Family fam : list){
            if (fam.isAlim()){return fam.getAlim();}
        }
        return 0;
    }

    public Integer getAllIndiv() {
        Integer allPop=0;
        for (Family fam : list){
            allPop+=fam.getPopulation();
        }
        return allPop;
    }

    public void calcExed(double moneyPerIndiv) {
        for(Family fam: list){
            fam.calcExed(moneyPerIndiv);
        }
    }
}
