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
}
