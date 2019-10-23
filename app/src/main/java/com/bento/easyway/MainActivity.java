package com.bento.easyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //UI variables
    private TextView hello;
    private Button btn_start;
    private Button btn_consult;
    private Button btn_worked;
    private Button btn_logout;
    private Chronometer working_chronometer;
    //some stuff that i will use
    private long lastPause;
    User user;
    boolean is_logged = false;
    boolean is_working = false;
    Date currentTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Verify if the user is logged
        verifyAuth();
        //UI references
        hello = findViewById(R.id.txt_main);
        btn_start = findViewById(R.id.btn_main_start);
        btn_consult = findViewById(R.id.btn_main_consult);
        btn_worked = findViewById(R.id.btn_main_worked);
        btn_logout = findViewById(R.id.btn_main_logout);
        working_chronometer = findViewById(R.id.chr_main);
        working_chronometer.setVisibility(View.INVISIBLE);
        //Buttons events
        btn_consult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HoleriteActivity.class);
                intent.putExtra("user", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_working == false) {
                    is_working = true;
                    working_chronometer.setBase(SystemClock.elapsedRealtime());
                    working_chronometer.start();
                    working_chronometer.setVisibility(View.VISIBLE);
                    currentTime = Calendar.getInstance().getTime();
                    btn_start.setText(R.string.main_stop);
                }else {
                    is_working = false;
                    btn_start.setText(R.string.main_start);
                    lastPause = SystemClock.elapsedRealtime();
                    working_chronometer.stop();
                    sendWorked();
                }
            }
        });

        btn_worked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WorkedActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        configureScreen();
    }

    private void configureScreen() {
        if (is_logged){hello.setText("Bem Vindo: " + user.getName());}
    }

    private void sendWorked(){
        int elapsedMillis = (int) (SystemClock.elapsedRealtime() - working_chronometer.getBase());
        elapsedMillis /= 1000;

        int hours = elapsedMillis/3600;
        int minusHours = hours * 3600;
        elapsedMillis -= minusHours;

        int minutes = elapsedMillis/60;
        int minusMinutes = minutes * 60;
        elapsedMillis -= minusMinutes;


        int seconds = elapsedMillis;
        String time = "";

        if(hours > 0){
            if(minutes > 9) {
                if (seconds > 9) {
                    time = String.valueOf(hours) + " : " + String.valueOf(minutes) + " : " + String.valueOf(seconds);
                } else {
                    time = String.valueOf(hours) + " : " + String.valueOf(minutes) + " : 0" + String.valueOf(seconds);
                }
            }else{
                    if(seconds>9) {
                        time = String.valueOf(hours) + " : 0" + String.valueOf(minutes) + " : " + String.valueOf(seconds);
                    }else{
                        time = String.valueOf(hours) + " : 0" + String.valueOf(minutes) + " : 0" + String.valueOf(seconds);
                    }
            }


        }else if(minutes > 0){
            if(minutes > 9){
                if(seconds > 9) {
                    time = String.valueOf(minutes) + " : " + String.valueOf(seconds);
                }else {
                    time = String.valueOf(minutes) + " : 0" + String.valueOf(seconds);
                }
            }
            else {
                if(seconds > 9) {
                    time = "0"+ String.valueOf(minutes) + " : " + String.valueOf(seconds);
                }else {
                    time = "0" + String.valueOf(minutes) + " : 0" + String.valueOf(seconds);
                }
            }
        }
        else {
            if(seconds > 9) {
                time = String.valueOf(seconds);
            }else {
                time = "0" + String.valueOf(seconds);
            }
        }

        Worked worked = new Worked(currentTime.toString(),time,user.getNumero());
        calculate();
        FirebaseFirestore.getInstance().collection("users").document(user.getDocReference()).collection("Worked").add(worked).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                hello.setText("Enviado para o banco de dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hello.setText("Error : " + e.getMessage());
            }
        });

    }

    private void calculate() {
    }

    private void verifyAuth() {
        //getting values from the login's activity
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
            is_logged = true;
        }else{
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
