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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;

public class CollectionRepresentativeDetails extends AppCompatActivity {
    ListView lvOrderedItems;
    Toolbar mActionBarToolbar;
    LinearLayout llHeaderProgress;
    String deptId;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_representative_details);

        lvOrderedItems= (ListView)findViewById(R.id.lvOrderedItems);
        llHeaderProgress = (LinearLayout) findViewById(R.id.llHeaderProgress);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        if(getIntent().hasExtra("DepartmentId")) {

            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mActionBarToolbar);
            getSupportActionBar().setTitle(getIntent().getExtras().getString("RepresentativeName"));
            getSupportActionBar().setSubtitle(getIntent().getExtras().getString("DepartmentName"));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            deptId = getIntent().getExtras().getString("DepartmentId");

            new AsyncTask<Void, Void, List<ProductsDescription>>() {
                @Override
                protected void onPreExecute() {
                    llHeaderProgress.setVisibility(View.VISIBLE);
                    super.onPreExecute();
                }

                @Override
                protected List<ProductsDescription> doInBackground(Void... params) {
                    return getProducts(deptId);
                }

                @Override
                protected void onPostExecute(List<ProductsDescription> result) {
                    Log.i(">>", "" + result.size());
                    if (result.size() > 0) {
                        final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                                R.layout.double_row_hidden_qty,
                                new String[]{"productName","qty","units","productCode"},
                                new int[]{R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4});
                        lvOrderedItems.setAdapter(adapter);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionRepresentativeDetails.this);

                        alertDialogBuilder
                                .setTitle("No orders for the selected representative")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        //MainActivity.this.finish();

                                        CollectionRepresentativeDetails.this.onBackPressed();
                                        CollectionRepresentativeDetails.this.finish();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    llHeaderProgress.setVisibility(View.GONE);
                }
            }.execute();
        }
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

    private List<ProductsDescription> getProducts(String id) {
        List<ProductsDescription> listProducts = new ArrayList<>();
        ProductsDescription productsDescription;
        int qty;
        String units;

        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "repDisb/" + id);

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

                productsDescription = new ProductsDescription(
                        jObj.getString("ProductCode").trim(),
                        jObj.getString("ProductName").trim(),
                        qty,
                        units);

                listProducts.add(productsDescription);
            }
        } catch (Exception e) {
            Log.e("AllOrders.java >>", "" + e.getMessage());
        }
        return listProducts;
    }
}
