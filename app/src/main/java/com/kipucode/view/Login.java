package com.kipucode.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kipucode.MainActivity;
import com.kipucode.R;

public class Login extends AppCompatActivity {

    TextView createAccount;

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

        String txt_createAccount = createAccount.getText().toString();

        SpannableString spannableString = new SpannableString(txt_createAccount);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent register = new Intent(Login.this, Register.class);
                startActivity(register);
            }
        };

        int fin = txt_createAccount.length();
        int inicio = txt_createAccount.indexOf("?") + 2;

        spannableString.setSpan(clickableSpan,inicio,fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        createAccount.setText(spannableString);
        createAccount.setMovementMethod(LinkMovementMethod.getInstance());
    }

}