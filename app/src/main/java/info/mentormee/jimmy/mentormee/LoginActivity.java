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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends Activity {

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //private EditText usernameField, passwordField, confirmPasswordField;
    private EditText usernameField, passwordField;
    private String postID;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.editText1);
        passwordField = (EditText) findViewById(R.id.editText2);
    }

    public void loginButton (View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        new login(this).execute(username, password);
    }

    public void backButton (View view) {
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(i);
        finish();
    }

    private class login extends AsyncTask<String, Void, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON Node names
        private static final String TAG_SUCCESS = "success";
        private int loginsuccess;

        private Context context;

        public login(Context context) {
            this.context = context;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground (String...arg0){
            try {
                String username = (String) arg0[0];
                String password = (String) arg0[1];

                email = username;

                String rawData = "email=" + username + "&" + "password=" + password;

                String link = "http://mentormee.info/dbTestConnect/loginTest.php";
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
                loginsuccess = jObj.getInt("success");

                if (loginsuccess == 1) {
                    String email = username;
                    String fetchUserID = "email=" + email;

                    String link2 = "http://mentormee.info/dbTestConnect/fetchUserID.php";

                    URL url2 = new URL(link2);
                    URLConnection conn2 = url2.openConnection();

                    conn2.setDoOutput(true);
                    OutputStreamWriter wr2 = new OutputStreamWriter(conn2.getOutputStream());

                    wr2.write(fetchUserID);
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

                    setDefaults("ID", postID, context);
                }

                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){

            if (loginsuccess == 1) {
                Context context = getApplicationContext();
                CharSequence text = "Successful Login!!! :)";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                i.putExtra("email", email);
                startActivity(i);
                finish();
            } else {
                Context context = getApplicationContext();
                CharSequence text = "YOU FAIL!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
                finish();
            }

        }

    }


}