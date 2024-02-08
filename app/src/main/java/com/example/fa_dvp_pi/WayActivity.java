package com.example.fa_dvp_pi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
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


        final TableRow TableViewPI = findViewById(R.id.textViewPI);
        final TableRow TableViewITM = findViewById(R.id.textViewITM);
        final TableRow TableViewTCBM = findViewById(R.id.textViewTCBM);

        final TextView textViewPI = findViewById(R.id.view_pi);
        final TextView textViewITM = findViewById(R.id.view_itm);
        final TextView textViewTCBM = findViewById(R.id.view_tcbm);

        TableViewPI.setOnClickListener(v -> {
            TableViewPI.setBackgroundResource(R.drawable.border_selected);
            TableViewITM.setBackgroundResource(R.drawable.border);
            TableViewTCBM.setBackgroundResource(R.drawable.border);
        });

        TableViewITM.setOnClickListener(v -> {
            TableViewPI.setBackgroundResource(R.drawable.border);
            TableViewITM.setBackgroundResource(R.drawable.border_selected);
            TableViewTCBM.setBackgroundResource(R.drawable.border);
        });

        TableViewTCBM.setOnClickListener(v -> {
            TableViewPI.setBackgroundResource(R.drawable.border);
            TableViewITM.setBackgroundResource(R.drawable.border);
            TableViewTCBM.setBackgroundResource(R.drawable.border_selected);
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
