package info.mentormee.jimmy.mentormee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

    int n = 10000;

    InputStream is = null;
    String result = "";
    JSONObject jArray = null;
    private Bitmap bmp;
    private String username;
    private String fullName;
    private String Qrimage;

    private ImageView imageview;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        imageview = (ImageView) findViewById(R.id.imageView);
        textview = (TextView) findViewById(R.id.textView);
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
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    username = extras.getString("email");
                }

                String rawData = "userID=" + username;

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

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
                imageview.setImageBitmap(bmp);
                textview.setText(fullName);
        }
    }

}