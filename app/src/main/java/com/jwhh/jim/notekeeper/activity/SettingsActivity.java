package com.jwhh.jim.notekeeper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.jwhh.jim.notekeeper.BuildConfig;
import com.jwhh.jim.notekeeper.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load Settings Fragment
        getFragmentManager().beginTransaction().replace(R.id.settings_content, new MainPreferenceFragment()).commit();
    }


    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main_inuse);

            setDefaults();

            // EditText Change Listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_display_name)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_email)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_social_network)));

            // Notification Pref Change Listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            // Feedback Pref Change Listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });
        }

        private void setDefaults() {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            findPreference(getString(R.string.key_display_name)).setSummary(prefs.getString(getString(R.string.key_display_name), getString(R.string.default_display_name)));

            findPreference(getString(R.string.key_email)).setSummary(prefs.getString(getString(R.string.key_email), getString(R.string.default_email)));

            ListPreference p = (ListPreference) findPreference(getString(R.string.key_social_network));
            String str = prefs.getString(getString(R.string.key_social_network), "");
            int index = p.findIndexOfValue(str);
            findPreference(getString(R.string.key_social_network)).setSummary(index >= 0 ? p.getEntries()[index] : null);

            RingtonePreference ringtonePreference = (RingtonePreference) findPreference(getString(R.string.key_notifications_new_message_ringtone));
            String ring = prefs.getString(getString(R.string.key_notifications_new_message_ringtone), "");
            if (TextUtils.isEmpty(ring)) {
                ringtonePreference.setSummary(R.string.pref_ringtone_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(ringtonePreference.getContext(), Uri.parse(ring));
                if (null == ringtone) {
                    ringtonePreference.setSummary(R.string.summary_choose_ringtone);
                } else {
                    String name = ringtone.getTitle(ringtonePreference.getContext());
                    ringtonePreference.setSummary(name);
                }
            }

            findPreference(getString(R.string.key_version)).setSummary(BuildConfig.VERSION_NAME);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                getActivity().onBackPressed();
            }
            return super.onOptionsItemSelected(item);
        }

        private static void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        }

        private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();

                if (preference instanceof ListPreference) {
                    // For List Preferences, look up the correct display value of the preference's entries list
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);
                    // Set summary to reflect new value
                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                } else if (preference instanceof RingtonePreference) {
                    if (TextUtils.isEmpty(stringValue)) {
                        preference.setSummary(R.string.pref_ringtone_silent);
                    } else {
                        Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

                        if (null == ringtone) {
                            preference.setSummary(R.string.summary_choose_ringtone);
                        } else {
                            String name = ringtone.getTitle(preference.getContext());
                            preference.setSummary(name);
                        }
                    }
                } else if (preference instanceof EditTextPreference) {
                    if (preference.getKey().equalsIgnoreCase("key_display_name")) {
                        preference.setSummary(stringValue);
                    } else if (preference.getKey().equalsIgnoreCase("key_email")) {
                        preference.setSummary(stringValue);
                    }
                } else {
                    preference.setSummary(stringValue);
                }
                return true;
            }
        };

        public static void sendFeedback(Context context) {
            String body = null;
            try {
                body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n" +
                        " Device OS version: " + Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: "
                        + Build.BRAND + "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"schoolwards.kupl@gmail.com", "rebelliontotherescue@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
            intent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
        }

    }
}
