package stellarnear.perequation;

import java.util.ArrayList;
import java.util.List;

public class FamilyList {
    private ArrayList<Family> list=new ArrayList<>();
    public FamilyList(ArrayList<Family> list){
        this.list=list;
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
}
