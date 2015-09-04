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
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class SignupActivity extends Activity {

    //private EditText usernameField, passwordField, confirmPasswordField;
    private EditText usernameField, passwordField, confirmPasswordField;
    public static final String PREFS_NAME = "MyApp_Settings";

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameField = (EditText) findViewById(R.id.editText1);
        passwordField = (EditText) findViewById(R.id.editText2);
        confirmPasswordField = (EditText) findViewById(R.id.editText3);
    }

    public void signupButton (View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        new Signup(this).execute(username, password, confirmPassword);
    }

    public void existingUser (View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    private class Signup extends AsyncTask<String, Void, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON Node names
        private static final String TAG_SUCCESS = "success";

        private Context context;
        private int signupsuccess;
        private String postID;
        private String postData;

        public Signup(Context context) {
        this.context = context;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Signing up...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground (String...arg0){
            try {
                String username = (String) arg0[0];
                String password = (String) arg0[1];
                String confirmPassword = (String) arg0[2];

                String rawData = "email=" + username + "&" + "password=" + password + "&" + "password_c=" + confirmPassword + "&" + "mentorStatus=Mentor";

                String link = "http://mentormee.info/dbTestConnect/signupScript4.php";
                //String data = URLEncoder.encode("rawData", "UTF-8") + URLEncoder.encode(rawData, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(rawData);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                JSONObject jObj = new JSONObject(line);
                signupsuccess = jObj.getInt("success");

                if (signupsuccess == 1) {

                    String email = username;
                    String fetchUserID2 = "email=" + email;

                    String link2 = "http://mentormee.info/dbTestConnect/fetchUserID2.php";

                    URL url2 = new URL(link2);
                    URLConnection conn2 = url2.openConnection();

                    conn2.setDoOutput(true);
                    OutputStreamWriter wr2 = new OutputStreamWriter(conn2.getOutputStream());

                    wr2.write(fetchUserID2);
                    wr2.flush();

                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));

                    StringBuilder sb2 = new StringBuilder();
                    String line2 = null;

                    // Read Server Response
                    while ((line2 = reader2.readLine()) != null) {
                        sb2.append(line2);
                        break;
                    }

                    JSONArray jArray = new JSONArray(line2);
                    JSONObject jObj2 = jArray.getJSONObject(0);
                    postID = jObj2.getString("ID");

                    String createNewAccount = "userID=" + postID;

                    SharedPreferences PREFS = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                    setDefaults("ID", postID, getBaseContext());

                    String link3 = "http://mentormee.info/dbTestConnect/createNewAccount.php";

                    URL url3 = new URL(link3);
                    URLConnection conn3 = url3.openConnection();

                    conn3.setDoOutput(true);
                    OutputStreamWriter wr3 = new OutputStreamWriter(conn3.getOutputStream());

                    wr3.write(createNewAccount);
                    wr3.flush();

                    BufferedReader reader3 = new BufferedReader(new InputStreamReader(conn3.getInputStream()));

                    StringBuilder sb3 = new StringBuilder();
                    String line3 = null;

                    // Read Server Response
                    while ((line3 = reader3.readLine()) != null) {
                        sb3.append(line3);
                        break;
                    }

                    JSONArray jArray2 = new JSONArray(line3);
                    JSONObject jObj3 = jArray2.getJSONObject(0);
                    postData = jObj3.getString("Capability_id");
                    String createPrimaryCapabilityEntry = "primaryCapabilityID=" + postData + "&" + "userID=" + postID;

                    String link4 = "http://mentormee.info/dbTestConnect/createPrimaryCapabilityEntry.php";

                    URL url4 = new URL(link4);
                    URLConnection conn4 = url4.openConnection();

                    conn4.setDoOutput(true);
                    OutputStreamWriter wr4 = new OutputStreamWriter(conn4.getOutputStream());

                    wr4.write(createPrimaryCapabilityEntry);
                    wr4.flush();

                    BufferedReader reader4 = new BufferedReader(new InputStreamReader(conn4.getInputStream()));

                    StringBuilder sb4 = new StringBuilder();
                    String line4 = null;

                    // Read Server Response
                    while ((line4 = reader4.readLine()) != null) {
                        sb4.append(line4);
                        break;
                    }

                }

                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

    }
}