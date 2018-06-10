package com.example.mahfuz.tourmate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mahfuz.tourmate.validation.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private ProgressBar loginPb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.emailEt);
        etPassword = findViewById(R.id.passwordEt);
        loginPb = findViewById(R.id.loginProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void SignUp(View view) {
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }

    public void loginAction(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (Validator.emailValidator(email) && Validator.passwordValidator(password)) {
            loginPb.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword ( email, password )
                    .addOnCompleteListener ( new OnCompleteListener<AuthResult>( ) {
                        @Override
                        public void onComplete ( @NonNull Task<AuthResult> task ) {


                            if (task.isSuccessful ()) {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                loginPb.setVisibility(View.GONE);
                                firebaseUser = firebaseAuth.getCurrentUser ();
                                Toast.makeText(LoginActivity.this, "Logged in as: "+firebaseUser.getEmail (), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    } )
                    .addOnFailureListener ( new OnFailureListener( ) {
                        @Override
                        public void onFailure ( @NonNull Exception e ) {
                            loginPb.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } );
        } else {
            Toast.makeText(LoginActivity.this, "Validation failed!!", Toast.LENGTH_SHORT).show();

        }
    }
}
