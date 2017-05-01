package com.example.romanpr.attendroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "RegisterActivity";

    private DatabaseReference database;
    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setting up authentication
        mAuth = FirebaseAuth.getInstance();
        // Notifies the app whenever the user signs in or signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());
                    database = FirebaseDatabase.getInstance().getReference(user.getUid());
                    database.setValue(newUser);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("USER_ID", user.getUid());
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    // Attach the listener to your FirebaseAuth instance in the onStart() method and remove it on onStop()
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
    protected void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmail:failed", task.getException());
                            Toast.makeText(RegisterActivity.this, R.string.reg_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // On-click listener of the register button
    public void onRegisterButtonClicked(View view) {
        EditText etFirstName = (EditText) findViewById(R.id.first_name);
        EditText etLastName = (EditText) findViewById(R.id.last_name);
        EditText etUsername = (EditText) findViewById(R.id.username);
        EditText etPassword = (EditText) findViewById(R.id.password);
        EditText etFaculty = (EditText) findViewById(R.id.faculty);
        EditText etDepartment = (EditText) findViewById(R.id.department);
        RadioGroup rgRole = (RadioGroup) findViewById(R.id.roleRadioGroup);

        switch (rgRole.getCheckedRadioButtonId()) {
            case R.id.radio_student:
                EditText etStudentId = (EditText) findViewById(R.id.student_id);
                long studentId = Long.parseLong(etStudentId.getText().toString());
                newUser = new Student(
                        etFirstName.getText().toString(),
                        etLastName.getText().toString(),
                        etUsername.getText().toString(),
                        etFaculty.getText().toString(),
                        etDepartment.getText().toString(),
                        studentId);
                break;
            case R.id.radio_professor:
                newUser = new Professor(
                        etFirstName.getText().toString(),
                        etLastName.getText().toString(),
                        etUsername.getText().toString(),
                        etFaculty.getText().toString(),
                        etDepartment.getText().toString()
                );
        }

        // User input validation
        if(etUsername.getText().toString().trim() != ""
                && etPassword.getText().toString().trim() != "") {
            createAccount(etUsername.getText().toString(), etPassword.getText().toString());
        } else{
            Toast.makeText(this, R.string.invalid_fields, Toast.LENGTH_SHORT);
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
