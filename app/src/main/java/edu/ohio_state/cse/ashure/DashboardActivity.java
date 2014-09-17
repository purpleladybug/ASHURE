package edu.ohio_state.cse.ashure;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


public class DashboardActivity extends ActionBarActivity implements ResponseFragment.OnQuestionAskedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new ResponseFragment())
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

    /**
     * This method receives the question text after the user presses the "Ask" button.
     * @param question The text the user provided before hitting "Ask"
     */
    @Override
    public void onQuestionAsked(String question) {
        // display the question provided by the fragment (for testing)
        TextView text = (TextView) this.findViewById(R.id.question_text_received);
        text.setText(question);
        // display the progress bar while Watson is "thinking" and our
        // pre-processing is taking place.
        ProgressBar progressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }
}
