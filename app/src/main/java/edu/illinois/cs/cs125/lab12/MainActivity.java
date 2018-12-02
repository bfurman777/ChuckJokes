package edu.illinois.cs.cs125.lab12;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;


/**
 * Main screen for our API testing app.
 */
public final class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /**
     * hi.
     */
    private String joke = "THERE IS NO JOKE!";

    /**
     * Reader guy.
     */
    private TextToSpeech bobTheReader;

    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests, start first request
        requestQueue = Volley.newRequestQueue(this);
        startAPICall();

        bobTheReader = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if (status != TextToSpeech.ERROR) {
                    bobTheReader.setLanguage(Locale.UK);
                }
            }
        });


        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        final TextView jokeTextView = findViewById(R.id.jokeTextView);
        jokeTextView.setText("Press the button for a joke!");

        // Attach the handler to our UI button
        final Button startAPICall = findViewById(R.id.startAPICall);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                // show the joke from the previous click and get a new one
                jokeTextView.setText(joke);
                bobTheReader.speak(joke, TextToSpeech.QUEUE_FLUSH, null);
                startAPICall();
            }
        });

        // Make sure that our progress bar isn't spinning and style it a bit
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Make an API call.
     */
    void startAPICall() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://api.icndb.com/jokes/random?limitTo=[nerdy]",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {

                                JSONObject value = response.getJSONObject("value");
                                joke = value.getString("joke").replaceAll("&quot;", "\"");
                            } catch (Exception e) {
                                Log.d(TAG, "OOF!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" + e.toString());
                            }
                            //Log.d(TAG, response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {
                            Log.w(TAG, error.toString());
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
