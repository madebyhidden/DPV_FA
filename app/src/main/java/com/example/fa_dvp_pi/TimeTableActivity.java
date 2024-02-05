package com.example.fa_dvp_pi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TimeTableActivity extends AppCompatActivity {
    {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
    private TextView tt_tvDate_;
    List<String> mondayData = new ArrayList<>();
    List<String> TuesdayData = new ArrayList<>();
    List<String> WednesdayData = new ArrayList<>();
    List<String> ThurdayData = new ArrayList<>();
    List<String> FridayData = new ArrayList<>();
    List<String> SaturdayData = new ArrayList<>();
    List<String> SundayData = new ArrayList<>();

    LinearLayout llMonday,llTuesday ,llWednesday ,llThurday , llFriday,llSaturday , llSunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String savedValueSpinner3 = "Выбрать";
        Date currentDate = new Date();
        setContentView(R.layout.activity_time_table);


        // Пример данных. Здесь вам нужно использовать ваши данные.


        // Получаем ссылку на LinearLayout для Пн
        llMonday = findViewById(R.id.llMonday);
        llTuesday = findViewById(R.id.llTuesday);
        llWednesday = findViewById(R.id.llWednesday);
        llThurday = findViewById(R.id.llThurday);
        llFriday = findViewById(R.id.llFriday);
        llSaturday = findViewById(R.id.llSaturday);
        llSunday = findViewById(R.id.llSunday);

        // Добавляем элементы в LinearLayout для Пн




        tt_tvDate_ = (TextView) findViewById(R.id.timetable_tvDate);
        tt_tvDate_.setText(MainActivity.findPreviousMonday(currentDate));
        String curr_tv_text = (String) tt_tvDate_.getText();


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


            savedValueSpinner3 = jsonData.getString("spinner3");

            // Используйте значения в вашем коде
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("mainForFA");
        PyObject result = pyObject.callAttr("update_schedule", savedValueSpinner3, curr_tv_text);
        // получение результата из Python в Java
        parseJsonArray(String.valueOf(result));
        Log.d("TimeTableActivity", String.valueOf(result));





    }
    public void parseJsonArray(String jsonArrayString) {

        int count_mon = 0;
        int count_tue = 0;
        int count_wed = 0;
        int count_thr = 0;
        int count_fri = 0;
        int count_sat = 0;

        try {
            // Создаем JSONArray из строки
            JSONArray jsonArray = new JSONArray(jsonArrayString);

            // Проходим по каждому элементу массива
            for (int i = 0; i < jsonArray.length(); i++) {
                // Получаем объект JSONObject из массива
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Извлекаем значения по ключам
                String auditorium = jsonObject.optString("auditorium", "");
                String dayOfWeekString = jsonObject.optString("dayOfWeekString", "");
                String kindOfWork = jsonObject.optString("kindOfWork", "");
                String discipline = jsonObject.optString("discipline", "");
                String beginLesson = jsonObject.optString("beginLesson", "");
                String endLesson = jsonObject.optString("endLesson", "");
                String date = jsonObject.optString("date", "");
                if (dayOfWeekString.equals("Пн")){
                    mondayData.add(discipline);
                    mondayData.add(kindOfWork);
                    count_mon += 1;
                }
                if (dayOfWeekString.equals("Вт")){
                    TuesdayData.add(discipline);
                    count_tue+= 1;
                }
                if (dayOfWeekString.equals("Ср")){
                    WednesdayData.add(discipline);
                    count_wed += 1;
                }
                if (dayOfWeekString.equals("Чт")){
                    ThurdayData.add(discipline);
                    count_thr += 1;
                }
                if (dayOfWeekString.equals("Пт")){
                    FridayData.add(discipline);
                    count_fri += 1;
                }
                if (dayOfWeekString.equals("Сб")){
                    SaturdayData.add(discipline);
                    count_sat += 1;
                }




            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(count_mon == 0){
            mondayData.add("Сегодня выходной");
        }
        if(count_tue == 0){
            TuesdayData.add("Сегодня выходной");
        }
        if(count_wed == 0){
            WednesdayData.add("Сегодня выходной");
        }
        if(count_thr == 0){
            ThurdayData.add("Сегодня выходной");
        }
        if(count_fri == 0){
            FridayData.add("Сегодня выходной");
        }
        if(count_sat == 0){
            SaturdayData.add("Сегодня выходной");
        }
        SundayData.add("Сегодня выходной");
        addItemsToLinearLayout(mondayData, llMonday);
        addItemsToLinearLayout(TuesdayData, llTuesday);
        addItemsToLinearLayout(WednesdayData, llWednesday);
        addItemsToLinearLayout(ThurdayData, llThurday);
        addItemsToLinearLayout(FridayData, llFriday);
        addItemsToLinearLayout(SaturdayData, llSaturday);
        addItemsToLinearLayout(SundayData, llSunday);

    }

    private void addItemsToLinearLayout(List<String> data, LinearLayout linearLayout) {
        int counter = 0;
        LinearLayout.LayoutParams layoutParams_disc = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT

        );

        for (String item : data) {
            if( counter % 2 == 0){
                TextView textView = new TextView(this);
                textView.setText(item);
                textView.setTextSize(20);
                // Здесь вы можете настроить стили для TextView и другие параметры
                linearLayout.addView(textView, layoutParams_disc);

            } else {
                TextView textView = new TextView(this);
                textView.setText(item);
                textView.setTextSize(10);
                // Здесь вы можете настроить стили для TextView и другие параметры
                linearLayout.addView(textView, layoutParams_disc);

            }
            counter += 1;
            TextView textView = new TextView(this);
            textView.setText(item);
            // Здесь вы можете настроить стили для TextView и другие параметры
            linearLayout.addView(textView, layoutParams_disc);
        }
    }

}