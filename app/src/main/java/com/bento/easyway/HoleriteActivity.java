package com.bento.easyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HoleriteActivity extends AppCompatActivity {
    private TextView txt;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olerite);
        txt = findViewById(R.id.txt_holerite);

        receiveClass();

    }

    private void receiveClass() {
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
            txt.setText("Codigo :" + user.getNumero() + "\n" + user.getName() + "\n"  + user.getCargo());
        }else{
            Intent intent = new Intent(HoleriteActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
