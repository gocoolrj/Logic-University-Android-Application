package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import iss.nus.com.logicuniversitystationary.Data.AdjustmentVoucherDetails;
import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class AdjustmentVoucher extends AppCompatActivity implements View.OnClickListener {

    EditText etItemCode, etQty, etRemarks, etDescription;
    Button btSubmitAdjustmentVoucher;
    ListView lvAdjustmentStock;
    ImageView ivAdd, ivSearch;
    List<AdjustmentVoucherDetails> values;
    SimpleAdapter adapter;
    LinearLayout llHeaderProgress2;
    Toolbar mActionBarToolbar;
    String output = "",description;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustment_voucher);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Adjustment Voucher");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etItemCode = (EditText) findViewById(R.id.etItemCode);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etQty = (EditText) findViewById(R.id.etQty);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        btSubmitAdjustmentVoucher = (Button) findViewById(R.id.btSubmitAdjustmentVoucher);
        lvAdjustmentStock = (ListView) findViewById(R.id.lvAdjustmentStock);
        ivAdd = (ImageView) findViewById(R.id.ivAdd);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        llHeaderProgress2 = (LinearLayout) findViewById(R.id.llHeaderProgress2);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);

        registerForContextMenu(lvAdjustmentStock);

        btSubmitAdjustmentVoucher.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        values = new ArrayList<>();

        getSharedPreferences("productSearch", MODE_PRIVATE).edit().clear().commit();

        if(getIntent().hasExtra("ProductCode")) {
            etItemCode.setText(getIntent().getExtras().getString("ProductCode"));
            etQty.setHint("Quantity: " + getIntent().getExtras().getString("Quantity"));
            etDescription.setText(getIntent().getExtras().getString("ProductName"));
            description = getIntent().getExtras().getString("ProductName");

            etItemCode.setEnabled(false);
            etDescription.setEnabled(false);

            etQty.requestFocus();
        }

        etItemCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>2){
                   // description = fillProductName();
                    etDescription.setText(fillProductName());

                    Log.i("desc>>",description+"");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        adapter = new SimpleAdapter(getApplicationContext(), values,
                R.layout.double_row_hidden,
                new String[]{"productName", "qty", "productCode"},
                new int[]{R.id.textView1, R.id.textView2, R.id.textView3});

        lvAdjustmentStock.setAdapter(adapter);

        ivAdd.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences("productSearch",MODE_PRIVATE);
        if(sharedpreferences.getString("productName",null) != null){
            etItemCode.setText(sharedpreferences.getString("productCode","").trim());
            etDescription.setText(sharedpreferences.getString("productName", "").trim());
        }

        getSharedPreferences("productSearch", MODE_PRIVATE).edit().clear().commit();

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.context_menu_modify_item, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        AdjustmentVoucherDetails avd = (AdjustmentVoucherDetails) lvAdjustmentStock.getItemAtPosition(acmi.position);

        if(item.getItemId() == R.id.update) {
            etItemCode.setText(avd.get("productCode"));
            etDescription.setText(avd.get("productName"));
            etQty.setText(avd.get("qty"));
            etQty.requestFocus();
        } else if(item.getItemId() == R.id.delete) {
            Iterator iterator = values.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(avd)) {
                    iterator.remove();
                    adapter.notifyDataSetChanged();
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == ivAdd.getId())
        {
            if (!etItemCode.getText().toString().equals("") && !etQty.getText().toString().equals("")) {
                if (!etDescription.getText().toString().contains("Not Valid")) {
                    AdjustmentVoucherDetails avd = new AdjustmentVoucherDetails(
                            etItemCode.getText().toString(),
                            etDescription.getText().toString(),
                            etQty.getText().toString(),
                            etRemarks.getText().toString());

                    Iterator iterator = values.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().equals(avd)) {
                            iterator.remove();
                        }
                    }
                    values.add(avd);
                    Collections.reverse(values);
                    adapter.notifyDataSetChanged();

                    etItemCode.getText().clear();
                    etQty.getText().clear();
                    etDescription.getText().clear();
                    etRemarks.getText().clear();
                    description = "";

                    etItemCode.setEnabled(true);
                    etDescription.setEnabled(true);
                    etQty.setHint("Quantity");
                    etItemCode.requestFocus();
                }
            }
        } else if (v.getId() == btSubmitAdjustmentVoucher.getId()) {
            JSONObject output = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "addAdjustment", createJsonArrayFromList());
            try {
                String vId = output.getString("Voucher").toString();
                String title = "Failed";
                String message = "Please try again..";

                if(!vId.contains("Failed") && !vId.contains("Quantity")) {
                    title = "Success";
                    message = "Voucher Id #" + vId;

                } else if (vId.contains("Quantity")) {
                    message = "Adjustment quantity exceeds inventory";
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                final String finalTitle = title;
                alertDialogBuilder
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                //MainActivity.this.finish();
                                if(finalTitle.contains("Success")){
                                    AdjustmentVoucher.this.onBackPressed();
                                    AdjustmentVoucher.this.finish();
                                }
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (v.getId() == ivSearch.getId()) {
            if (etDescription.getText().toString().trim() != "") {
                String searchText = etDescription.getText().toString();

                if(!searchText.startsWith(" ")) {
                    String[] firstName = etDescription.getText().toString().split(" ");
                    searchText = firstName[0];
                }

                Intent i = new Intent(this, ProductList.class);
                i.putExtra("pName",searchText);
                startActivity(i);
            }
        }
    }

    String fillProductName() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                llHeaderProgress2.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                return getProductDescription(etItemCode.getText().toString());
            }

            @Override
            protected void onPostExecute(String result) {
                output = result;
                if (result.length() > 0) {
                    etDescription.setText(result);
                }
                llHeaderProgress2.setVisibility(View.GONE);
            }
        }.execute();
        return output;
    }

    String getProductDescription(String productId) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String result = "Not Valid";
        try {
            JSONObject jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "prodCode/" + productId);
            result = jObject.getString("ProductName").toString().trim();

        } catch (Exception e) {
            Log.e(">>>>>>>", "" + e.getMessage());
        }
        return result;
    }


    public String createJsonArrayFromList() {

        JSONArray jsonArray = new JSONArray();

        try {
            for (AdjustmentVoucherDetails avd : values) {
                JSONObject jObject = new JSONObject();

                jObject.put("ClerkId", avd.get("clerkId"));
                jObject.put("ProductCode", avd.get("productCode"));
                jObject.put("Qty", Integer.parseInt(avd.get("qty")));
                jObject.put("Remark", avd.get("remark"));

                jsonArray.put(jObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON error>>", e.getMessage() + "");
        }
        Log.e("JSON>> ", jsonArray.toString());
        return jsonArray.toString();
    }
}