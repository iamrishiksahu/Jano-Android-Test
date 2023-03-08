package com.jano.rks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    public static final String mixPanelToken = "fea42816e1c98de1d6d2f90000e14928";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppWide.mixpanel = MixpanelAPI.getInstance(this, mixPanelToken, true);

        AppWide.mixpanel.getPeople().withIdentity("123456789"); //generate this id using auth context


        //track first logged
        try {
            final JSONObject properties = new JSONObject();
            properties.put("first viewed on", System.currentTimeMillis());
            properties.put("app", "android");
            AppWide.mixpanel.registerSuperPropertiesOnce(properties);
        } catch (final JSONException e) {
            throw new RuntimeException("Could not encode hour first viewed as JSON");
        }


        setContentView(R.layout.activity_auth);

        LoginFragment fragment = new LoginFragment();
        //setting up the fragment in the activity
        loadFragment(fragment);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // To preserve battery life, the Mixpanel library will store
        // events rather than send them immediately. This means it
        // is important to call flush() to send any unsent events
        // before your application is taken out of memory.
        AppWide.mixpanel.flush();
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.trfr, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}