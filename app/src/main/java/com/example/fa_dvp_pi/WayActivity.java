package com.example.fa_dvp_pi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

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


        AtomicReference<String> direction = new AtomicReference<>();
        TableViewPI.setOnClickListener(v -> {
            TableViewPI.setBackgroundResource(R.drawable.border_selected);
            TableViewITM.setBackgroundResource(R.drawable.border);
            TableViewTCBM.setBackgroundResource(R.drawable.border);
            direction.set("PI");
        });

        TableViewITM.setOnClickListener(v -> {
            TableViewPI.setBackgroundResource(R.drawable.border);
            TableViewITM.setBackgroundResource(R.drawable.border_selected);
            TableViewTCBM.setBackgroundResource(R.drawable.border);
            direction.set("ITM");
        });

        TableViewTCBM.setOnClickListener(v -> {
            TableViewPI.setBackgroundResource(R.drawable.border);
            TableViewITM.setBackgroundResource(R.drawable.border);
            TableViewTCBM.setBackgroundResource(R.drawable.border_selected);
            direction.set("TCBM");
        });


        btn = (Button) findViewById(R.id.btn_go_next);
        btn.setOnClickListener(view -> {

            try {

                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("direction.txt"))){
                    bufferedWriter.write(direction.get());
                } catch (IOException e) {
                    System.err.println("Ошибка при записи в файл: " + e.getMessage());
                }

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }
}
