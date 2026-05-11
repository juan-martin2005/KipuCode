package com.kipucode.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.jspecify.annotations.NonNull;

public class Utils {
    public static SpannableString getSpannableString(Activity activity, String txt_link, Class<?> target, String charToSearch) {
        SpannableString spannableString = new SpannableString(txt_link);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(activity, target);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void updateDrawState(@androidx.annotation.NonNull TextPaint drawState){
                super.updateDrawState(drawState);
                drawState.setUnderlineText(false);
                drawState.setTypeface(Typeface.create(drawState.getTypeface(), Typeface.BOLD));
            }
        };

        int end = txt_link.length();
        int start;

        if (charToSearch.isEmpty()){
            start = 0;
        }else {
            int index = txt_link.indexOf(charToSearch);

            start = index + 2;
        }

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static SpannableString getColoredText(String fullText, String targetText, int color) {
        SpannableString spannable = new SpannableString(fullText);

        int start = fullText.indexOf(targetText);
        int end = start + targetText.length();

        if (start >= 0) {
            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannable;
    }

    public static String getAuthErrorMessage(Exception ex){

        if (ex instanceof FirebaseAuthInvalidUserException) {
            return "No existe una cuenta con este email";
        } else if (ex instanceof FirebaseAuthInvalidCredentialsException) {
            return "Email o contraseña incorrecta";
        } else if (ex instanceof FirebaseAuthUserCollisionException) {
            return "Este email ya está registrado";
        } else if (ex instanceof FirebaseNetworkException) {
            return "Error de red, verifica tu conexión";
        }

        return "Error de autenticacion, intentelo de nuevo";
    }

}
