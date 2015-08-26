package info.mentormee.jimmy.mentormee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends Activity {

    //private EditText usernameField, passwordField, confirmPasswordField;
    private EditText usernameField, passwordField;

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
        @Override protected void onPreExecute () {
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