package com.example.digital.appfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseActivity extends AppCompatActivity {
    private static final String TAG = "string";
    private FirebaseAuth mAuth;

    Button botaologin;
    Button botaoregistro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

// ...
        mAuth = FirebaseAuth.getInstance();
        botaoregistro = findViewById(R.id.id_registro);
        botaoregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registroUsuario();
            }
        });

        botaologin = findViewById(R.id.id_login);
        botaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                autenciticarUsuario();

            }
        });


    }

  //  @Override
    //public void onStart() {
      ///  super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
       // FirebaseUser currentUser = mAuth.getCurrentUser();
       // if (currentUser != null) {
        //    gotToHome();
       // }*//


    private void gotToHome() {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void registroUsuario(){
        Intent intent =new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void autenciticarUsuario() {

        EditText emailEditText = findViewById(R.id.id_email);
        EditText passwordEditText = findViewById(R.id.id_senha);

        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            gotToHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }
}

