package iss.nus.com.logicuniversitystationary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.Representatives;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;

public class CollectionRepresentatives extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lvCollectionRepresentatives;
    LinearLayout llHeaderProgress;
    String collectionPointName;
    Toolbar mActionBarToolbar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_representatives);
        lvCollectionRepresentatives= (ListView)findViewById(R.id.lvCollectionRepresentatives);
        llHeaderProgress= (LinearLayout)findViewById(R.id.llHeaderProgress);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        collectionPointName = getIntent().getExtras().getString("CollectionPoint");

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(collectionPointName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (collectionPointName.contains(" "))
            collectionPointName = collectionPointName.replaceAll(" ","%20");

        lvCollectionRepresentatives.setOnItemClickListener(this);

        new AsyncTask<Void, Void, List<Representatives>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<Representatives> doInBackground(Void... params) {
                return getRepresentatives();
            }

            @Override
            protected void onPostExecute(List<Representatives> result) {
                Log.i(">>",""+result.size());
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row_hidden,
                            new String[]{"RepresentativeName","DepartmentName","DepartmentId"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvCollectionRepresentatives.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionRepresentatives.this);

                    alertDialogBuilder
                            .setTitle("No representative for the selected collection point")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    CollectionRepresentatives.this.onBackPressed();
                                    CollectionRepresentatives.this.finish();
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
        } else if (id == R.id.action_home) {
            startActivity(new Intent(this, Homepage.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Representatives r = (Representatives)parent.getItemAtPosition(position);
        String deptId = r.get("DepartmentId").trim();
        String repName = r.get("RepresentativeName").trim();
        String deptName = r.get("DepartmentName").trim();

        Intent i = new Intent(this, CollectionRepresentativeDetails.class);
        i.putExtra("DepartmentId",deptId);
        i.putExtra("RepresentativeName",repName);
        i.putExtra("DepartmentName",deptName);

        startActivity(i);
    }

    List<Representatives> getRepresentatives() {
        List<Representatives> representatives = new ArrayList<>();
        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "repName/"+collectionPointName);
            Log.i("jarray",jArray.toString()+"");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                representatives.add(new Representatives(jObj.getString("RepName").trim(),
                        jObj.getString("DepartmentName").trim(),
                        jObj.getString("DepartmentID").trim()));
            }
        } catch (Exception e) {
            Log.e("Representatives>>", "" + e.getMessage());
        }
        return representatives;
    }
}
