package com.example.networks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty())
            return input;
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c))
                nextTitleCase = true;
            else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            } else
                c = Character.toLowerCase(c);

            titleCase.append(c);
        }
        return titleCase.toString();
    }

    public static String getGreetings() {
        /*
          This Method Returns Greetings As Per The Time Of The Device
         */
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        Date date;
        try {
            date = new SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(currentTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert date != null;
        int hour = Integer.parseInt(new SimpleDateFormat("H", Locale.getDefault()).format(date));
        String[] message = {"Good Morning!", "Good Afternoon!", "Good Evening!"};
        if (hour >= 12 && hour <= 18) return (message[1]);
        else if (hour >= 19 && hour <= 23) return (message[2]);
        else return (message[0]);
    }

    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null)
                return !netInfo.isConnected();
        }
        return true;
    }

    public static void setAutoOpenKeyboardOnFocus(final View view) {
        /*
         * Automatically open the keyboard if a EditText is focused
         * */
        View.OnFocusChangeListener focusChangeListener = (editText, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        };
        view.setOnFocusChangeListener(focusChangeListener);
    }

    public static void activityActionBar(String title, ActionBar actionBar, Activity activity) {
        /*
         * Change The ActionBar Style For Activities
         * */
        actionBar.setElevation(5);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(activity, R.color.white)));
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(new TypefaceSpan("monospace"), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new AbsoluteSizeSpan(18, true), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new ScaleXSpan(1.1f), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(titleSpannable);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static void makeToast(String message, Context context) {
        /*
         * Make Custom Toast
         * */
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.layout_toast, null);
        TextView textView = layout.findViewById(R.id.textViewMessage);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static SpannableString spannableString(String s) {
        SpannableString titleSpannable = new SpannableString(s);
        titleSpannable.setSpan(new TypefaceSpan("monospace"), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new AbsoluteSizeSpan(18, true), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new ScaleXSpan(1.1f), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Set letter spacing to 1.2 times the default
        titleSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return titleSpannable;
    }


    public static Bitmap rotateImageIfRequired(Context context, Bitmap bitmap, Uri uri) throws IOException {
        ExifInterface ei = new ExifInterface(context.getContentResolver().openInputStream(uri));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
                return bitmap;
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotatedBitmap;
    }
}