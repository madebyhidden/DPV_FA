package com.example.fa_dvp_pi;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    private Spinner mySpinner1;
    private Spinner mySpinner2;
    private Spinner mySpinner3;
    private Button btnSave__;
    public String monday_finder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);

        mySpinner1 = (Spinner) findViewById(R.id.spinner1);

        mySpinner2 = (Spinner) findViewById(R.id.spinner2);
        mySpinner3 = (Spinner) findViewById(R.id.spinner_pi);


        btnSave__ = (Button) findViewById(R.id.btnSave);
        btnSave__.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                try {
                    Intent intent = new Intent(view.getContext(), TimeTableActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        monday_finder = getMondayDate();
        loadData();
    }

    private void saveData() {
        String curr_tv_text = getMondayDate();
        Log.d("MyActivity", curr_tv_text);
        // Получите выбранные значения из выпадающих списков
        String selectedValueSpinner1 = mySpinner1.getSelectedItem().toString();
        String selectedValueSpinner2 = mySpinner2.getSelectedItem().toString();
        String selectedValueSpinner3 = mySpinner3.getSelectedItem().toString();

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("mainForFA");
        pyObject.callAttr("update_disciplines", selectedValueSpinner1, selectedValueSpinner2);

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("spinner1", selectedValueSpinner1);
            jsonData.put("spinner2", selectedValueSpinner2);
            jsonData.put("spinner3", selectedValueSpinner3);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
        }


        String fileName = "data.json";
        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(jsonData.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadData() {
        String savedValueSpinner1 = "Выбрать";
        String savedValueSpinner2 = "Выбрать";
        String savedValueSpinner3 = "Выбрать";

        String fileName = "data.json";
        try (FileInputStream fis = openFileInput(fileName)) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // Преобразовываем JSON-строку в объект JSONObject
            JSONObject jsonData = new JSONObject(sb.toString());

            // Получаем значения из объекта JSONObject
            savedValueSpinner1 = jsonData.getString("spinner1");
            savedValueSpinner2 = jsonData.getString("spinner2");
            savedValueSpinner3 = jsonData.getString("spinner3");

            // Используйте значения в вашем коде
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        // Установка сохраненных значений в спиннеры
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) mySpinner1.getAdapter();
        int spinner1Position = adapter.getPosition(savedValueSpinner1);
        mySpinner1.setSelection(spinner1Position);

        adapter = (ArrayAdapter<String>) mySpinner2.getAdapter();
        int spinner2Position = adapter.getPosition(savedValueSpinner2);
        mySpinner2.setSelection(spinner2Position);

        adapter = (ArrayAdapter<String>) mySpinner3.getAdapter();
        int spinner3Position = adapter.getPosition(savedValueSpinner3);
        mySpinner3.setSelection(spinner3Position);
    }


    public static String getMondayDate() {
        android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
        calendar.set(android.icu.util.Calendar.DAY_OF_WEEK, android.icu.util.Calendar.MONDAY);
        int year = calendar.get(android.icu.util.Calendar.YEAR);
        int month = calendar.get(android.icu.util.Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d.%02d.%02d", year, month, dayOfMonth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}