package info.mentormee.jimmy.mentormee;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends ListActivity {

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    private String University;
    private String City;
    private String Faculty;
    private String Program;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ArrayList<Map<String, String>> list = buildData();
        String[] from = { "Info", "Value" };
        int[] to = { android.R.id.text1, android.R.id.text2 };

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to){

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(0xffff0000);

                return view;
            };
        };

        setListAdapter(adapter);
    }

    private ArrayList<Map<String, String>> buildData() {

        University = getDefaults("University", getBaseContext());
        Faculty = getDefaults("Faculty", getBaseContext());
        Program = getDefaults("Program", getBaseContext());

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(putData("University", University));
        list.add(putData("Faculty", Faculty));
        list.add(putData("Program", Program));
        return list;
    }

    private HashMap<String, String> putData(String name, String purpose) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("Info", name);
        item.put("Value", purpose);
        return item;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, String> map = (Map<String, String>) this.getListAdapter().getItem(position);
        String info = map.get("Info");
        String value = map.get("Value");
        Toast.makeText(this, info + " " + value + " selected", Toast.LENGTH_LONG).show();

        try {
            Intent i = new Intent(getApplicationContext(), DetailUpdateActivity.class);
            i.putExtra("selection", info);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onListItemClick(l, v, position, id);
    }

}
