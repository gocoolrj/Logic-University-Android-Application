package iss.nus.com.logicuniversitystationary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
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
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;

public class AllOrders extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lvAllOrders;
    LinearLayout llHeaderProgress;
    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Retrieval List");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvAllOrders = (ListView) findViewById(R.id.lvAllOrders);
        llHeaderProgress = (LinearLayout) findViewById(R.id.llHeaderProgress);

        lvAllOrders.setOnItemClickListener(this);

        registerForContextMenu(lvAllOrders);

        new AsyncTask<Void, Void, List<ProductsDescription>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<ProductsDescription> doInBackground(Void... params) {
                return getAllOrders();
            }

            @Override
            protected void onPostExecute(List<ProductsDescription> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), result,
                            R.layout.double_row_hidden_bin,
                            new String[]{"productName", "qty", "productCode","units","bin"},
                            new int[]{R.id.textView1, R.id.textView2, R.id.textView5, R.id.textView3, R.id.textView4});
                    lvAllOrders.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AllOrders.this);

                    alertDialogBuilder
                            .setTitle("No orders")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    AllOrders.this.onBackPressed();
                                    AllOrders.this.finish();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.context_menu_adjustment, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        ProductsDescription pd = (ProductsDescription) lvAllOrders.getItemAtPosition(acmi.position);

        Intent i = new Intent(this, AdjustmentVoucher.class);
        i.putExtra("ProductCode", (String) pd.get("productCode"));
        i.putExtra("Quantity", (String) pd.get("quantity"));
        i.putExtra("ProductName",(String) pd.get("productName"));

        startActivity(i);
        return super.onContextItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    List<ProductsDescription> getAllOrders() {
        List<ProductsDescription> listAllOrders = new ArrayList<>();
        ProductsDescription productsDescription;
        int qty;
        String units;

        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "collections/"+Globals.EmpId);

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
                        units,
                        "#"+jObj.getString("BinNo").trim());

                listAllOrders.add(productsDescription);
            }
        } catch (Exception e) {
            Log.e("AllOrders.java >>", "" + e.getMessage());
        }
        return listAllOrders;
    }
}
