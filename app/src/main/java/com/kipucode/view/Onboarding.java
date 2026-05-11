package com.kipucode.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kipucode.R;
import com.kipucode.utils.SliderTextAdapter;
import com.kipucode.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class Onboarding extends AppCompatActivity {

    TextView tvHero;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    Button btnLogin, btnCreateAccount;
    ImageView ivMode;
    TypedValue typedValue = new TypedValue();
    int currentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewPager = findViewById(R.id.viewPager_Slider);
        tabLayout = findViewById(R.id.tabLayout_Dots);

        SliderTextAdapter adapter = getSliderTextAdapter();
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                }
        ).attach();

        ivMode = findViewById(R.id.iv_mode);

        currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentMode == Configuration.UI_MODE_NIGHT_YES){
            ivMode.setImageResource(R.drawable.light_mode);
        }else {
            ivMode.setImageResource(R.drawable.dark_mode);
        }
        changeTheme();

        tvHero = findViewById(R.id.tv_heroHead);

        btnCreateAccount = findViewById(R.id.btn_createAccount);
        btnLogin = findViewById(R.id.btn_logIn);


        getTheme().resolveAttribute(android.R.attr.textColorLink, typedValue, true);

        loginIntent();
        createAccountIntent();
    }

    private SliderTextAdapter getSliderTextAdapter() {
        List<String> listaSubTextos = new ArrayList<>();

        listaSubTextos.add("Build real programming skills through guided lessons and hands on experience");
        listaSubTextos.add("Master fundamental logic through structured and easy to follow learning paths");
        listaSubTextos.add("Transition from theory to execution with interactive coding challenges");
        listaSubTextos.add("Transform basic knowledge into advanced & professional engineering skills.");

        final String[] palabrasClave = {"code", "clarity", "practice", "purpose"};

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                colorHero(palabrasClave[position]);
            }
        });

        return new SliderTextAdapter(listaSubTextos);
    }

    public void colorHero(String text){
        int colorEnlace = typedValue.data;
        String _hero = tvHero.getText().toString();

        SpannableString spannableString = Utils.getColoredText(_hero, text, colorEnlace);

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