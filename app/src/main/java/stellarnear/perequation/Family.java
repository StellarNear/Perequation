package stellarnear.perequation;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;


public class Family extends AppCompatActivity implements Serializable {


    private String  name;
    private int  donation;
    private int n_member;
    private int transfert;
    private String transfert_to;

    public Family(String name,Integer donation,Integer n_member){
        this.name=name;
        this.donation=donation;
        this.n_member=n_member;
    }

    public int getDonation() {
        return this.donation;
    }

    public int getPopulation() {
        return this.n_member;
    }
}
