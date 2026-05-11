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
import com.kipucode.utils.Utils;

public class Register extends AppCompatActivity {

    TextView login;
    EditText email, pass, confirmPass;
    Button btnSingUp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        btnSingUp = findViewById(R.id.btn_createAccount);
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password);
        confirmPass = findViewById(R.id.et_confirm_password);

        spannableStringLogin();
        setupFirebaseAuth();
    }

    public void spannableStringLogin(){
        login.setHighlightColor(Color.TRANSPARENT);

        String txt_login = login.getText().toString();

        SpannableString spannableString = Utils.getSpannableString(this, txt_login, Login.class, "?");

        login.setText(spannableString);
        login.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setupFirebaseAuth() {

        btnSingUp.setOnClickListener(v -> {

            String _email = email.getText().toString().trim();
            String _pass = pass.getText().toString().trim();
            String _confirm_pass = confirmPass.getText().toString().trim();

            if(_email.isEmpty() || _pass.isEmpty() || _confirm_pass.isEmpty()){
                Toast.makeText(Register.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            }
            if(!_confirm_pass.equals(_pass)){
                Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }

            createUserAccount(_email, _pass);

        });
    }

    public void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(tk -> {
                if(tk.isSuccessful()){
                    Toast.makeText(Register.this, "Se ha enviado un email de verificación a \n" + user.getEmail(), Toast.LENGTH_LONG).show();
                }
                else {
                    Log.e("EmailVerification", "Error al enviar el email de verificación.", tk.getException());
                    Toast.makeText(Register.this, "Error al enviar el email de verificación.", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    public void createUserAccount(String _email, String _pass){
        mAuth.createUserWithEmailAndPassword(_email, _pass)
                .addOnCompleteListener(Register.this, task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(Register.this, "Se ha creado tu cuenta", Toast.LENGTH_SHORT).show();
                        sendEmailVerification();
                        email.setText("");
                        pass.setText("");
                        confirmPass.setText("");
                    } else {
                        Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                        String errorMessage = Utils.getAuthErrorMessage(task.getException());
                        Toast.makeText(Register.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}