package com.bento.easyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    File localFile;
    //date
    int currentYear;
    int currentDay;
    int currentMonth;
    double salary;
    double old_salary;
    double total;
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
                downloadPdf();
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
                    Date date = Calendar.getInstance().getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    currentDay = cal.get(Calendar.DAY_OF_MONTH);
                    currentMonth = cal.get(Calendar.MONTH);
                    currentYear = cal.get(Calendar.YEAR);
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

    private void openPdf(File file){
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Uri path = Uri.fromFile(file);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setDataAndType(path, "application/pdf");
        try {
            startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException e) {

        }
    }

    private void downloadPdf() {
// Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        storageRef.child("pdf.pdf").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                startActivity(browserIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
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

        calculate(hours,minutes);

        Worked worked = new Worked(String.valueOf(currentDay),String.valueOf(currentMonth),String.valueOf(currentYear),time,user.getNumero(),String.valueOf(total));
        verifyDay(worked);


    }

    private void verifyDay(final Worked worked){
        final String[] new_time = new String[1];

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(user.getDocReference()).collection("Worked").
                document(String.valueOf(currentYear)).collection(String.valueOf(currentMonth)).document(worked.worked_day);


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Worked worked1 = documentSnapshot.toObject(Worked.class);

                        if (worked1 == null){
                            FirebaseFirestore.getInstance().collection("users").document(user.getDocReference()).collection("Worked").
                                    document(String.valueOf(currentYear)).collection(String.valueOf(currentMonth)).document(worked.worked_day)
                                    .set(worked).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                          @Override
                                                                          public void onSuccess(Void aVoid) {
                                                                              hello.setText("Enviado para o banco de dados");
                                                                          }
                                                                      }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hello.setText("Error : " + e.getMessage());
                                }
                            });
                        }else{
                            String time = worked1.getWorked_time();
                            new_time[0] = addTime(time, worked.getWorked_time());
                            Log.d("Teste", "No such document");

                            worked.setWorked_time(new_time[0]);
                            FirebaseFirestore.getInstance().collection("users").document(user.getDocReference()).collection("Worked").
                                    document(String.valueOf(currentYear)).collection(String.valueOf(currentMonth)).document(worked.worked_day)
                                    .set(worked).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                          @Override
                                                                          public void onSuccess(Void aVoid) {
                                                                              hello.setText("Enviado para o banco de dados");
                                                                          }
                                                                      }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hello.setText("Error : " + e.getMessage());
                                }
                            });
                        }

            }
        });

    }

    private String addTime(String time_p,String time_n) {
        String[] parts = time_p.split(":",3);
        String part1 = parts[0];
        int hours_to_add = Integer.parseInt(part1.trim());

        String part2 = parts[1];
        int minutes_to_add = Integer.parseInt(part2.trim());

        String part3 = parts[2];
        int seconds_to_add = Integer.parseInt(part3.trim());

        String[] parts_n = time_n.split(":",3);
        String part1_n = parts_n[0];
        int hours_to_add_n = Integer.parseInt(part1_n.trim());

        String part2_n = parts_n[1];
        int minutes_to_add_n = Integer.parseInt(part2_n.trim());

        String part3_n = parts_n[2];
        int seconds_to_add_n = Integer.parseInt(part3_n.trim());

        int hours = hours_to_add + hours_to_add_n;
        int minutes = minutes_to_add + minutes_to_add_n;
        if(minutes >=60){
            hours = hours + (minutes/60);
        }

        int seconds = seconds_to_add + seconds_to_add_n;

        if(seconds>=60){
            minutes = minutes + (seconds/60);
        }

        String time= "";

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
        return time;
    }

    private void calculate(int hours, int minutes) {
        salary = 10.0;
        total = (hours * salary) + (minutes * (salary/60));
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
