package stellarnear.perequation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;


public class MainActivity extends AppCompatActivity {

    private  BuildInputPage inputPageBuilder;
    private BuildDisplayPage displayPageBuilder;
    private BuildTransfertPage transfertPageBuilder;
    private ViewFlipper panel;
    private Tools tools=new Tools();
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_def))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LinearLayout pageInput = (LinearLayout) ((FrameLayout)findViewById(R.id.include_input)).findViewById(R.id.main_linear_1);
        final LinearLayout pageDisplay = (LinearLayout) ((FrameLayout)findViewById(R.id.include_display)).findViewById(R.id.main_linear_2);
        final LinearLayout pageTransfert = (LinearLayout) ((FrameLayout)findViewById(R.id.include_transfert)).findViewById(R.id.main_linear_3);

        panel = (ViewFlipper) findViewById(R.id.panel);


        final AllFamilies allFamilies = AllFamilies.getInstance(getApplicationContext());
        Family famAlloc = testAllocAlim(allFamilies);
        String msg="";
        if (famAlloc==null) {
            msg="Aucune famille n'a été trouvée dans les paramètres pour organiser le repas.";
        } else {
            msg="La famille "+famAlloc.getName()+" a été désignée organisatrice du repas.";
        }

        tools.customToast(getApplicationContext(),msg,"center");


        inputPageBuilder = new BuildInputPage(getApplicationContext(), pageInput);
        inputPageBuilder.setValidationEventListener(new BuildInputPage.OnValidationRequest() {
            @Override
            public void onEvent() {
                Calculation.computeInstance(getApplicationContext(),allFamilies);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e){}

                displayPageBuilder=new BuildDisplayPage(MainActivity.this,getApplicationContext(),pageDisplay);
                displayPageBuilder.setBackEventListener(new BuildDisplayPage.OnBackRequest() {
                    @Override
                    public void onEvent() {
                        inputPageBuilder.refresh();
                        panel.showPrevious();
                    }
                });
                displayPageBuilder.setValidationEventListener(new BuildDisplayPage.OnValidationRequest() {
                    @Override
                    public void onEvent() {
                        transfertPageBuilder=new BuildTransfertPage(getApplicationContext(),pageTransfert);
                        transfertPageBuilder.setBackEventListener(new BuildTransfertPage.OnBackRequest() {
                            @Override
                            public void onEvent() {
                                inputPageBuilder.refresh();
                                panel.setDisplayedChild(0);
                            }
                        });
                        panel.showNext();
                    }
                });
                panel.showNext();

            }
        });


    }

    private Family testAllocAlim(AllFamilies AllFamilies) {
        for (final Family fam : AllFamilies.asList()){
            if (fam.isAlim()) {
                return fam;
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}