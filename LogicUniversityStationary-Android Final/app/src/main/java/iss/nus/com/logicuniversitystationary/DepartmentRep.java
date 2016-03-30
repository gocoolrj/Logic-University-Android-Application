package iss.nus.com.logicuniversitystationary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.ProductsDescription;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class DepartmentRep extends AppCompatActivity implements View.OnClickListener {
    ListView lvDeptRep;
    LinearLayout llHeaderProgress;
    View.OnClickListener mOnClickListener;
    String disbursementId;
    TextView tvDisbursementId;
    CheckBox checkBox;
    Button btSubmitRep;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_rep);
        lvDeptRep = (ListView) findViewById(R.id.lvDeptRep);
        llHeaderProgress = (LinearLayout) findViewById(R.id.llHeaderProgress);
        tvDisbursementId = (TextView) findViewById(R.id.tvDisbursementId);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        btSubmitRep = (Button) findViewById(R.id.btSubmitRep);
        disbursementId = "";

        btSubmitRep.setOnClickListener(this);

         sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);
        if(sp.getString("UserId",null)!=null) {
            Globals.EmpId = sp.getString("UserId",null);
        } else {
            startActivity(new Intent(this,Login.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "You have accepted, this action cannot be undone later.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Undo", mOnClickListener);
                    snackbar.setActionTextColor(Color.parseColor("#32b6a8"));
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.DKGRAY);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(false);
            }
        };

        new AsyncTask<Void, Void, List<ProductsDescription>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<ProductsDescription> doInBackground(Void... params) {
                return getProductList();
            }

            @Override
            protected void onPostExecute(List<ProductsDescription> result) {
                if (result.size() > 0) {

                    final SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), result,
                            R.layout.double_row_hidden_qty,
                            new String[]{"productName","qty","units","productCode"},
                            new int[]{R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4});
                    lvDeptRep.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DepartmentRep.this);

                    alertDialogBuilder
                            .setTitle("Please come back after you have received your order")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    DepartmentRep.this.onBackPressed();
                                    DepartmentRep.this.finish();
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
        getMenuInflater().inflate(R.menu.menu_homepage, menu);

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

        return super.onOptionsItemSelected(item);
    }

    List<ProductsDescription> getProductList() {
        List<ProductsDescription> listProducts = new ArrayList<>();
        ProductsDescription pd;

        int qty;
        String units;

        try {
            JSONObject jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "disbursementList/"+Globals.EmpId);
            JSONArray jArray = jObject.getJSONArray("DList");

            disbursementId = jObject.getString("DisbursmentId");
            tvDisbursementId.setText("Disbursement #" + disbursementId);

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

                pd = new ProductsDescription(
                        jObj.getString("ProductId").trim(),
                        jObj.getString("ProductName").trim(),
                        jObj.getInt("Qty"),
                        units
                );

                listProducts.add(pd);
            }
        } catch (Exception e) {
            Log.e("LowStockInventory>>", "" + e.getMessage());
        }
        return listProducts;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btSubmitRep.getId()) {
            if (checkBox.isChecked()) {
                String result = "Accepting stationery failed!";
                String title = "Failed";
                if (getResult()) {
                    result = "You have successfully accepted the stationery";
                    title = "Success";
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setTitle(title)
                        .setMessage(result)
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
        }
    }

    Boolean getResult() {
        Boolean result = false;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            JSONObject jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "acceptDisbursement/" + disbursementId);
            result = jObject.getBoolean("Result");

        } catch (Exception e) {
            Log.e(">>>>>>>", "" + e.getMessage());
        }
        return result;
    }
}
