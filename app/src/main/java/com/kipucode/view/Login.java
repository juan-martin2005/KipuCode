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

public class Login extends AppCompatActivity {

    TextView createAccount, forgotPassword;
    Button btnLogin;
    EditText email, pass;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        btnLogin.setOnClickListener(v -> {

            String _email = email.getText().toString().trim();
            String _pass = pass.getText().toString().trim();

            if(_email.isEmpty() || _pass.isEmpty()){
                Toast.makeText(Login.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            }

            signInWithEmail(_email, _pass);

        });
    }

    public boolean verifiedEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null && user.isEmailVerified();
    }

    public void signInWithEmail(String _email, String _pass){

        mAuth.signInWithEmailAndPassword(_email, _pass)
                .addOnCompleteListener(Login.this, task -> {

                    if (task.isSuccessful()) {
                        if(!verifiedEmail()){
                            Toast.makeText(Login.this, "Verifica tu email", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            return;
                        }
                        Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        email.setText("");
                        pass.setText("");
                    } else {
                        Log.w("ERROR", "signIn:failure", task.getException());
                        String errorMessage = Utils.getAuthErrorMessage(task.getException());
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}