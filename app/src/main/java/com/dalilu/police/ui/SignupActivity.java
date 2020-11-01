package com.dalilu.police.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dalilu.police.R;
import com.dalilu.police.data.Police;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Creating user";
    private TextInputLayout emailTV, passwordTV, fullNameTV, phoneTV;
    private MaterialButton signupBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void initViews() {
        emailTV = findViewById(R.id.emailTextInput);
        passwordTV = findViewById(R.id.passwordTextInput);
        fullNameTV = findViewById(R.id.fullNameTextInput);
        phoneTV = findViewById(R.id.phoneTextInput);
        signupBtn = findViewById(R.id.register_button);
    }

    public void openLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void registerNewUser() {
//        progressBar.setVisibility(View.VISIBLE);

        String email, password, name, phone;
        email = emailTV.getEditText().getText().toString();
        password = passwordTV.getEditText().getText().toString();
        name = fullNameTV.getEditText().getText().toString();
        phone = phoneTV.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_LONG).show();
            emailTV.setError("Please enter email!");
            emailTV.setErrorEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            passwordTV.setError("Please enter password!");
            passwordTV.setErrorEnabled(true);
            return;
        }

        createUserAccount(email, password, name, phone );
    }

    private void createUserAccount(final String email, String password, final String name, final String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            createPolice(name, phone, email);
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
            startActivity(new Intent(this, MainActivity.class));
    }

    private void createPolice(String fullName, String telephone, String email) {

        FirebaseFirestore
                .getInstance()
                .collection("Police")
                .add(new Police("Police", "101xpuo", fullName, telephone, email, "kdfdfafda.jpg"))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Police data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
