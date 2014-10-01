package edu.ohio_state.cse.ashure;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link edu.ohio_state.cse.ashure.ResponseFragment.OnQuestionAskedListener} interface
 * to handle interaction events.
 * Use the {@link ResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ResponseFragment extends Fragment {
    private String watsonResponse;
    private String errorResponse = "It looks like the network is having trouble. Please try your question again.";
    private TextView responseTextView;
    private ProgressBar progressBar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnQuestionAskedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResponseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResponseFragment newInstance(String param1, String param2) {
        ResponseFragment fragment = new ResponseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public ResponseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_response, container, false);
        final Button questionButton = (Button)v.findViewById(R.id.question_button);
        questionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText question = ((EditText)v.findViewById(R.id.question_area));
                final String questionString = question.getText().toString();
                onButtonPressed(questionString);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(questionButton.getWindowToken(), 0);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String question) {
        if (mListener != null) {
            responseTextView = (TextView)getActivity().findViewById(R.id.watson_response);
            responseTextView.setText("");
            // pass the question back to the dashboard activity
            mListener.onQuestionAsked(question);
            // query watson
            submitQuery(question);

        }
    }

    private void displayResponse(JSONObject formattedResponse) {
        responseTextView = (TextView)getActivity().findViewById(R.id.watson_response);
        // set the response text to the first answer given by Watson
        try {
            JSONArray answers = formattedResponse.getJSONArray("answers");
            responseTextView.setVisibility(View.VISIBLE);
            responseTextView.setText(answers.getJSONObject(0).getString("text"));
        } catch (JSONException e) {
            responseTextView.setText("I'm not sure... can you try rephrasing the question?");
        }
    }

    private void displayError() {
        responseTextView = (TextView)getActivity().findViewById(R.id.watson_response);
        responseTextView.setText(errorResponse);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnQuestionAskedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnQuestionAskedListener {
        public void onQuestionAsked(String string);
        public void onAnswerReceived(JSONObject answer);
    }

    public void submitQuery(String questionText) {
        progressBar = (ProgressBar)getActivity().findViewById(R.id.progress_bar);
        responseTextView = (TextView)getActivity().findViewById(R.id.watson_response);
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            responseTextView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            new WatsonTask().execute(questionText);
        } else {
            watsonResponse = "No network connection available.";
        }
    }

    private class WatsonTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            watsonResponse = errorResponse;
            String id = "osu_student7", passwd = "sRK6Ugnn";    // will be given

            //String question = "Was Doctor Zhivago shown in Russia?";
            String question = "";
            if (strings.length > 0) {
                question = strings[0];
            }

            URL watsonURL = null;
            try {
                watsonURL = new URL("https://watson-wdc01.ihost.com/instance/501/deepqa/v1/question");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn;


            try {
                conn = (HttpURLConnection) watsonURL.openConnection();
            } catch (Exception e) {
                return watsonResponse;
            }

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-SyncTimeout", "30");
            String auth = Base64.encodeToString((id + ":" + passwd).getBytes(), Base64.DEFAULT);
            conn.setRequestProperty("Authorization", "Basic " + auth);
            conn.setDoOutput(true);

            try {
                conn.getOutputStream().write(("{\"question\" : {\"questionText\":\"" + question + "\"}}").getBytes());
            } catch (IOException e) {
                return watsonResponse;
            }

            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } catch (IOException e) {
                return watsonResponse;
            }

            String line;
            try {
                line = in.readLine();
                if (line != null) {
                    watsonResponse = "";
                }
                while (line != null) {
                    watsonResponse += line;
                    line = in.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //System.out.println(watsonResponse);

            return watsonResponse;
            //return "This is a hardcoded response";
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            JSONObject wrapper;
            try {
                if (result != null) {
                    wrapper = new JSONObject(result);
                    if (result.equals(errorResponse)) {
                        displayError();
                        return;
                    }
                    JSONObject response = wrapper.getJSONObject("question");
                    // display the response
                    displayResponse(response);
                    // pass the response back to the dashboard activity
                    mListener.onAnswerReceived(response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
