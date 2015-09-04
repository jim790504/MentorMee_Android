package info.mentormee.jimmy.mentormee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by j1mmy on 2015-09-03.
 */
public class DetailUpdateActivity extends Activity {

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private Button returnButton;

    private String University;
    private String City;
    private String Faculty;
    private String Program;

    private String selection;

    String[] outputList = null;

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter;

    private int updatesuccess;
    InputStream is = null;

    int i;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailupdate);

        returnButton = (Button) findViewById(R.id.returnButton);

        returnButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                startActivity(i);
                finish();
            }

        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selection = extras.getString("selection");
        }

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.detailProfile );

        // Create and populate a List of planet names.
        String[] planets = new String[] { };
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll( Arrays.asList(planets) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, planetList);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);

        new loading(this).execute();

    }


    private class loading extends AsyncTask<String, Void, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        private Context context;

        public loading(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {

                University = getDefaults("University", getBaseContext());
                Faculty = getDefaults("Faculty", getBaseContext());
                Program = getDefaults("Program", getBaseContext());

                String rawData = "selection=" + selection;

                String link = "http://mentormee.info/dbTestConnect/programUpdate2.php";
                //String data = URLEncoder.encode("rawData", "UTF-8") + URLEncoder.encode(rawData, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(rawData);
                wr.flush();

                is = conn.getInputStream();

                //BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                is.close();

                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            try {

                // Add more planets. If you passed a String[] instead of a List<String>
                // into the ArrayAdapter constructor, you must not add more items.
                // Otherwise an exception will occur.
                JSONArray jArray = new JSONArray(result);
                for ( i = 0 ; i < jArray.length(); i ++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    listAdapter.add(jObj.getString(selection));
                }
            } catch (Exception e){
                return;
            }
        }
    }
}
