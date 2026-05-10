package com.kipucode.view;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kipucode.R;
import com.kipucode.service.Utils;

public class Login extends AppCompatActivity {

    TextView createAccount, forgotPassword;
    Button btnLogin;
    EditText email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createAccount = findViewById(R.id.tv_createAccount);
        forgotPassword = findViewById(R.id.tv_forgotPassword);
        btnLogin = findViewById(R.id.btn_logIn);
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password);


        createAccount.setHighlightColor(Color.TRANSPARENT);
        forgotPassword.setHighlightColor(Color.TRANSPARENT);

        spannableStrings();
        setupFirebaseAuth();
    }



    public void spannableStrings(){
        String txt_createAccount = createAccount.getText().toString();
        String txt_forgotPassword = forgotPassword.getText().toString();

        SpannableString spannableString1 = Utils.getSpannableString(this, txt_createAccount, Register.class, "?");
        SpannableString spannableString2 = Utils.getSpannableString(this, txt_forgotPassword, Register.class, "");

        createAccount.setText(spannableString1);
        createAccount.setMovementMethod(LinkMovementMethod.getInstance());

        forgotPassword.setText(spannableString2);
        forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setupFirebaseAuth() {
        // 1. Inicializamos mAuth correctamente
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            // 2. Obtenemos el texto DENTRO del onClick para capturar lo que el usuario escribió
            String _email = email.getText().toString().trim();
            String _pass = pass.getText().toString().trim();

            if(!_email.isEmpty() && !_pass.isEmpty()){
                // 3. Pasamos el contexto de la Actividad (Register.this) en lugar de 'this'
                mAuth.signInWithEmailAndPassword(_email, _pass)
                        .addOnCompleteListener(Login.this, task -> {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                if(!user.isEmailVerified()){
                                    Toast.makeText(Login.this, "Verified your email", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    Toast.makeText(Login.this, "Sign In successful", Toast.LENGTH_SHORT).show();
                                    email.setText("");
                                    pass.setText("");
                                } else {
                                    var ex = task.getException();
                                    String message = ex != null ? ex.getMessage() : null;
                                    Log.w("ERROR", "signIn:failure", task.getException());// Si falla, mostramos el mensaje
                                    Toast.makeText(Login.this, message , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                // Opcional: Avisar al usuario que faltan datos
                Toast.makeText(Login.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        }); // 4. Cerramos el setOnClickListener correctamente
    }
}