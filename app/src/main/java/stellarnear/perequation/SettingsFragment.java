package stellarnear.perequation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class SettingsFragment extends PreferenceFragment {
    private Activity mA;
    private Context mC;
    private List<String> histoPrefKeys = new ArrayList<>();
    private List<String> histoTitle = new ArrayList<>();

    private String currentPageKey;
    private String currentPageTitle;

    private Tools tools = new Tools();
    private SharedPreferences settings;
    private PrefInfoScreenFragment prefInfoScreenFragment;
    private PrefAllFamsListFragment prefAllFamsListFragment;

    private OnSharedPreferenceChangeListener listener =
            new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    if (key.contains("_member")|| key.contains("_child") || key.equalsIgnoreCase("alloc_alime")){
                        AllFamilies.getInstance(getContext()).checkSharedSettings();
                    }
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        settings.registerOnSharedPreferenceChangeListener(listener);
        this.mA=getActivity();
        this.mC=getContext();
        addPreferencesFromResource(R.xml.pref);
        this.histoPrefKeys.add("pref");
        this.histoTitle.add(getResources().getString(R.string.setting_activity));
        this.prefInfoScreenFragment=new PrefInfoScreenFragment(mA,mC);
        this.prefAllFamsListFragment=new PrefAllFamsListFragment(mA,mC);
    }




    // will be called by SettingsActivity (Host Activity)
    public void onUpButton() {
        if (histoPrefKeys.get(histoPrefKeys.size() - 1).equalsIgnoreCase("pref") || histoPrefKeys.size() <= 1) // in top-level
        {
            Intent intent = new Intent(mA, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mA.startActivity(intent);
        } else // in sub-level
        {
            currentPageKey=histoPrefKeys.get(histoPrefKeys.size() - 2);
            currentPageTitle=histoTitle.get(histoTitle.size() - 2);
            navigate();
            histoPrefKeys.remove(histoPrefKeys.size() - 1);
            histoTitle.remove(histoTitle.size() - 1);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().contains("pref_")) {
            histoPrefKeys.add(preference.getKey());
            histoTitle.add(preference.getTitle().toString());
        }

        if (preference.getKey().startsWith("pref")) {
            this.currentPageKey =preference.getKey();
            this.currentPageTitle =preference.getTitle().toString();
            navigate();
        } else {
            action(preference);
        }
        /*
        // Top level PreferenceScreen
        if (key.equals("top_key_0")) {         changePrefScreen(R.xml.pref_general, preference.getTitle().toString()); // descend into second level    }

        // Second level PreferenceScreens
        if (key.equals("second_level_key_0")) {        // do something...    }       */
        return true;
    }

    private void navigate() {
        if (currentPageKey.equalsIgnoreCase("pref")) {
            getPreferenceScreen().removeAll();
            addPreferencesFromResource(R.xml.pref);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPageTitle);
        } else if (currentPageKey.contains("pref_")) {
            loadPage();
            switch (currentPageKey) {
                case "pref_general":
                    PreferenceCategory allFams = (PreferenceCategory) findPreference("all_fams");
                    PreferenceCategory famsAlim = (PreferenceCategory) findPreference("general_alim");
                    prefAllFamsListFragment.addFamsList(allFams);
                    prefAllFamsListFragment.addFamilyAlimListPref(famsAlim);
                    break;
            }
        }
    }

    private void loadPage() {
        getPreferenceScreen().removeAll();
        int xmlID = getResources().getIdentifier(currentPageKey, "xml", getContext().getPackageName());
        addPreferencesFromResource(xmlID);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPageTitle);
    }

    private void action(Preference preference) {
        switch (preference.getKey()) {
            case "infos":
                prefInfoScreenFragment.showInfo();
                break;
        }

    }
        /*
        // Top level PreferenceScreen
        if (key.equals("top_key_0")) {         changePrefScreen(R.xml.pref_general, preference.getTitle().toString()); // descend into second level    }

        // Second level PreferenceScreens
        if (key.equals("second_level_key_0")) {        // do something...    }       */
        @Override
        public void onDestroy() {
            super.onDestroy();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
            prefs.unregisterOnSharedPreferenceChangeListener(listener);
        }
}