package com.oweshie.womenprotector.womenprotector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oweshie.womenprotector.womenprotector.common.CommonContant;
import com.oweshie.womenprotector.womenprotector.common.CommonTask;

public class Settings extends AppCompatActivity implements View.OnClickListener {
    private EditText userName;
    private EditText emergencyNumber;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    private void initViews() {
        userName = findViewById(R.id.userName);
        emergencyNumber = findViewById(R.id.emergencyNumber);
        userName.setText(CommonTask.getPreference(this, CommonContant.USER_NAME));
        emergencyNumber.setText(CommonTask.getPreference(this, CommonContant.EMERGENCY_NUMBER));
        saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.buttonSave){
            // TODO: 7/28/18 save
            CommonTask.savePreference(this, CommonContant.USER_NAME,userName.getText().toString());
            CommonTask.savePreference(this, CommonContant.EMERGENCY_NUMBER,emergencyNumber.getText().toString());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
