package com.example.romanpr.attendroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
Dummy user:
    dum@thk.edu.tr
    qwerty123
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    // Listens for any changes in the user's login status
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        // Setting up authentication
        mAuth = FirebaseAuth.getInstance();
        // Notifies the app whenever the user signs in or signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(LoginActivity.this, "You've logged in successfully!\nYour data is loading...", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER_ID", user.getUid());
                    startActivity(intent);*/

                     // Attendata.setUserId(user.getUid());
                     Attendata.get(LoginActivity.this);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        //signOut();
        if (!GPSLocation.locationAccessPermissionGranted(this)) {
            GPSLocation.requestLocationPermissions(this);
        }
    }

    // Attach the listener to your FirebaseAuth instance in the onStart() method and remove it on onStop()
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart:addAuthStateListener");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Signed out on resume");
        signOut();
        Attendata.get(this).clearAttendata();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            Log.d(TAG, "onStop:removeAuthStateListener");
        }
    }

    // Attempts to log in the user with their account's email and password passed as arguments
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete: " + task.isSuccessful());
                        /*
                        If sign-in fails, display a message to the user. If sign-in succeeds
                        the auth state listener will be notified and logic to handle the
                        signed in user can be handled in the listener.
                         */
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Logs out the currently logged in user
    public void signOut() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
            Log.d(TAG, "Signed out: " + currentUser.getUid());
        } else {
            Log.d(TAG, "No one to sign out");
        }
    }

    // On-click listener of the login button
    public void onLoginButtonClicked(View view) {
        EditText etUsername = (EditText) findViewById(R.id.username);
        EditText etPassword = (EditText) findViewById(R.id.password);
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        // User input validation
        if(username.length() > 0 && password.length() > 0){
            signIn(username, password);
        }else{
            Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
        }
    }

    // On-click listener of the register link
    public void onSignupLinkClicked(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
