package iss.nus.com.logicuniversitystationary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.InetAddress;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class Homepage extends ActionBarActivity implements View.OnClickListener {

    TableLayout tlAdjustment, tlLow, tlOrders, tlCollection;
    TextView tvNotification;
    LinearLayout linlaHeaderProgress;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        tlAdjustment = (TableLayout) findViewById(R.id.tlAdjustment);
        tlLow = (TableLayout) findViewById(R.id.tlLow);
        tlOrders = (TableLayout) findViewById(R.id.tlOrders);
        tlCollection = (TableLayout) findViewById(R.id.tlCollection);
        tvNotification = (TextView) findViewById(R.id.tvNotification);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        tlAdjustment.setOnClickListener(this);
        tlLow.setOnClickListener(this);
        tlOrders.setOnClickListener(this);
        tlCollection.setOnClickListener(this);

       sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);
        if(sp.getString("UserId",null)!=null) {
            Globals.EmpId = sp.getString("UserId",null);
        } else {
            startActivity(new Intent(this,Login.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                return getLowStockCount();
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result > 0) {
                    tvNotification.setAlpha(1.0f);
                    tvNotification.setText(result.toString());
                    tvNotification.setText("" + result);
                    tvNotification.setPadding(11, 5, 11, 5);
                    if (result < 10)
                        tvNotification.setPadding(18, 5, 18, 5);
                }
                linlaHeaderProgress.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onResume() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                return getLowStockCount();
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result > 0) {
                    tvNotification.setAlpha(1.0f);
                    tvNotification.setText(result.toString());
                    tvNotification.setText("" + result);
                    tvNotification.setPadding(11, 5, 11, 5);
                    if (result < 10)
                        tvNotification.setPadding(18, 5, 18, 5);
                }
                linlaHeaderProgress.setVisibility(View.GONE);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return isInternetAvailable();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                linlaHeaderProgress.setVisibility(View.GONE);
                if (result == false) {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                } else {
                    navigate(true);
                }
            }

            private void navigate(Boolean result) {
                if (result) {
                    if (v.getId() == tlAdjustment.getId()) {
                        startActivity(new Intent(getBaseContext(), AdjustmentVoucher.class));
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    } else if (v.getId() == tlLow.getId()) {
                        startActivity(new Intent(getBaseContext(), LowStock.class));
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    } else if (v.getId() == tlOrders.getId()) {
                        CharSequence colors[] = new CharSequence[]{"Retrieval List", "Orders by department"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
                        builder.setTitle("Select type of order");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {
                                    startActivity(new Intent(getBaseContext(), OrderList.class));
                                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                } else {
                                    startActivity(new Intent(getBaseContext(), AllOrders.class));
                                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                }
                            }
                        });
                        builder.show();
                    } else {
                        startActivity(new Intent(getBaseContext(), Collectionpoint.class));
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }
                }
            }
        }.execute();
    }

    int getLowStockCount() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        int result = 0;
        try {
            JSONObject jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "stockCount");
            result = Integer.parseInt(jObject.getString("StockCount").toString());

        } catch (Exception e) {
            Log.e(">>>>>>>", "" + e.getMessage());

        }
        return result;
    }

    public Boolean isInternetAvailable() {
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
            if (isNetworksAvailable()) {
                InetAddress ipAddr = InetAddress.getByName("google.com");

                if (ipAddr.equals("")) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isNetworksAvailable() {
        ConnectivityManager mConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnMgr != null) {
            NetworkInfo[] mNetInfo = mConnMgr.getAllNetworkInfo();
            if (mNetInfo != null) {
                for (int i = 0; i < mNetInfo.length; i++) {
                    if (mNetInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}