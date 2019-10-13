package com.bento.easyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText num;
    private EditText password;
    private Button btn;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ui
        btn = findViewById(R.id.btn_login);
        num = findViewById(R.id.edtxt_login_number);
        password = findViewById(R.id.extxt_login_password);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Vnum = num.getText().toString();
                String Vpassword = password.getText().toString();
                if(Vnum.equals("") || Vpassword.equals("")){
                    Toast.makeText(LoginActivity.this, "O numero e senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
                }else {
                    Login();
                }
            }
        });

    }


    private void Login() {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("numero",num.getText().toString()).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Usuario não encontrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs) {
                    user = doc.toObject(User.class);
                }

                if(user != null && !user.getName().equals("")){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Usuario não encontrado", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}
