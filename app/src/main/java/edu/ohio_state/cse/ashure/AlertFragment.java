package edu.ohio_state.cse.ashure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Woods on 10/14/14.
 */
public class AlertFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_alert, parent, false);

        final TextView clickableText = (TextView)v.findViewById(R.id.alert_message);
        clickableText.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                
                Intent i = new Intent(getActivity(),AlertActivity.class);
                startActivity(i);
            }
        });

        return v;
    }


}
