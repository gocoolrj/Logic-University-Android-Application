package iss.nus.com.logicuniversitystationary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import iss.nus.com.logicuniversitystationary.Data.Globals;


public class DeptHomePage extends Activity implements View.OnClickListener {
    TableLayout tlDelegate, tlApprove;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_home_page);
        tlDelegate = (TableLayout) findViewById(R.id.tlDelegate);
        tlApprove = (TableLayout) findViewById(R.id.tlApprove);

        tlDelegate.setOnClickListener(this);
        tlApprove.setOnClickListener(this);

        sp = getSharedPreferences("LoginPreference",MODE_PRIVATE);
        if(sp.getString("UserId",null)!=null) {
            Globals.EmpId = sp.getString("UserId",null);
        } else {
            startActivity(new Intent(this,Login.class));
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == tlDelegate.getId()) {
            startActivity(new Intent(this, DeptDelegate.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        else {
            startActivity(new Intent(this, DeptApprove_Reject.class));
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }
}
