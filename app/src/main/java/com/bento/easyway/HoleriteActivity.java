package com.bento.easyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HoleriteActivity extends AppCompatActivity {
    //referencing the text view in the screen
    private TextView txt;

    // a user class
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set the screen layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olerite);
        //get the text view's reference in the olerite activity
        txt = findViewById(R.id.txt_holerite);
        //get variables from another script
        receiveClass();
    }

    private void receiveClass()
    {
        //getting extra information that remain
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") )
        {
            //getting user class info
            user = (User) extras.get("user");
            //showing the info
            txt.setText("Codigo :" + user.getNumero() + "\n" + user.getName() + "\n"  + user.getCargo());
        }
        else
        {
            //we transfer the user to the login screen
            Intent intent = new Intent(HoleriteActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
