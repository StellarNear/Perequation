package stellarnear.perequation;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ContentFrameLayout;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                startActivity(new Intent(this, MainActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || InfosPreferenceFragment.class.getName().equals(fragmentName)
                || RazPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     page générale
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);


        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }



    /**
     page d'info settings
     */
    @TargetApi(23)
    public static class InfosPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ContentFrameLayout window =(ContentFrameLayout) getActivity().findViewById(android.R.id.content);

            ImageView ivBackground = new ImageView(getContext());
            ivBackground.setImageDrawable(getResources().getDrawable(R.mipmap.logo_near));

            window.addView(ivBackground);


            LinearLayout page_info = new LinearLayout(getContext());
            page_info.setOrientation(LinearLayout.VERTICAL);
            window.addView(page_info);


            TextView Yfa_version = new TextView(getContext());
            Yfa_version.setText("Version actuelle : "+getResources().getString(R.string.version));
            Yfa_version.setTextSize(22);

            page_info.addView(Yfa_version);

            ScrollView scroll_info = new ScrollView(getContext());
            page_info.addView(scroll_info);

            final TextView texte_infos = new TextView(getContext());
            texte_infos.setSingleLine(false);
            texte_infos.setTextColor(Color.DKGRAY);

            texte_infos.setText(getResources().getString(R.string.basic_infos));

            scroll_info.addView(texte_infos);

            final Button button = new Button(getContext());
            button.setText("Patch notes");
            button.setTextSize(18);
            button.setElevation(10);
            page_info.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    texte_infos.setText(getResources().getString(R.string.patch_list));

                    button.setVisibility(View.GONE);
                }
            });


        }
    }


    /**
     page de reset settings
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RazPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            View window =(View) getActivity().findViewById(android.R.id.content);
            window.setBackgroundResource(R.drawable.reset_background);


            int time=2000; // in milliseconds

            Handler h=new Handler();

            h.postDelayed(new Runnable() {

                              @Override
                              public void run() {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();

            String descr="Remise à zero des paramètres de l'application";
            Toast toast = Toast.makeText(getContext(), descr, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            startActivity(new Intent(getActivity(), MainActivity.class));
                              }

            },time);

        }
    }



}
