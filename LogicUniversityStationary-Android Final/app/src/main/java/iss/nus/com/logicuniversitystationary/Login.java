package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

import iss.nus.com.logicuniversitystationary.Data.Globals;
import iss.nus.com.logicuniversitystationary.JSON.JSONParser;


public class Login extends Activity implements View.OnClickListener {
    EditText etUserName, etPassword;
    Button btLogin;
    LinearLayout loginLayout, llLogo, lvHeaderProgress;
    Boolean doubleBackToExitPressedOnce;
    TextView tvTitle;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogin = (Button) findViewById(R.id.btLogin);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        llLogo = (LinearLayout) findViewById(R.id.llLogo);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        lvHeaderProgress = (LinearLayout) findViewById(R.id.lvHeaderProgress);

        try {
            SharedPreferences sp = getSharedPreferences("LoginPreference", MODE_PRIVATE);
            if (sp.getString("UserId", null) != null) {
                Globals.EmpId = sp.getString("UserId", null);

                switch (sp.getString("role", null).trim().toLowerCase()) {
                    case "store clerk":
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        break;
                    case "department rep":
                        startActivity(new Intent(getApplicationContext(), DepartmentRep.class));
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        break;
                    case "department head":
                        startActivity(new Intent(getApplicationContext(), DeptHomePage.class));
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        break;
                }

            }
        } catch (Exception e) {}

        doubleBackToExitPressedOnce = false;

        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        btLogin.setOnClickListener(this);

        // Prepare the View for the animation
        loginLayout.setVisibility(View.VISIBLE);
        loginLayout.setAlpha(0.0f);
        tvTitle.setAlpha(0.0f);

        Animation translatebu = AnimationUtils.loadAnimation(this, R.anim.login_anim);

        llLogo.startAnimation(translatebu);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    tvTitle.animate()
                            .alpha(1f)
                            .setDuration(1000);
                    loginLayout.animate()
                            .alpha(1f)
                            .setDuration(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        doubleBackToExitPressedOnce = false;
        //getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit",
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public void onClick(View v) {


        JSONObject jObject = JSONParser.getJSONObjectFromUrl(Globals.baseurl + "userlogin",createJsonObjectFromList());
        Log.i("Response>>",jObject.toString()+"");
        try {
            role = jObject.getString("Role").toString().trim();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                lvHeaderProgress.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return isInternetAvailable();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                lvHeaderProgress.setVisibility(View.GONE);
                if (result == false) {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    switch (role.toLowerCase()) {
                        case "store clerk":
                            Globals.EmpId = etUserName.getText().toString().trim();
                            editor.putString("UserId",Globals.EmpId);
                            editor.putString("Role",role);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), Homepage.class));
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            break;
                        case "department rep":
                            Globals.EmpId = etUserName.getText().toString().trim();
                            editor.putString("UserId",Globals.EmpId);
                            editor.putString("Role",role);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), DepartmentRep.class));
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            break;
                        case "department head":
                            Globals.EmpId = etUserName.getText().toString().trim();
                            editor.putString("UserId",Globals.EmpId);
                            editor.putString("Role",role);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), DeptHomePage.class));
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                            break;
                        default:
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);

                            alertDialogBuilder
                                    .setTitle("Invalid username/password")
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
        }.execute();
    }

    public boolean isInternetAvailable() {
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
        return false;
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

    public String createJsonObjectFromList() {

        JSONObject jObject = new JSONObject();

        try {
            jObject.put("Username", etUserName.getText().toString().trim());
            jObject.put("Password", etPassword.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Json>> ", jObject.toString() + "");
        return jObject.toString();
    }

}
