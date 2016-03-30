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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.DepartmentOrder;
import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class DeptApprove_Reject extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView lvDepartmentOrders;
    LinearLayout llHeaderProgress;
    Toolbar mActionBarToolbar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_approve__reject);
        lvDepartmentOrders= (ListView)findViewById(R.id.lvDepartmentOrders);
        llHeaderProgress= (LinearLayout)findViewById(R.id.llHeaderProgress);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Pending Approval");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        registerForContextMenu(lvDepartmentOrders);

        lvDepartmentOrders.setOnItemClickListener(this);

        new AsyncTask<Void, Void, List<DepartmentOrder>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<DepartmentOrder> doInBackground(Void... params) {
                return getDepartmentOrders();
            }

            @Override
            protected void onPostExecute(List<DepartmentOrder> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row,
                            new String[]{"departmentName","orderId"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvDepartmentOrders.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeptApprove_Reject.this);

                    alertDialogBuilder
                            .setTitle("No orders found")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    DeptApprove_Reject.this.onBackPressed();
                                    DeptApprove_Reject.this.finish();
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
    protected void onResume() {
        new AsyncTask<Void, Void, List<DepartmentOrder>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<DepartmentOrder> doInBackground(Void... params) {
                return getDepartmentOrders();
            }

            @Override
            protected void onPostExecute(List<DepartmentOrder> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.double_row,
                            new String[]{"departmentName","orderId"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvDepartmentOrders.setAdapter(adapter);
                }
                llHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();

        super.onResume();
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.approval_context_menu, menu);
        return;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        JSONObject jObject = null;
        Boolean result = false;
        String message="", title = "Failed";

        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        DepartmentOrder pd = (DepartmentOrder) lvDepartmentOrders.getItemAtPosition(acmi.position);

        //noinspection SimplifiableIfStatement
        if (id == R.id.approve) {
            jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "approveReqOrder/" + pd.get("orderId") + "/Approved");
            message = "Request was accepted successful!";
        }
        else  if (id == R.id.reject) {
            jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "approveReqOrder/" + pd.get("orderId") + "/Rejected");
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
                        onResume();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return super.onContextItemSelected(item);
    }

    List<DepartmentOrder> getDepartmentOrders() {
        List<DepartmentOrder> departmentOrders = new ArrayList<>();
        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "reqOrders/"+Globals.EmpId);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                departmentOrders.add(new DepartmentOrder(jObj.getString("RequestOrderId").trim(),
                        jObj.getString("EmployeeName").trim()
                        ));
            }
        } catch (Exception e) {
            Log.e("DeptApprove_Reject>>", "" + e.getMessage());
        }
        return departmentOrders;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DepartmentOrder c = (DepartmentOrder)parent.getItemAtPosition(position);
        String orderNumber = c.get("orderId").trim();

        Intent i = new Intent(this, DeptApprove_RejectforDetails.class);
        i.putExtra("DepartmentOrderId",orderNumber);
        startActivity(i);
    }
}
