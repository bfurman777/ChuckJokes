package edu.illinois.cs.cs125.lab12;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Locale;


/**
 * Main screen for our API testing app.
 */
public final class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** The joke from the API call is stored here. */
    private String joke = "THERE IS NO JOKE!";

    /** The joke from the spinner dropdown menu is stored here. */
    private String jokeType = "THERE IS NO JOKE TYPE!";

    /** Types of jokes to get. */
    private final String[] jokeTypes = new String[]{"Nerdy", "Explicit", "All"};

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
        getChuckJoke();

        // Bob will be our TextToSpeech spokesperson today
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

        final Spinner dropdown = findViewById(R.id.jokeTypeSpinner);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, jokeTypes);
        dropdown.setAdapter(adapter);

        // Make sure that our progress bar isn't spinning and style it a bit
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // Attach the handler to our UI button
        final Button jokeButton = findViewById(R.id.jokeButton);
        jokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                jokeType = dropdown.getSelectedItem().toString();   //TODO FIX TIMING ERROR!!!!!!!!!!!!!!!!!
                getChuckJoke();
                jokeTextView.setText(joke);
                bobTheReader.speak(joke, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    /** Get the extension for the web api joke based on the type selected in Spinner.
     * @return
     */
    private String getJokeTypeExtension() {
        if (jokeType == "Nerdy") {
            return "limitTo=[nerdy]";
        }
        if (jokeType == "Explicit") {
            return "limitTo=[explicit]";
        }
        return "";
    }

    /** Make an API call.*/
    private void getChuckJoke() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://api.icndb.com/jokes/random?" + getJokeTypeExtension(),
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
