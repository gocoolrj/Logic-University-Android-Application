package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
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
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.EmpList;
import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;

public class ProductList extends AppCompatActivity{

    ListView lvproducts;
    LinearLayout llProductsProgress;
    SimpleAdapter adapter = null;
    String pname;
    Toolbar mActionBarToolbar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        lvproducts = (ListView) findViewById(R.id.lvproducts);
        llProductsProgress = (LinearLayout)findViewById(R.id.llProductsProgress);
        pname = getIntent().getExtras().getString("pName");

        getSupportActionBar().setTitle("Search: "+pname);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductsDescription p = (ProductsDescription)parent.getItemAtPosition(position);


                SharedPreferences sharedpreferences = getSharedPreferences("productSearch",MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("productName", (String) p.get("productName"));
                editor.putString("productCode", (String) p.get("productCode"));
                editor.commit();
                Log.i("prod pref>>>", "" + sharedpreferences.getString("productCode", ""));
                onBackPressed();
            }
        });
        new AsyncTask<Void, Void, List<ProductsDescription>>() {
            @Override
            protected void onPreExecute() {
                llProductsProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<ProductsDescription> doInBackground(Void... params) {
                return getProductsList();
            }

            @Override
            protected void onPostExecute(List<ProductsDescription> result) {
                if (result.size() > 0) {
                    adapter = new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row_hidden,
                            new String[]{"productName","productCode"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvproducts.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductList.this);

                    alertDialogBuilder
                            .setTitle("No products found")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    ProductList.this.onBackPressed();
                                    ProductList.this.finish();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                llProductsProgress.setVisibility(View.GONE);
            }
        }.execute();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    List<ProductsDescription> getProductsList() {
        List<ProductsDescription> listProducts = new ArrayList<>();
        ProductsDescription pd;
        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl+"products/"+pname);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                pd = new ProductsDescription(
                        jObj.getString("ProductCode").trim(),
                        jObj.getString("ProductName").trim());

                listProducts.add(pd);
            }
        } catch (Exception e) {
            Log.e("Products List>>", "" + e.getMessage());
        }
        return listProducts;
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


}
