package info.mentormee.jimmy.mentormee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by j1mmy on 2015-08-23.
 */
public class HomeScreen extends Activity {

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    int n = 10000;

    private Button button_updateProfile;

    InputStream is = null;
    InputStream is2 = null;
    InputStream is3 = null;
    String result = "";
    String result2 = "";
    String result3 = "";
    JSONObject jArray = null;
    private Bitmap bmp;
    private String fullName;
    private String Qrimage;

    private ImageView imageview;
    private TextView textview;
    private TextView textview2;
    private TextView textview3;

    private String programID;
    private String universityID;

    private String University;
    private String City;
    private String Faculty;
    private String Program;

    public void updateButton (View view) {
        Intent i = new Intent(getApplicationContext(), UpdateProfileActivity.class);
        startActivity(i);
        finish();
    }

    public void logoutButton (View view) {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        imageview = (ImageView) findViewById(R.id.imageView);
        textview = (TextView) findViewById(R.id.textView);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        new loading(this).execute();
    }


    private class loading extends AsyncTask<String, Void, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        private Context context;
        private String firstName;
        private String lastName;


        public loading(Context context) {
            this.context = context;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * * */
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground (String...arg0){
            try {
                String ID = getDefaults("ID", getBaseContext());

                String rawData = "userID=" + ID;

                String link = "http://mentormee.info/dbTestConnect/homeScreenUpdateAndroidTest.php";
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

                result = sb.toString();
                JSONArray jArray = new JSONArray(result);
                JSONObject jObj = jArray.getJSONObject(0);
                firstName = jObj.getString("FirstName");
                lastName = jObj.getString("LastName");
                Qrimage = jObj.getString("Picture");
                fullName = firstName + " " + lastName;

                URL url2 = new URL(Qrimage);
                HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bmp = BitmapFactory.decodeStream(input);


                String rawData2 = "userID=" + ID;

                String link3 = "http://mentormee.info/dbTestConnect/findAccountInfo_Android.php";
                //String data = URLEncoder.encode("rawData", "UTF-8") + URLEncoder.encode(rawData, "UTF-8");

                URL url3 = new URL(link3);
                URLConnection conn3 = url3.openConnection();

                conn3.setDoOutput(true);
                OutputStreamWriter wr3 = new OutputStreamWriter(conn3.getOutputStream());

                wr3.write(rawData2);
                wr3.flush();

                is2 = conn3.getInputStream();

                //BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2, "iso-8859-1"), 8);

                StringBuilder sb2 = new StringBuilder();
                String line2 = null;

                while ((line2 = reader2.readLine()) != null) {
                    sb2.append(line2 + "\n");
                }

                is2.close();

                result2 = sb2.toString();
                JSONArray jArray2 = new JSONArray(result2);
                JSONObject jObj2 = jArray2.getJSONObject(0);
                universityID = jObj2.getString("University_id");
                programID = jObj2.getString("Program_id");

                setDefaults("University_id", universityID, getBaseContext());
                setDefaults("Program_id", programID, getBaseContext());


                String rawData3 = "universityID=" + universityID + "&" + "programID=" + programID;

                String link4 = "http://mentormee.info/dbTestConnect/universityLookup.php";
                //String data = URLEncoder.encode("rawData", "UTF-8") + URLEncoder.encode(rawData, "UTF-8");

                URL url4 = new URL(link4);
                URLConnection conn4 = url4.openConnection();

                conn4.setDoOutput(true);
                OutputStreamWriter wr4 = new OutputStreamWriter(conn4.getOutputStream());

                wr4.write(rawData3);
                wr4.flush();

                is3 = conn4.getInputStream();

                //BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                BufferedReader reader3 = new BufferedReader(new InputStreamReader(is3, "iso-8859-1"), 8);

                StringBuilder sb3 = new StringBuilder();
                String line3 = null;

                while ((line3 = reader3.readLine()) != null) {
                    sb3.append(line3 + "\n");
                }

                is3.close();

                result3 = sb3.toString();
                JSONArray jArray3 = new JSONArray(result3);
                JSONObject jObj3 = jArray3.getJSONObject(0);
                University = jObj3.getString("University");
                City = jObj3.getString("City");
                JSONObject jObj4 = jArray3.getJSONObject(1);
                Faculty = jObj4.getString("Faculty");
                Program = jObj4.getString("Program");

                setDefaults("University", University, getBaseContext());
                setDefaults("Program", Program, getBaseContext());
                setDefaults("Faculty", Faculty, getBaseContext());
                setDefaults("City", City, getBaseContext());

                return sb3.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
                imageview.setImageBitmap(bmp);
                textview.setText(fullName);
                textview2.setText(University);
                textview3.setText(Program);
        }

    }

}