package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity
{

    private Button mRegister;
    private ProgressBar spinner;
    private EditText mEmail, mName, mPassword, mConfirmPassword, mBudget;

    //private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String TAG = "RegisterActivity";

//    @Override
//    public int checkCallingOrSelfPermission(String permission)
//    {
//
//        return super.checkCallingOrSelfPermission(permission);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(RegisterActivity.this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinner = findViewById(R.id.progressBarRegister);
        spinner.setVisibility(View.GONE);

        //TextView existing = findViewById(R.id.existing);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                spinner.setVisibility(View.VISIBLE);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null && user.isEmailVerified())
                {
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    spinner.setVisibility(View.GONE);
                    return;
                }
                spinner.setVisibility(View.GONE);
            }
        };

        /*
        existing.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });
         */

        mRegister = findViewById(R.id.btn_register);
        mEmail = findViewById(R.id.input_email);
        mName = findViewById(R.id.input_username);
        mPassword = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.input_passwordConfirmation);
        //final CheckBox checkBox = findViewById(R.id.checkbox);
        //TextView textView = findViewById(R.id.TextView2);

        //checkbox.setText("");
        //textview.setText(Html.fromHtml("I have read and agree to the " + "<a href = '>Terms & Conditions</a>")); give a link here later
        //textview.setClickable(true);
        //textview.setMovementMethod(LinkMovementMethod.getInstance());

        mRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                spinner.setVisibility(View.VISIBLE);

                final String email = mEmail.getText().toString();
                final String username = mName.getText().toString();
                final String password = mPassword.getText().toString();
                final String confirmPassword = mConfirmPassword.getText().toString();
                //final Boolean tnc = checkbox.isChecked();

                if(checkInputs(email, username, password, confirmPassword))
                {

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(RegisterActivity.this, "User Registered Successfully. Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                            String userId = mAuth.getCurrentUser().getUid();
                                            DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                            Map userInfo = new HashMap<>();
                                            userInfo.put("name", username);
                                            userInfo.put("profileImageUrl", "default");
                                            currentUserDB.updateChildren(userInfo);

                                            mEmail.setText("");
                                            mName.setText("");
                                            mPassword.setText("");
                                            mConfirmPassword.setText("");

                                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                spinner.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkInputs(String email, String username, String password, String confirmPassword)
    {
        if(email.isEmpty() || username.isEmpty()  || password.isEmpty()  || confirmPassword.isEmpty())
        {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!email.matches(emailPattern))
        {
            Toast.makeText(this, "Invalid email address. Try again", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(confirmPassword))
        {
            Toast.makeText(this, "Passwords do not match. Try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

//        if(!tnc){
//            Toast.makeText(this, "Please accept the terms and conditions.", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return true;

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    public void ReturnToLogin(View v)
    {
        finish();
    }

}