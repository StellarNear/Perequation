package stellarnear.perequation;

import android.app.Activity;
import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.text.InputType;

import java.util.ArrayList;


public class PrefAllFamsListFragment {

    private Activity mA;
    private Context mC;

    private PreferenceCategory listCat;

    private Tools tools=new Tools();
    public PrefAllFamsListFragment(Activity mA, Context mC){
        this.mA=mA;
        this.mC=mC;
    }


    public void addFamsList(PreferenceCategory famList) {
        this.listCat=famList;
        refreshList();
    }

    private void refreshList() {
        listCat.removeAll();

        Preference div = new Preference(mC);
        div.setLayoutResource(R.layout.divider_pref);
        div.setSelectable(false);
        listCat.addPreference(div);
        for (Family fam: AllFamilies.getInstance(mC).asList()) {
            EditTextPreference textMember = new EditTextPreference(mC,InputType.TYPE_CLASS_NUMBER);
            textMember.setKey(fam.getId()+"_member");
            textMember.setTitle(fam.getName() + " (principaux)");
            textMember.setSummary("Membres : %s");
            textMember.setDefaultValue(String.valueOf(fam.getnMember()));
            listCat.addPreference(textMember);

            EditTextPreference textChild = new EditTextPreference(mC,InputType.TYPE_CLASS_NUMBER);
            textChild.setKey(fam.getId()+"_child");
            textChild.setTitle(fam.getName() + " (enfants)");
            textChild.setSummary("Enfants : %s");
            textChild.setDefaultValue(String.valueOf(fam.getnChild()));
            listCat.addPreference(textChild);
        }
    }

    public void addFamilyAlimListPref(PreferenceCategory general){
        ListPreference list = new ListPreference(mC);
        list.setTitle("Famille qui organise le repas");

        ArrayList<String> listName = new ArrayList();
        ArrayList<String> listVal = new ArrayList();
        listName.add("Aucune");
        listVal.add("aucune");
        for(Family fam : AllFamilies.getInstance(mC).asList()){
            listName.add(fam.getName());
            listVal.add(fam.getId());
        }

        list.setEntries(listName.toArray(new CharSequence[listName.size()]));
        list.setEntryValues(listVal.toArray(new CharSequence[listVal.size()]));
        list.setSummary("Valeur : %s");
        list.setDefaultValue("Aucune");
        list.setKey("alloc_alime");
        general.addPreference(list);
    }
}
