package com.kipucode.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kipucode.R;
import com.kipucode.service.Utils;

public class Register extends AppCompatActivity {

    TextView login;
    EditText email, pass, confirmPass;
    Button btnSingUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login = findViewById(R.id.tv_login);
        btnSingUp = findViewById(R.id.btn_signUp);
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password);
        confirmPass = findViewById(R.id.et_confirm_password);

        login.setHighlightColor(Color.TRANSPARENT);

        String txt_login = login.getText().toString();

        SpannableString spannableString = Utils.getSpannableString(this, txt_login, Login.class, "?");

        login.setText(spannableString);
        login.setMovementMethod(LinkMovementMethod.getInstance());

        setup();
    }

    public void setup() {
        // 1. Inicializamos mAuth correctamente
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        btnSingUp.setOnClickListener(v -> {
            // 2. Obtenemos el texto DENTRO del onClick para capturar lo que el usuario escribió
            String _email = email.getText().toString().trim();
            String _pass = pass.getText().toString().trim();
            String _confirm_pass = confirmPass.getText().toString().trim();

            if(!_email.isEmpty() && !_pass.isEmpty() && !_confirm_pass.isEmpty() && _confirm_pass.equals(_pass)){

                mAuth.createUserWithEmailAndPassword(_email, _pass)
                        .addOnCompleteListener(Register.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign up success
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user != null){
                                    user.sendEmailVerification().addOnCompleteListener(tk -> {
                                        if(tk.isSuccessful()){
                                            Toast.makeText(Register.this, "Se ha enviado un email de verificación a \n" + user.getEmail(), Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Log.e("EmailVerification", "Error al enviar el email de verificación.", task.getException());
                                        }
                                    });
                                }
                                Toast.makeText(Register.this, "Sign Up successful", Toast.LENGTH_SHORT).show();
                                email.setText("");
                                pass.setText("");
                                confirmPass.setText("");
                            } else {
                                Log.w("ERROR", "createUserWithEmail:failure", task.getException());// Si falla, mostramos el mensaje
                                Toast.makeText(Register.this, "Authentication failed: "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                // Opcional: Avisar al usuario que faltan datos
                Toast.makeText(Register.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        }); // 4. Cerramos el setOnClickListener correctamente
    }
}