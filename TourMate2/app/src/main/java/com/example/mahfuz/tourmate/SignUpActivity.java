package com.example.mahfuz.tourmate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.mahfuz.tourmate.db.TourMateDB;
import com.example.mahfuz.tourmate.model.User;
import com.example.mahfuz.tourmate.validation.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEt, emailEt, phoneEt, passwordEt;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private AwesomeValidation awesomeValidation;

    private ProgressBar signUpPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        phoneEt = findViewById(R.id.phoneEt);
        passwordEt = findViewById(R.id.passwordEt);
        signUpPb = findViewById(R.id.signUpProgressBar);

        auth = FirebaseAuth.getInstance();

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
//
//        rootRef = FirebaseDatabase.getInstance().getReference();
//
//        userRef = rootRef.child("users");

    }

    public void signUpAction(View view) {

        String email = emailEt.getText().toString ();
        String password = passwordEt.getText().toString();
        String name = nameEt.getText().toString();
        String phone = phoneEt.getText().toString();

        awesomeValidation.addValidation(SignUpActivity.this, R.id.nameEt, "[a-zA-Z\\s]+", R.string.name_err);
        awesomeValidation.addValidation(SignUpActivity.this, R.id.passwordEt, new SimpleCustomValidation() {
            @Override
            public boolean compare(String s) {
                if (!s.isEmpty() && s.length() >= 6) {
                    return true;
                }
                return false;
            }
        }, R.string.password_err);
        awesomeValidation.addValidation(SignUpActivity.this, R.id.phoneEt, RegexTemplate.NOT_EMPTY, R.string.phone_err);
        awesomeValidation.addValidation(SignUpActivity.this, R.id.emailEt, android.util.Patterns.EMAIL_ADDRESS, R.string.email_err);

        if (awesomeValidation.validate()) {

            final User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(name);
            user.setPhone(phone);


            Task<AuthResult> task = auth.createUserWithEmailAndPassword ( email, password);
            signUpPb.setVisibility(View.VISIBLE);
            task.addOnCompleteListener ( new OnCompleteListener<AuthResult>( ) {
                @Override
                public void onComplete ( @NonNull Task <AuthResult> task ) {
                    if (task.isSuccessful ()) {
                        firebaseUser = auth.getCurrentUser ();
                        TourMateDB tourMateDB = new TourMateDB(SignUpActivity.this, firebaseUser);
                        tourMateDB.addUser(user);
                        Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                        //userRef.child(firebaseUser.getUid()).setValue(user);
                        signUpPb.setVisibility(View.GONE);
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    }
                }
            } );

            task.addOnFailureListener ( new OnFailureListener( ) {
                @Override
                public void onFailure ( @NonNull Exception e ) {
                    signUpPb.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                    e.printStackTrace ();
                }
            } );
        } else {
            Toast.makeText(SignUpActivity.this, "Validation failed!!", Toast.LENGTH_SHORT).show();
        }

    }
}
