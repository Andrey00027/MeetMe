package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity
{

    private static final String TAG = "LoginActivity";

    private ProgressBar spinner;
    private Button mLogin;
    private EditText mEmail, mPassword;
    private TextView mforgotPassword;
    private boolean btnLoginClicked;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(LoginActivity.this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLoginClicked = false;
        spinner = findViewById(R.id.progressBarLogin);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mLogin = findViewById(R.id.btn_login);
        mEmail = findViewById(R.id.edtxtLoginEmail);
        mPassword = findViewById(R.id.edtxtLoginPassword);
        mforgotPassword = findViewById(R.id.forgot_Password);

        mLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnLoginClicked = true;
                spinner.setVisibility(View.VISIBLE);
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if(isStringNull(email) || isStringNull(password))
                {
                    Toast.makeText(LoginActivity.this, "All the fields must be completed.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(!task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        if(mAuth.getCurrentUser().isEmailVerified())
                                        {
                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, "Authentication failed, please verify your email and password and try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
                spinner.setVisibility(View.GONE);
            }
        });

        mforgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                spinner.setVisibility(View.VISIBLE);
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

                // Inflate custom layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
                alert.setView(dialogView);

                // Initialize EditText and TextView from custom layout
                EditText input = dialogView.findViewById(R.id.edtxtLoginEmail);

                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String entered_email = input.getText().toString();

                        mAuth.sendPasswordResetEmail(entered_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email sent. Please check your email.", Toast.LENGTH_SHORT).show();
                                    spinner.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error sending email. Please try again.", Toast.LENGTH_SHORT).show();
                                    spinner.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        spinner.setVisibility(View.GONE);
                    }
                });

                AlertDialog dialog = alert.create();
                // Apply rounded corners to the dialog window
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
                }
                dialog.show();

                // Customize button colors (optional)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.primaryButtonColor));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.secondaryButtonColor));
            }
        });

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null && user.isEmailVerified() && !btnLoginClicked)
                {
                    spinner.setVisibility(View.VISIBLE);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    spinner.setVisibility(View.GONE);
                }
            }
        };
    }

    private boolean isStringNull(String email)
    {
        return email.equals("");
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

    public void goToRegister(View v)
    {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}