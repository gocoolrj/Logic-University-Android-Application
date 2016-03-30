package iss.nus.com.logicuniversitystationary;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class DeptApprove_RejectforDetails extends AppCompatActivity
        implements View.OnClickListener {

    ListView lvOrderDetails;
    LinearLayout llHeaderProgress;
    Button btApproveReq,btRejectreq;
    Toolbar mActionBarToolbar;
    String orderId;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_approve__rejectfor_details);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        btApproveReq = (Button) findViewById(R.id.btApproveReq);
        btRejectreq = (Button) findViewById(R.id.btRejectreq);
        llHeaderProgress= (LinearLayout)findViewById(R.id.llHeaderProgress);
        lvOrderDetails= (ListView)findViewById(R.id.lvOrderDetails);

        orderId = getIntent().getExtras().getString("DepartmentOrderId");

        btApproveReq.setOnClickListener(this);
        btRejectreq.setOnClickListener(this);

        new AsyncTask<Void, Void, List<ProductsDescription>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<ProductsDescription> doInBackground(Void... params) {
                return getOrderDetails();
            }

            @Override
            protected void onPostExecute(List<ProductsDescription> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row_qty,
                            new String[]{"productName","qty","units"},
                            new int[]{R.id.textView1,R.id.textView2,R.id.textView3});
                    lvOrderDetails.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeptApprove_RejectforDetails.this);

                    alertDialogBuilder
                            .setTitle("No items found")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    DeptApprove_RejectforDetails.this.onBackPressed();
                                    DeptApprove_RejectforDetails.this.finish();
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
        finish();
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
            startActivity(new Intent(this, DeptHomePage.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<ProductsDescription> getOrderDetails() {
        List<ProductsDescription> productsDescriptions = new ArrayList<>();

        int qty;
        String units;

        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "reqDetails/"+orderId);

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

                productsDescriptions.add(new ProductsDescription(
                        jObj.getString("ProductName").trim(),
                        qty,
                        units));

            }
        } catch (Exception e) {
            Log.e("Order Descriptions>>", "" + e.getMessage());
        }
        return productsDescriptions;
    }

    @Override
    public void onClick(View v) {

        JSONObject jObject = null;
        Boolean result = false;
        String message="", title = "Failed";

        if (v.getId() == btApproveReq.getId() || v.getId() == btRejectreq.getId()) {

            if (v.getId() == btApproveReq.getId()) {
                jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "approveReqOrder/" + orderId + "/Approved");
                message = "Request was accepted successful!";
            } else if (v.getId() == btRejectreq.getId()) {
                jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "approveReqOrder/" + orderId + "/Rejected");
                message = "Request was rejected successful!";
            }

            try {
                result = jObject.getBoolean("Result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(result) {
                title = "Success";
            } else {
                message = "Request failed!";
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            //MainActivity.this.finish();

                            DeptApprove_RejectforDetails.this.onBackPressed();
                            DeptApprove_RejectforDetails.this.finish();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
