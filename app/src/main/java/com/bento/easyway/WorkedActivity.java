package com.bento.easyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkedActivity extends AppCompatActivity {
    private TextView txt;

    User user;
    int currentYear;
    int currentDay;
    int currentMonth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worked);
        receiveValues();

        txt = findViewById(R.id.txt_worked);
        Date date = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);
        configureTextview();

    }

    private void configureTextview() {
        final int[] hours_total = new int[1];
        final int[] minutes_total = new int[1];
        final int[] seconds_total = new int[1];
        final double[] salary = new double[1];

        CollectionReference docRef = FirebaseFirestore.getInstance().collection("users").document(user.getDocReference()).collection("Worked").
                document(String.valueOf(currentYear)).collection(String.valueOf(currentMonth));
        docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs) {
                    final Worked worked;
                    worked = doc.toObject(Worked.class);
                    salary[0] = salary[0] + Double.parseDouble(worked.getPay());

                    String[] parts = worked.getWorked_time().split(":",3);
                    String part1 = parts[0];
                    hours_total[0] = hours_total[0] + Integer.parseInt(part1.trim());

                    String part2 = parts[1];
                    minutes_total[0] = minutes_total[0] + Integer.parseInt(part2.trim());

                    String part3 = parts[2];
                    seconds_total[0] = seconds_total[0] + Integer.parseInt(part3.trim());

                    txt.setText("Esse mês você trabalhou "+hours_total[0]+"horas,"+user.getName()+" e recebera R$"+ salary[0] +" por elas");
                }
            }
        });

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
