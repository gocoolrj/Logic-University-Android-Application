package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.LowStockInventory;
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class LowStock extends AppCompatActivity {
    ListView lvlowStock;
    LinearLayout lvStockHeaderProgress;
    Toolbar mActionBarToolbar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_stock);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Low Stock");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        lvlowStock = (ListView) findViewById(R.id.lvlowStock);
        lvStockHeaderProgress = (LinearLayout) findViewById(R.id.lvStockHeaderProgress);

        new AsyncTask<Void, Void, List<ProductsDescription>>() {
            @Override
            protected void onPreExecute() {
                lvStockHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<ProductsDescription> doInBackground(Void... params) {
                return getLowStockList();
            }

            @Override
            protected void onPostExecute(List<ProductsDescription> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row_qty,
                            new String[]{"productName","qty","units"},
                            new int[]{R.id.textView1,R.id.textView2,R.id.textView3});
                    lvlowStock.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LowStock.this);

                    alertDialogBuilder
                            .setTitle("All products have sufficient quantity")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    LowStock.this.onBackPressed();
                                    LowStock.this.finish();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                lvStockHeaderProgress.setVisibility(View.GONE);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
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

    List<ProductsDescription> getLowStockList() {
        List<ProductsDescription> listLowStock = new ArrayList<>();
        ProductsDescription lsi;
        int qty;
        String units;
        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl+"lowStockItems");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                qty = jObj.getInt("Qty");
                units = jObj.getString("UnitOfMeasurement").trim();

                if(qty!=1 && !units.equalsIgnoreCase("Each")) {
                    if(units.endsWith("x"))
                        units+="es";
                    else
                        units+="s";
                }

                lsi = new ProductsDescription(
                        jObj.getString("ProductName").trim(),
                        qty,
                        units);

                listLowStock.add(lsi);
            }
        } catch (Exception e) {
            Log.e("LowStockInventory>>",""+e.getMessage());
        }
        return listLowStock;
    }
}
