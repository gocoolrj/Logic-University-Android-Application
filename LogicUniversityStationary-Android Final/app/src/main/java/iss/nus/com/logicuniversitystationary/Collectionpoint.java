package iss.nus.com.logicuniversitystationary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.CollectionPoint;
import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class Collectionpoint extends AppCompatActivity implements OnItemClickListener{

    ListView lvCollectionPoint;
    LinearLayout llHeaderProgress;
    Toolbar mActionBarToolbar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectionpoint);
        lvCollectionPoint= (ListView)findViewById(R.id.lvCollectionPoint);
        llHeaderProgress= (LinearLayout)findViewById(R.id.llHeaderProgress);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Collection Points");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvCollectionPoint.setOnItemClickListener(this);

        new AsyncTask<Void, Void, List<CollectionPoint>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<CollectionPoint> doInBackground(Void... params) {
                return getCollectionPoint();
            }

            @Override
            protected void onPostExecute(List<CollectionPoint> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row,
                            new String[]{"CollPointName","CollPointTime"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvCollectionPoint.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Collectionpoint.this);

                    alertDialogBuilder
                            .setTitle("No orders disbursed at the moment")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    Collectionpoint.this.onBackPressed();
                                    Collectionpoint.this.finish();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                llHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inner_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            sp.edit().clear();
            startActivity(new Intent(this, Login.class));
            finish();
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            return true;
        }
        else if (id == R.id.action_home) {
            startActivity(new Intent(this, Homepage.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<CollectionPoint> getCollectionPoint() {
        List<CollectionPoint> collectionPoints = new ArrayList<>();
        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "collectionPoint/"+Globals.EmpId);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                collectionPoints.add(new CollectionPoint(jObj.getString("CollPointName"),
                        jObj.getString("CollPointTime")));
            }
        } catch (Exception e) {
            Log.e("Collection Points>>", "" + e.getMessage());
        }
        return collectionPoints;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CollectionPoint c = (CollectionPoint)parent.getItemAtPosition(position);
        String collPointName = c.get("CollPointName").trim();

        Intent i = new Intent(this, CollectionRepresentatives.class);
        i.putExtra("CollectionPoint",collPointName);

        startActivity(i);

        Log.i("Collection >>",collPointName+"");
    }
}
