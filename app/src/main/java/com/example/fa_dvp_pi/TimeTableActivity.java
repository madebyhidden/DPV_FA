package com.example.fa_dvp_pi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.timetable.DateAdapter;
import com.example.timetable.TimetableAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeTableActivity extends AppCompatActivity {

    {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
    private TextView tt_tvDate_;



    TimetableAdapter mondayAdapter, tuesdayAdapter, wednesdayAdapter, thursdayAdapter, fridayAdapter, saturdayAdapter, sundayAdapter;

    RecyclerView rvMonday, rvTuesday, rvWednesday, rvThursday, rvFriday, rvSaturday, rvSunday;
    List<TimetableAdapter.TimetableItem> mondayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> tuesdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> wednesdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> thursdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> fridayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> saturdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> sundayItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] savedValueSpinner1 = {"Выбрать"};
        final String[] savedValueSpinner2 = {"Выбрать"};
        final String[] savedValueSpinner3 = {"Выбрать"};
        Date currentDate = new Date();
        setContentView(R.layout.activity_time_table);


        // Получил ссылку на RecyclerView с датами
        RecyclerView rvDate = findViewById(R.id.timetable_rvDate);

        List<DateAdapter.DateItem> dateItems = new ArrayList<>();
        for (int i = 5; i <= 26; i = i +7){
            if (i<10){
                String dateValue = String.format("2024.02.0%s", i); // ваше значение даты
                System.out.println(dateValue);
                dateItems.add(new DateAdapter.DateItem(dateValue));
            }else {
                String dateValue = String.format("2024.02.%s", i); // ваше значение даты
                System.out.println(dateValue);
                dateItems.add(new DateAdapter.DateItem(dateValue));
            }

        }


            // Создал адаптер с этим списком и слушателем, который реализует интерфейс OnDateSelectedListener
        DateAdapter dateAdapter = new DateAdapter(dateItems, new DateAdapter.OnDateSelectedListener() {
            @Override
            public void onDateSelected(DateAdapter.DateItem dateItem) {
                reset();
                tt_tvDate_ = (TextView) findViewById(R.id.item_date_tvDate);

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

                    savedValueSpinner1[0] = jsonData.getString("spinner1");
                    savedValueSpinner2[0] = jsonData.getString("spinner2");
                    savedValueSpinner3[0] = jsonData.getString("spinner3");

                    // Используйте значения в вашем коде
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("mainForFA");
                PyObject result_void = pyObject.callAttr("update_disciplines", savedValueSpinner1[0], savedValueSpinner2[0]);
                PyObject result = pyObject.callAttr("update_schedule", savedValueSpinner3[0], curr_tv_text);
                // получение результата из Python в Java
                parseJsonArray(String.valueOf(result));
            }
        });

        rvDate.setAdapter(dateAdapter);





        // Добавляем элементы в LinearLayout для Пн
        rvMonday = findViewById(R.id.timetable_rvMonday);
        rvTuesday = findViewById(R.id.timetable_rvTuesday);
        rvWednesday = findViewById(R.id.timetable_rvWednesday);
        rvThursday = findViewById(R.id.timetable_rvThurday);
        rvFriday = findViewById(R.id.timetable_rvFriday);
        rvSaturday = findViewById(R.id.timetable_rvSaturday);
        rvSunday = findViewById(R.id.timetable_rvSunday);













    }

    public void reset(){
        mondayItems.clear();
        tuesdayItems.clear();
        wednesdayItems.clear();
        thursdayItems.clear();
        fridayItems.clear();
        saturdayItems.clear();
        sundayItems.clear();

    }
    public void parseJsonArray(String jsonArrayString) {

        int count_mon = 0;
        int count_tue = 0;
        int count_wed = 0;
        int count_thr = 0;
        int count_fri = 0;
        int count_sat = 0;


        // Получил ссылку на RecyclerView для понедельника







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
                    System.out.println(discipline + beginLesson+ endLesson+ auditorium+ kindOfWork);
                    mondayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork));

                    count_mon += 1;
                }
                if (dayOfWeekString.equals("Вт")){
                    tuesdayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork));
                    count_tue+= 1;
                }
                if (dayOfWeekString.equals("Ср")){
                    wednesdayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork));
                    count_wed += 1;
                }
                if (dayOfWeekString.equals("Чт")){
                    thursdayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork));
                    count_thr += 1;
                }
                if (dayOfWeekString.equals("Пт")){
                    fridayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork));
                    count_fri += 1;
                }
                if (dayOfWeekString.equals("Сб")){
                    saturdayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork));
                    count_sat += 1;
                }




            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(count_mon == 0){
            mondayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));
        }
        if(count_tue == 0){
            tuesdayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));
        }
        if(count_wed == 0){
            wednesdayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));
        }
        if(count_thr == 0){
            thursdayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));
        }
        if(count_fri == 0){
            fridayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));
        }
        if(count_sat == 0){
            saturdayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));
        }
        sundayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "",""));

        mondayAdapter = new TimetableAdapter(mondayItems);
        rvMonday.setAdapter(mondayAdapter);


        tuesdayAdapter = new TimetableAdapter(tuesdayItems);
        rvTuesday.setAdapter(tuesdayAdapter);


        wednesdayAdapter = new TimetableAdapter(wednesdayItems);
        rvWednesday.setAdapter(wednesdayAdapter);


        thursdayAdapter = new TimetableAdapter(thursdayItems);
        rvThursday.setAdapter(thursdayAdapter);

        fridayAdapter = new TimetableAdapter(fridayItems);
        rvFriday.setAdapter(fridayAdapter);

        saturdayAdapter = new TimetableAdapter(saturdayItems);
        rvSaturday.setAdapter(saturdayAdapter);

        sundayAdapter = new TimetableAdapter(sundayItems);
        rvSunday.setAdapter(sundayAdapter);

    }

    private void addItemsToLinearLayout(List<String> data, LinearLayout linearLayout) {
        int counter = 0;
        LinearLayout.LayoutParams layoutParams_disc = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT

        );
        System.out.println(data.get(0));
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
        }
    }

}