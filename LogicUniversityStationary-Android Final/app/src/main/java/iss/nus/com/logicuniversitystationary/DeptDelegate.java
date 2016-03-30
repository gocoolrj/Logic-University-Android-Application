package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.EmpList;
import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.Data.LowStockInventory;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class DeptDelegate extends Activity
        implements AdapterView.OnItemClickListener, View.OnClickListener, DatePicker.OnDateChangedListener {

    DatePicker dtPickerFrom,dtPickerTo;
    TextView etEmplDelegateName, tvRow;
    ListView lvemployeedelegate;
    LinearLayout llHeaderProgress;
    Button btDelegateEmployee;
    View previouslySelectedItem = null;
    String selectedId="";
    long from,to;
    String fromDate,toDate;
    EmpList e = null;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_delegate);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        etEmplDelegateName = (TextView) findViewById(R.id.etEmplDelegateName);
        lvemployeedelegate= (ListView)findViewById(R.id.lvemployeedelegate);
        llHeaderProgress= (LinearLayout)findViewById(R.id.llHeaderProgress);
        btDelegateEmployee = (Button) findViewById(R.id.btDelegateEmployee);
        dtPickerFrom = (DatePicker)findViewById(R.id.dtPickerFrom);
        dtPickerTo = (DatePicker)findViewById(R.id.dtPickerTo);

        dtPickerFrom.setMinDate(System.currentTimeMillis() - 1000);
        dtPickerTo.setMinDate(System.currentTimeMillis() - 1000);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        dtPickerFrom.init(day, month, year, this);

        new AsyncTask<Void, Void, List<EmpList>>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<EmpList> doInBackground(Void... params) {
                return getEmployeeList();
            }

            @Override
            protected void onPostExecute(List<EmpList> result) {
                if (result.size() > 0) {
                    final SimpleAdapter adapter= new SimpleAdapter(getApplicationContext(),result,
                            R.layout.row,
                            new String[]{"employeeName","employeeId"},
                            new int[]{R.id.textView1,R.id.textView2});
                    lvemployeedelegate.setAdapter(adapter);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeptDelegate.this);

                    alertDialogBuilder
                            .setTitle("No employees in your department")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();

                                    DeptDelegate.this.onBackPressed();
                                    DeptDelegate.this.finish();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                llHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();

        lvemployeedelegate.setOnItemClickListener(this);

        btDelegateEmployee.setOnClickListener(this);

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
        }
        else if (id == R.id.action_home) {
            startActivity(new Intent(this, DeptHomePage.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        e = (EmpList)parent.getItemAtPosition(position);
        etEmplDelegateName.setText("Delegate: "+e.get("employeeName"));


        selectedId = e.get("employeeId");
    }

    List<EmpList> getEmployeeList() {
        List<EmpList> listEmployees = new ArrayList<>();
        EmpList el;
        try {
            JSONArray jArray = JSONParser.getJSONArrayFromUrl(Globals.baseurl+"employeesList/"+Globals.EmpId);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);

                el = new EmpList(
                        jObj.getString("EmployeeName"),
                        jObj.getString("EmployeeId"));

                listEmployees.add(el);
            }
        } catch (Exception e) {
            Log.e("LowStockInventory>>", "" + e.getMessage());
        }
        return listEmployees;
    }



    @Override
    public void onClick(View v) {

        if(v.getId() == btDelegateEmployee.getId()) {
             from = Date.UTC(dtPickerFrom.getYear(),
                     dtPickerFrom.getMonth(),
                     dtPickerFrom.getDayOfMonth(),
                     00, 00, 00);

             to = Date.UTC(dtPickerTo.getYear(),
                     dtPickerTo.getMonth(),
                     dtPickerTo.getDayOfMonth(),
                     00, 00, 00);

           //  fromDate = "/date("+from+")/";
            // toDate = "/date("+to+")/";

            fromDate = dtPickerFrom.getYear()+"-"+String.format("%02d", dtPickerFrom.getMonth())
                    +"-"+String.format("%02d",dtPickerFrom.getDayOfMonth());
             toDate = dtPickerTo.getYear()+"-"+String.format("%02d", dtPickerTo.getMonth())
                     +"-"+String.format("%02d", dtPickerTo.getDayOfMonth());


            JSONObject output = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "updateDelegate", createJsonObject());

            try {
                String title = " failed";
                if(output!=null && output.getBoolean("Result"))
                    title = " succeeded";

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder
                        .setMessage("Delegation" + title)
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dtPickerTo.updateDate(year,monthOfYear,dayOfMonth);
    }

    public String createJsonObject() {

        JSONObject jObject = new JSONObject();

        try {
            jObject.put("EmployeeId", e.get("employeeId").trim());
            jObject.put("StartDate", fromDate);
            jObject.put("EndDate", toDate);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON error>>", e.getMessage() + "");
        }
        Log.e("JSON>> ", jObject.toString());
        return jObject.toString();
    }
}