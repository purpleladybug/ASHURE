package edu.ohio_state.cse.ashure;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;

import org.json.JSONObject;


public class DashboardActivity extends ActionBarActivity implements ResponseFragment.OnQuestionAskedListener, SocialFragment.OnSocialFragmentTouchedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new ResponseFragment(), "response")
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new SocialFragment(), "social")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onQuestionAsked(String string) {
        // do any processing on our end while Watson is working
        SocialFragment socialFrag = (SocialFragment) getSupportFragmentManager()
                .findFragmentByTag("social");
        // query and display related tweets in the social fragment
        socialFrag.query(string);
    }

    @Override
    public void onAnswerReceived(JSONObject answer) {
        // deal with the answer and evidence returned by Watson
        final ScrollView sv = (ScrollView)findViewById(R.id.scroll);
        sv.post(new Runnable() {
            public void run() {
                sv.smoothScrollBy(0, 500);
            }
        });
    }

    @Override
    public void onSocialFragmentTouched(Uri uri) {
        // launch the social activity
    }
}
