package com.kipucode.service;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import org.jspecify.annotations.NonNull;

public class Utils {

    public static SpannableString getSpannableString(Context context, String txt_link, Class<?> target, String charToSearch) {
        SpannableString spannableString = new SpannableString(txt_link);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context, target);
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@androidx.annotation.NonNull TextPaint drawState){
                super.updateDrawState(drawState);
                drawState.setUnderlineText(false);
                drawState.setFakeBoldText(true);
            }
        };

        int end = txt_link.length();
        int start = txt_link.indexOf(charToSearch) + 2;

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
