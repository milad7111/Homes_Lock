package com.projects.company.homes_lock.ui.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseActivity;

import java.util.Objects;

public class SupportActivity extends BaseActivity implements View.OnClickListener {

    //region Declare Views
    private Button btnSubmitActivitySupport;

    private TextInputEditText tietTicketTitleActivitySupport;
    private TextInputEditText tietTicketTextActivitySupport;
    //endregion Declare Views

    //region Main Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //region Initialize Views
        btnSubmitActivitySupport = findViewById(R.id.btn_submit_activity_support);

        tietTicketTitleActivitySupport = findViewById(R.id.tiet_ticket_title_activity_support);
        tietTicketTextActivitySupport = findViewById(R.id.tiet_ticket_text_activity_support);
        //endregion Initialize Views

        //region Setup Views
        btnSubmitActivitySupport.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_activity_support:
                sendFeedBack();
                break;
        }
    }

    private void sendFeedBack() {
        if (validateInputs(tietTicketTitleActivitySupport.getText(), tietTicketTextActivitySupport.getText()))
            startEmailIntent(tietTicketTitleActivitySupport.getText().toString(), tietTicketTextActivitySupport.getText().toString());
    }

    private void startEmailIntent(String title, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@neurnet.se"});
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    private boolean validateInputs(Editable title, Editable text) {
        if (title == null || title.toString().isEmpty()) {
            showToast("Type title, then try again.");
            return false;
        }
        if (text == null || text.toString().isEmpty()) {
            showToast("Fill description, then try again.");
            return false;
        }

        return true;
    }
    //endregion Main Callbacks
}
