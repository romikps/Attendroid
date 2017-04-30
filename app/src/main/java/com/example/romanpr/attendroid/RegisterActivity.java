package com.example.romanpr.attendroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();


        // Notifies the app whenever the user signs in or signs out.
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    DataMaster.userDb = new Database(user.getUid());
                    changeActivity();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart:addAuthStateListener");
    }

    @Override
    public void onStop() {

        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            Log.d(TAG, "onStop:removeAuthStateListener");
        }
    }

    /*
    If the new account was created, the user is also signed in,
    and the AuthStateListener runs the onAuthStateChanged callback.
    In the callback, you can use the getCurrentUser method
    to get the user's account data.
     */
    private void createMasterAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createMasterAccount:onComplete: " + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createMasterAccount:failed", task.getException());
                            Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changeActivity() {

        Intent myIntent = new Intent(this, ShowServices.class);
        startActivity(myIntent);
    }

    // On-click listener of the register button
    public void buttonClickRegister(View view) {

        EditText etUsername = (EditText) findViewById(R.id.editTextRegisterUsername);
        EditText etPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        EditText etConfirmPassword = (EditText) findViewById(R.id.editTextConfimePassword);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // User input validation
        if(username.length() > 0 && password.length() > 0 && password.equals(confirmPassword)) {
            // Required to encrypt new passwords
            DataMaster.masterHash = PMCrypto.whirlpoolDigest(password.getBytes());
            createMasterAccount(username, password);
        } else{
            Toast.makeText(this, "Some fields are invalid", Toast.LENGTH_SHORT);
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        EditText etStudentId = (EditText) findViewById(R.id.student_id);
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_student:
                if (checked)
                    etStudentId.setVisibility(View.VISIBLE);
                    break;
            case R.id.radio_professor:
                if (checked)
                    etStudentId.setVisibility(View.GONE);
                    break;
        }
    }
}
