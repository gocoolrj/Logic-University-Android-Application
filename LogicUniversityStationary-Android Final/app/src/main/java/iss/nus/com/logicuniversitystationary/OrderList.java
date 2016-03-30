package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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

import iss.nus.com.logicuniversitystationary.Data.DepartmentOrder;
import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class OrderList extends AppCompatActivity implements AdapterView.OnItemClickListener, TabLayout.OnTabSelectedListener {
    ListView lvOrderList;
    LinearLayout llHeaderProgress;
    Toolbar mActionBarToolbar;
    TabLayout tablayout;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        tablayout = (TabLayout) findViewById(R.id.tablayout);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Department Orders");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tablayout.addTab(tablayout.newTab().setText("New Orders"));
        tablayout.addTab(tablayout.newTab().setText("Unfulfilled"));

        tablayout.setTabTextColors(Color.parseColor("#999999"), Color.parseColor("#ffffff"));

        lvOrderList= (ListView)findViewById(R.id.lvOrderList);
        llHeaderProgress= (LinearLayout)findViewById(R.id.llHeaderProgress);

        lvOrderList.setOnItemClickListener(this);
        tablayout.setOnTabSelectedListener(this);

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
                            new String[]{"orderId","departmentName"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvOrderList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderList.this);

                    alertDialogBuilder
                            .setTitle("No orders found")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    OrderList.this.onBackPressed();
                                    OrderList.this.finish();
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
        } else if (id == R.id.action_home) {
            startActivity(new Intent(this, Homepage.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<DepartmentOrder> getDepartmentOrders() {
        List<DepartmentOrder> listDepartmentOrders = new ArrayList<>();
        DepartmentOrder deptOrd;

        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl + "order/"+Globals.EmpId);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                deptOrd = new DepartmentOrder(
                        jObj.getString("OrderID").trim(),
                        jObj.getString("DepartmentName").trim());

                listDepartmentOrders.add(deptOrd);
            }
        } catch (Exception e) {
            Log.e("OrderList.java >>", "" + e.getMessage());
        }
        return listDepartmentOrders;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DepartmentOrder e = (DepartmentOrder)parent.getItemAtPosition(position);
        String orderId = e.get("orderId");
        String deptName = e.get("departmentName");

        Intent i = new Intent(this, Ordereditems.class);
        i.putExtra("OrderId",orderId);
        i.putExtra("DepartmentName", deptName);

        startActivity(i);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 1)
            startActivity(new Intent(this,OrderListUnfulfilled.class));
        finish();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
