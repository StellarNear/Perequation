package stellarnear.perequation;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;


public class Family extends AppCompatActivity implements Serializable {


    private String  name;
    private int  donation;
    public Family(String name,Integer donation){
        this.name=name;
        this.donation=donation;

    }

}
