package com.bento.easyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class WorkedActivity extends AppCompatActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worked);
        receiveValues();
    }

    private void receiveValues() {
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
        }else{
            Intent intent = new Intent(WorkedActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
