package stellarnear.perequation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PrefHistory {

    private Activity mA;
    private Context mC;

    private PreferenceCategory listHisto;

    private Tools tools=new Tools();
    public PrefHistory(Activity mA, Context mC){
        this.mA=mA;
        this.mC=mC;
    }


    public void addCategory(PreferenceCategory list) {
        this.listHisto = list;
        refreshList();
    }

    private void refreshList() {
        listHisto.removeAll();

        Preference div = new Preference(mC);
        div.setLayoutResource(R.layout.divider_pref);
        div.setSelectable(false);
        listHisto.addPreference(div);

        History history = new History(mC);
        for (final History.Record record : history.gettAllHistoryFamilies()) {
            Preference data = new Preference(mC);
            data.setKey("_member");
            data.setTitle("Année "+record.getCalendar().get(Calendar.YEAR) +" ("+String.format("%.2f",record.getMoneyPerIndiv())+"€/personne)");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            data.setSummary("Enregistré le "+dateFormat.format(record.getCalendar().getTime()));

            final String time = dateFormat.format(record.getCalendar().getTime());
            data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    tools.customToast(mC, "Chargement de la pérequation de "+record.getCalendar().get(Calendar.YEAR));
                    Intent intent = new Intent(mC, MainActivity.class);
                    Bundle extras = new Bundle();
                    extras.putLong("record_timestamp",record.getTimestamp());
                    intent.putExtras(extras);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    mC.startActivity(intent);
                    return false;
                }
            });
            listHisto.addPreference(data);
        }
    }
}
