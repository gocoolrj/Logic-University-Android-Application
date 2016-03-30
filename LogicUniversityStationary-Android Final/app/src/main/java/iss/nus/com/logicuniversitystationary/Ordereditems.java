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
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class Ordereditems extends AppCompatActivity implements View.OnClickListener ,AdapterView.OnItemClickListener {
    ListView lvOrderedItems;
    Toolbar mActionBarToolbar;
    LinearLayout llHeaderProgress;
    String orderId, productId;
    Button sendDisbursement;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordereditems);
        lvOrderedItems= (ListView)findViewById(R.id.lvOrderedItems);
        llHeaderProgress = (LinearLayout) findViewById(R.id.llHeaderProgress);
        sendDisbursement = (Button) findViewById(R.id.sendDisbursement);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        lvOrderedItems.setOnItemClickListener(this);
        sendDisbursement.setOnClickListener(this);

        if(getIntent().hasExtra("OrderId")) {

            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mActionBarToolbar);
            getSupportActionBar().setTitle("Order #" + getIntent().getExtras().getString("OrderId"));
            getSupportActionBar().setSubtitle(getIntent().getExtras().getString("DepartmentName"));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            orderId = getIntent().getExtras().getString("OrderId");

            new AsyncTask<Void, Void, List<ProductsDescription>>() {
                @Override
                protected void onPreExecute() {
                    llHeaderProgress.setVisibility(View.VISIBLE);
                    super.onPreExecute();
                }

                @Override
                protected List<ProductsDescription> doInBackground(Void... params) {
                    return getProducts(orderId);
                }

                @Override
                protected void onPostExecute(List<ProductsDescription> result) {
                    Log.i(">>", "" + result.size());
                    if (result.size() > 0) {
                        final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                                R.layout.double_row_hidden_bin,
                                new String[]{"productName", "qty", "productCode","units"},
                                new int[]{R.id.textView1, R.id.textView2, R.id.textView5, R.id.textView3});
                        lvOrderedItems.setAdapter(adapter);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Ordereditems.this);

                        alertDialogBuilder
                                .setTitle("No items found")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        //MainActivity.this.finish();

                                        Ordereditems.this.onBackPressed();
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
    protected void onResume() {

        new AsyncTask<Void, Void, List<ProductsDescription>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<ProductsDescription> doInBackground(Void... params) {
                return getProducts(orderId);
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
                }
                llHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();

        super.onResume();
    }

    private List<ProductsDescription> getProducts(String id) {
        List<ProductsDescription> listProducts = new ArrayList<>();
        ProductsDescription productsDescription;

        int qty;
        String units;

        String url = Globals.baseurl + "orderItems/"+id;

        if(getIntent().getExtras().getBoolean("Unfulfilled"))
            url = Globals.baseurl + "unfulfilledItems/"+id;

        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(url);

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
                        jObj.getString("ProductId").trim(),
                        jObj.getString("ProductName").trim(),
                        jObj.getInt("Qty"),
                        units
                        );

                listProducts.add(productsDescription);
            }
        } catch (Exception e) {
            Log.e("AllOrders.java >>", "" + e.getMessage());
        }
        return listProducts;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        this.finish();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ProductsDescription p = (ProductsDescription)parent.getItemAtPosition(position);
        productId = p.get("productCode").toString();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Edit Quantity");
        alertDialog.setCancelable(false);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {

                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected void onPreExecute() {
                                llHeaderProgress.setVisibility(View.VISIBLE);
                                super.onPreExecute();
                            }

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                return updateQuantity(productId, input.getText().toString());
                            }

                            @Override
                            protected void onPostExecute(Boolean result) {

                                if (result == true) {
                                    onResume();
                                } else {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Ordereditems.this);

                                    alertDialogBuilder
                                            .setTitle(input.getText().toString()+" is higher than available")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // if this button is clicked, close
                                                    // current activity
                                                    //MainActivity.this.finish();

                                                }
                                            });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                                llHeaderProgress.setVisibility(View.GONE);
                            }
                        }.execute();
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    Boolean updateQuantity(String id, String qty) {
        Boolean result = false;

        try {
                JSONObject jObj = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "updateIssueQty/"+orderId+"/"+id+"/"+qty);

                result = jObj.getBoolean("Result");
        } catch (Exception e) {
            Log.e("AllOrders.java >>", "" + e.getMessage());
        }
        Log.e("update quantity >>", "" + result);
        return result;
    }

    @Override
    public void onClick(View v) {

        Boolean result = false;

        try {
            JSONObject jObj = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "submitOrderForDisb/"+orderId);

            Log.i("url>>",Globals.baseurl + "submitOrderForDisb/"+orderId+"");

            result = jObj.getBoolean("Result");
        } catch (Exception e) {
            Log.e("AllOrders.java >>", "" + e.getMessage());
        }

        String title = " Failed";

        if(result) {
            title = " Succeeded";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setMessage("Disbursement" + title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        finish();
                        startActivity(new Intent(getBaseContext(), OrderList.class));


                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
