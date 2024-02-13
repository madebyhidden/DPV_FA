package com.example.fa_dvp_pi;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    private List<String> subjectList = new ArrayList<>();
    List<String> groupList = new ArrayList<>();


    private String content;


    public void make_spisoks()
    {
        String fileName = "dir.json";
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
            content = jsonData.getString("direction");


            // Используйте значения в вашем коде
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if(content.equals("PI")) {

            subjectList.add("Модуль ERP-системы");
            subjectList.add("Модуль Системное программирование");
            subjectList.add("Модуль Управление разработкой");
            subjectList.add("Модуль Технологии искусственного интеллекта");
            subjectList.add("Модуль Языки и методы программирования");
            subjectList.add("Модуль Разработка распределенных приложений");
            subjectList.add("Модуль Технологии машинного обучения");
            subjectList.add("Модуль Финтех");

            groupList.add("ПИ21-1");
            groupList.add("ПИ21-2");
            groupList.add("ПИ21-3");
            groupList.add("ПИ21-4");
            groupList.add("ПИ21-5");
            groupList.add("ПИ21-6");
            groupList.add("ПИ21-7");
        }

        if(content.equals("ITM")) {
            System.out.println("Love ITM");
            subjectList.add("Модуль КИС для среднего и крупного бизнеса");
            subjectList.add("Модуль Информационно-аналитические технологии");
            subjectList.add("Модуль Технологии управления коллективной работой");
            subjectList.add("Модуль Сквозные технологии цифровой экономики");

            groupList.add("ИТМ21-1");
            groupList.add("ИТМ21-2");
            groupList.add("ИТМ21-3");
            groupList.add("ИТМ21-4");
            groupList.add("ИТМ21-5");
        }

        if(content.equals("TCBM")) {
            subjectList.add("'Модуль Цифровая трансформация бизнеса");
            subjectList.add("Модуль Управление цифровыми ресурсами компании");
            subjectList.add("Модуль ИТ-менеджмент");
            subjectList.add("Модуль Технологии анализа данных");

            groupList.add("ТЦБМ21-1");
            groupList.add("ТЦБМ21-2");
            groupList.add("ТЦБМ21-3");
            groupList.add("ТЦБМ21-4");
            groupList.add("ТЦБМ21-5");
        }

        System.out.println(content);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        make_spisoks();
        System.out.println(String.valueOf(content));


        mySpinner1 = (Spinner) findViewById(R.id.spinner1);

        mySpinner2 = (Spinner) findViewById(R.id.spinner2);
        mySpinner3 = (Spinner) findViewById(R.id.spinner_pi);


        btnSave__ = (Button) findViewById(R.id.btnSave);
        btnSave__.setOnClickListener(view -> {
            saveData();

            try {
                Intent intent = new Intent(view.getContext(), TimeTableActivity.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE);
                startActivity(intent);

            } catch (Exception e) {
                throw new RuntimeException(e);
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
//        pyObject.callAttr("update_disciplines", selectedValueSpinner1,
//                selectedValueSpinner2, "PI");
        pyObject.callAttr("update_disciplines", selectedValueSpinner1,
                selectedValueSpinner2, content);

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

//        ArrayAdapter<String> adapter;
//        adapter = (ArrayAdapter<String>) mySpinner1.getAdapter();
//        int spinner1Position = adapter.getPosition(savedValueSpinner1);
//        mySpinner1.setSelection(spinner1Position);
//
//        adapter = (ArrayAdapter<String>) mySpinner2.getAdapter();
//        int spinner2Position = adapter.getPosition(savedValueSpinner2);
//        mySpinner2.setSelection(spinner2Position);
//
//        adapter = (ArrayAdapter<String>) mySpinner3.getAdapter();
//        int spinner3Position = adapter.getPosition(savedValueSpinner3);
//        mySpinner3.setSelection(spinner3Position);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subjectList
        );
        mySpinner1.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subjectList
        );
        mySpinner2.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                groupList
        );
        mySpinner3.setAdapter(adapter3);
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