package stellarnear.perequation;

import java.util.HashMap;
import java.util.Map;


public class Family  {

    private String id;
    private String branchId;
    private String  name;
    private Integer donation=0;
    private Integer nMember;
    private Integer nChild;
    private Integer exed=0;
    private boolean alimentaire=false;
    private Integer alim=0;

    public Family(String id,String name,Integer nMember,Integer nChild,String branchId){
        this.id=id;
        this.name=name;
        this.nMember =nMember;
        this.nChild=nChild;
        this.branchId=branchId;
    }

    public String getId() {
        return id;
    }

    public String getBranchId() {
        return branchId;
    }

    public Integer getnMember() {
        return nMember;
    }

    public Integer getnChild() {
        return nChild;
    }

    public void setnMember(Integer nMember) {
        this.nMember = nMember;
    }

    public void setnChild(Integer nChild) {
        this.nChild = nChild;
    }

    public Integer getDonation() {
        return this.donation;
    }

    public Integer getPopulation() {
        return this.nMember+this.nChild;
    }
    public String getName() {
        return this.name;
    }

    public void setDonation(Integer donation) {
        this.donation = donation;
    }

    public void calcExed(Double moneyPerIndiv){
        if (this.alimentaire) {
            this.exed = (int) (this.donation - (this.nMember + this.nChild) * moneyPerIndiv - this.alim);
        } else {
            this.exed = (int) (this.donation - (this.nMember + this.nChild) * moneyPerIndiv);
        }
    }

    public void calcExedRefund(Double oldMoneyPerIndiv,Double newMoneyPerIndiv) {  //quand on fixe manuelement une moneyPerIndiv inferieur il faut diminuer les don de chaqu'un si y a pas d'alimentaire
        this.donation=this.donation-(int)((oldMoneyPerIndiv-newMoneyPerIndiv)*(this.nChild+this.nMember));
        this.exed = (int) (this.donation - (this.nMember + this.nChild) * newMoneyPerIndiv);
    }


    public void setExed(Integer exed){
            this.exed = exed;
    }


    public Integer getExed() {
        return this.exed;
    }

    public void setAlimentaire_bool(boolean alim){
        this.alimentaire=alim;
    }

    public boolean isAlim(){
        return this.alimentaire;
    }

    public void setAlim(int alim){
        this.alim=alim;
    }
    public Integer getAlim(){
        return  this.alim;
    }


    public void reset() {
        donation=0;
        exed=0;
    }
}
