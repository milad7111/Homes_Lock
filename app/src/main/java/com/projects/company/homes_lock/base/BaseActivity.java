package com.projects.company.homes_lock.base;

/*
  This is Base Activity for All Activities
 */

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import static com.projects.company.homes_lock.utils.helper.ProgressDialogHelper.closeProgressDialog;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
    }

    public void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
}