package stellarnear.perequation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

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

    private Family tempRemoveFamily=null;
    private String tempIDbranchFamily="";

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
                    prefAllFamsListFragment.addFamsList(allFams);
                    break;
                case "pref_calcul":
                    PreferenceCategory famsAlim = (PreferenceCategory) findPreference("general_alim");
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
            case "create_family":
                popupCreateFamily();
                break;
            case "remove_family":
                popupRemoveFamily();
                break;

        }

    }

    private void popupCreateFamily() {
        LayoutInflater inflater = mA.getLayoutInflater();
        final View creationView = inflater.inflate(R.layout.custom_toast_family_creation, null);

        CustomAlertDialog creationItemAlert = new CustomAlertDialog(mA, mC, creationView);
        creationItemAlert.setPermanent(true);
        creationItemAlert.addConfirmButton("Créer");
        creationItemAlert.addCancelButton("Annuler");

        View branchIDButton=creationView.findViewById(R.id.branch_id_family_creation);
        tempIDbranchFamily="";
        branchIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupChooseMainBranch();
            }
        });

        creationItemAlert.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                String name = ((EditText) creationView.findViewById(R.id.name_family_creation)).getText().toString();
                String id = ((EditText) creationView.findViewById(R.id.id_family_creation)).getText().toString();
                String nMemberTxt = ((EditText) creationView.findViewById(R.id.nmember_family_creation)).getText().toString();
                String nChildTxt = ((EditText) creationView.findViewById(R.id.nchild_family_creation)).getText().toString();

                Family fam = new Family(id,name,tools.toInt(nMemberTxt),tools.toInt(nChildTxt),tempIDbranchFamily);

                if(testUniqueID(fam) && !tempIDbranchFamily.equalsIgnoreCase("")) {
                    AllFamilies.getInstance(mC).addFamily(fam);
                    tools.customToast(mC, fam.getName() + " ajoutée !");
                    navigate();
                } else {
                    if(!testUniqueID(fam)){
                        tools.customToast(mC,"L'identifiant de famille existe déjà...");
                    } else {
                        tools.customToast(mC,"Vous devez donner la branche familliale principale !");
                    }
                }
            }
        });
        creationItemAlert.showAlert();
        final EditText editName = ((EditText) creationView.findViewById(R.id.name_family_creation));
        editName.post(new Runnable() {
            public void run() {
                editName.setFocusableInTouchMode(true);
                editName.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mA.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(editName, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void popupChooseMainBranch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mC);
        builder.setTitle("Choose an animal");

        final String[] familiesBranchs = {"chatron", "gouillion", "maestre", "ribiere"};
        builder.setItems(familiesBranchs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    tempIDbranchFamily=familiesBranchs[which];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean testUniqueID(Family famNew) {
        boolean unique=true;
        for(Family fam : AllFamilies.getInstance(mC).getFamList().asList()){
            if(fam.getId().equalsIgnoreCase(famNew.getId())){
                unique=false;
            }
        }
        return unique;
    }

    private void popupRemoveFamily() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mA);
        builder.setTitle("Choix de la famille");
        final ArrayList<String> familiesNames=new ArrayList<>();
        for(Family fam : AllFamilies.getInstance(mC).getFamList().asList()){
            familiesNames.add(fam.getName());
        }

        int checkedItem = -1;
        String[] familiesArray = familiesNames.toArray(new String[familiesNames.size()]);
        builder.setSingleChoiceItems(familiesArray, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempRemoveFamily = AllFamilies.getInstance(mC).getFamList().asList().get(which);
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                if(tempRemoveFamily!=null) {
                    AllFamilies.getInstance(mC).removeFamily(tempRemoveFamily);
                    tools.customToast(mC,"Famille supprimée !");
                    navigate();
                }
            }
        });
        builder.setNegativeButton("Annuler", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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