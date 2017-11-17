package stellarnear.perequation;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Family extends AppCompatActivity implements Serializable {


    private String  name;
    private Integer  donation=0;
    private Integer n_member;
    private double exed;
    private boolean alimentaire=false;
    private Integer alim=0;
    private Map<String,Integer> transfert_map = new HashMap<String,Integer>();

    public Family(String name,Integer n_member){
        this.name=name;
        this.n_member=n_member;
    }

    public Integer getDonation() {
        return this.donation;
    }

    public Integer getPopulation() {
        return this.n_member;
    }
    public String getName() {
        return this.name;
    }

    public void setDonation(Integer donation) {
        Log.d("STATE dona:", "ca:"+donation+this.donation);
        this.donation = donation;
    }

    public void setExed(double money_per_indiv){
        if (this.alimentaire) {
            this.exed = this.donation - this.n_member * money_per_indiv - this.alim;
        }else {
            this.exed = this.donation - this.n_member * money_per_indiv;
        }
    }

    public Double getExed() {
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
}
