package com.example.fa_dvp_pi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WayActivity extends AppCompatActivity {
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

//        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//
//        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
//
//
//        if (isFirstRun) {
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putBoolean("isFirstRun", false);
//            editor.apply();
//        }
//        else {
//            Intent intent = new Intent(this, WayActivity.class);
//            startActivity(intent);
//            finish();
//        }


        final TextView textViewPI = findViewById(R.id.textViewPI);
        final TextView textViewITM = findViewById(R.id.textViewITM);
        final TextView textViewTCBM = findViewById(R.id.textViewTCBM);

        textViewPI.setOnClickListener(v -> {
            textViewPI.setBackgroundResource(R.drawable.border_selected);
            textViewITM.setBackgroundResource(R.drawable.border);
            textViewTCBM.setBackgroundResource(R.drawable.border);
        });

        textViewITM.setOnClickListener(v -> {
            textViewPI.setBackgroundResource(R.drawable.border);
            textViewITM.setBackgroundResource(R.drawable.border_selected);
            textViewTCBM.setBackgroundResource(R.drawable.border);
        });

        textViewTCBM.setOnClickListener(v -> {
            textViewPI.setBackgroundResource(R.drawable.border);
            textViewITM.setBackgroundResource(R.drawable.border);
            textViewTCBM.setBackgroundResource(R.drawable.border_selected);
        });


        btn = (Button) findViewById(R.id.btn_go_next);
        btn.setOnClickListener(view -> {

            try {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }
}
