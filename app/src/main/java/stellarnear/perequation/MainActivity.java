package stellarnear.perequation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    private BuildInputPage inputPageBuilder;
    private BuildDisplayPage displayPageBuilder;
    private BuildTransfertPage transfertPageBuilder;
    private ViewFlipper panel;
    private final Tools tools = new Tools();
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Preferences and data reset as before
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // [ADDED] Detect a fresh launch and reset saved panel index
        if (savedInstanceState == null) {
            settings.edit().remove("current_panel_index").apply();
        }
        AllFamilies.getInstance(getApplicationContext()).reset();

        // Fullscreen mode check (unchanged)
        if (settings.getBoolean("switch_fullscreen_mode",
                getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_def))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        // [MOVED] setContentView() was in onResume() — now done once in onCreate()
        setContentView(R.layout.activity_main);

        // Toolbar setup (unchanged)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Page containers
        final LinearLayout pageInput = ((FrameLayout) findViewById(R.id.include_input))
                .findViewById(R.id.main_linear_1);
        final LinearLayout pageDisplay = ((FrameLayout) findViewById(R.id.include_display))
                .findViewById(R.id.main_linear_2);
        final LinearLayout pageTransfert = ((FrameLayout) findViewById(R.id.include_transfert))
                .findViewById(R.id.main_linear_3);

        // ViewFlipper initialization
        panel = findViewById(R.id.panel);

        // Example toast (unchanged)
        final AllFamilies allFamilies = AllFamilies.getInstance(getApplicationContext());
        Family famAlloc = testAllocAlim(allFamilies);
        if (famAlloc != null) {
            String msg = "La famille " + famAlloc.getName() + " a été désignée organisatrice du repas.";
            tools.customToast(getApplicationContext(), msg, "center");
        }

        // --- Build Input Page (unchanged logic)
        inputPageBuilder = new BuildInputPage(getApplicationContext(), pageInput);
        inputPageBuilder.setValidationEventListener(new BuildInputPage.OnValidationRequest() {
            @Override
            public void onEvent() {
                // Hide keyboard
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } catch (Exception ignored) {}

                // Reset calculations and transfers
                allFamilies.getCalculation().resetCalculation();
                allFamilies.getTransfertManager().invalidateTranferts();

                // --- Build Display Page
                displayPageBuilder = new BuildDisplayPage(MainActivity.this, getApplicationContext(), pageDisplay);

                // Back button from display -> input
                displayPageBuilder.setBackEventListener(new BuildDisplayPage.OnBackRequest() {
                    @Override
                    public void onEvent() {
                        inputPageBuilder.refresh();
                        setAnimPanelBack();
                        panel.showPrevious();
                        savePanelIndex(); // [ADDED] remember current panel index
                    }
                });

                // Validation from display -> transfert
                displayPageBuilder.setValidationEventListener(new BuildDisplayPage.OnValidationRequest() {
                    @Override
                    public void onEvent() {
                        // --- Build Transfert Page
                        transfertPageBuilder = new BuildTransfertPage(MainActivity.this, getApplicationContext(), pageTransfert);
                        transfertPageBuilder.setBackEventListener(new BuildTransfertPage.OnBackRequest() {
                            @Override
                            public void onEvent() {
                                inputPageBuilder.refresh();
                                setAnimPanelBack();
                                panel.setDisplayedChild(0);
                                savePanelIndex(); // [ADDED]
                            }
                        });
                        setAnimPanelIn();
                        panel.showNext();
                        savePanelIndex(); // [ADDED]
                    }
                });

                // Animate and show next (input -> display)
                setAnimPanelIn();
                panel.showNext();
                savePanelIndex(); // [ADDED]
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // [ADDED] Restore last displayed panel when returning to app
        int savedIndex = settings.getInt("current_panel_index", 0);
        if (savedIndex < panel.getChildCount()) {
            panel.setDisplayedChild(savedIndex);
        } else {
            panel.setDisplayedChild(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePanelIndex(); // [ADDED] Save current panel index before app goes to background
    }

    // [ADDED] Helper to centralize saving of panel index
    private void savePanelIndex() {
        if (panel != null) {
            settings.edit().putInt("current_panel_index", panel.getDisplayedChild()).apply();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            long id = extras.getLong("record_timestamp");
            History history = new History(getApplicationContext());
            final History.Record record = history.getRecordForTimestamp(id);
            if (record != null) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final LinearLayout pageDisplay = ((FrameLayout) findViewById(R.id.include_display))
                                .findViewById(R.id.main_linear_2);

                        displayPageBuilder = new BuildDisplayPage(MainActivity.this, getApplicationContext(), pageDisplay, record);
                        displayPageBuilder.setBackEventListener(new BuildDisplayPage.OnBackRequest() {
                            @Override
                            public void onEvent() {
                                inputPageBuilder.refresh();
                                setAnimPanelBack();
                                panel.showPrevious();
                                savePanelIndex(); // [ADDED]
                            }
                        });
                        setAnimPanelIn();
                        panel.showNext();
                        savePanelIndex(); // [ADDED]
                    }
                }, 333);
            }
        }
    }

    // Animation utilities (unchanged)
    private void setAnimPanelIn() {
        Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.infromright);
        Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.outtoleft);
        panel.clearAnimation();
        panel.setInAnimation(in);
        panel.setOutAnimation(out);
    }

    private void setAnimPanelBack() {
        Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.outtoright);
        Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.infromleft);
        panel.clearAnimation();
        panel.setInAnimation(in);
        panel.setOutAnimation(out);
    }

    // Utility for toast message (unchanged)
    private Family testAllocAlim(AllFamilies allFamilies) {
        for (final Family fam : allFamilies.asList()) {
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
