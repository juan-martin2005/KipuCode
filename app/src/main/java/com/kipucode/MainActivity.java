package com.kipucode;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kipucode.service.Utils;
import com.kipucode.view.Login;
import com.kipucode.view.Register;


public class MainActivity extends AppCompatActivity {

    TextView tvHero, tvSubHead;
    Button btnLogin, btnCreateAccount;
    ImageView ivMode;
    TypedValue typedValue = new TypedValue();
    int currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivMode = findViewById(R.id.iv_mode);

        currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentMode == Configuration.UI_MODE_NIGHT_YES){
            ivMode.setImageResource(R.drawable.light_mode);
        }else {
            ivMode.setImageResource(R.drawable.dark_mode);
        }
        changeTheme();

        tvHero = findViewById(R.id.tv_heroHead);
        tvSubHead = findViewById(R.id.subHeadline);

        btnCreateAccount = findViewById(R.id.btn_createAccount);
        btnLogin = findViewById(R.id.btn_logIn);


        getTheme().resolveAttribute(android.R.attr.textColorLink, typedValue, true);

        loginIntent();
        createAccountIntent();
        colorHero();
    }

    public void colorHero(){
        int colorEnlace = typedValue.data;
        String _hero = tvHero.getText().toString();
        SpannableString spannableString = Utils.getColoredText(_hero, "code", colorEnlace);

        tvHero.setText(spannableString);
    }

    public void changeTheme(){
        ivMode.setOnClickListener(v -> {
            if (currentMode == Configuration.UI_MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                ivMode.setImageResource(R.drawable.light_mode);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                ivMode.setImageResource(R.drawable.dark_mode);
            }
            recreate();
        });
    }

    public void loginIntent(){
        btnLogin.setOnClickListener(v -> {
            Intent login = new Intent(this, Login.class);
            this.startActivity(login);
        });
    }

    public void createAccountIntent(){
        btnCreateAccount.setOnClickListener(v -> {
            Intent createAccount = new Intent(this, Register.class);
            this.startActivity(createAccount);
        });
    }

}