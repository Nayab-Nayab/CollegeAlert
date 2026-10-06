package com.example.collegealert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    android.graphics.Color.parseColor("#1A73E8"));
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText()
                        .toString().trim();
                String password = etPassword.getText()
                        .toString();

                // Validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Enter email and password!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Login
                mAuth.signInWithEmailAndPassword(
                                email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Login successful
                                String userId = mAuth
                                        .getCurrentUser()
                                        .getUid();
                                checkUserRole(userId);
                            } else {

                                String error = task
                                        .getException() != null
                                        ? task.getException()
                                        .getMessage()
                                        : "Unknown error";
                                Toast.makeText(
                                                MainActivity.this,
                                                "Error: " + error,
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
            }
        });
    }


    private void checkUserRole(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document
                                .getString("role");

                        Intent intent = new Intent(
                                MainActivity.this,
                                EventListActivity.class);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();

                    } else {

                        Intent intent = new Intent(
                                MainActivity.this,
                                EventListActivity.class);
                        intent.putExtra("role", "user");
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}