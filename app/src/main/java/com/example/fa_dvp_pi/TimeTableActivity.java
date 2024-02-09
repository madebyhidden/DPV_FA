package com.example.fa_dvp_pi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.timetable.DateAdapter;
import com.example.timetable.DateTransformer;
import com.example.timetable.TimetableAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class TimeTableActivity extends AppCompatActivity {

    {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    private TextView tt_tvDate_;

    private TextView targetTextView;

    TimetableAdapter mondayAdapter, tuesdayAdapter, wednesdayAdapter, thursdayAdapter, fridayAdapter, saturdayAdapter, sundayAdapter;

    RecyclerView rvMonday, rvTuesday, rvWednesday, rvThursday, rvFriday, rvSaturday, rvSunday;
    List<TimetableAdapter.TimetableItem> mondayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> tuesdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> wednesdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> thursdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> fridayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> saturdayItems = new ArrayList<>();
    List<TimetableAdapter.TimetableItem> sundayItems = new ArrayList<>();

    List<String> weekdays = Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс");

    Map<String, List<TimetableAdapter.TimetableItem>> scheduleMap = new HashMap<>();

    {
        scheduleMap.put("Пн", mondayItems);
        scheduleMap.put("Вт", tuesdayItems);
        scheduleMap.put("Ср", wednesdayItems);
        scheduleMap.put("Чт", thursdayItems);
        scheduleMap.put("Пт", fridayItems);
        scheduleMap.put("Сб", saturdayItems);
        scheduleMap.put("Вс", sundayItems);
    }

    private String content;
    private void make_spisok_intime()
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

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        reset();
        make_spisok_intime();
        initializeViews();
        setupDateRecyclerView();
    }


    private void scroll_to_date() {


        Date currentDate = new Date();

        // Создание объекта Calendar и установка текущей даты
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Получение номера дня недели (0 - Воскресенье, 1 - Понедельник, ..., 6 - Суббота)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Преобразование номера дня недели в строку с названием дня недели
        String[] daysOfWeek = new String[]{"Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
        String dayOfWeekString = daysOfWeek[dayOfWeek - 1]; // -1, так как нумерация в массиве начинается с 0

        switch (dayOfWeekString) {
            case "Понедельник":
                targetTextView = findViewById(R.id.timetable_tvMonday);
                break;
            case "Вторник":
                targetTextView = findViewById(R.id.timetable_tvTuesday);
                break;
            case "Среда":
                targetTextView = findViewById(R.id.timetable_tvWednesday);
                break;
            case "Четверг":
                targetTextView = findViewById(R.id.timetable_tvThurday);
                break;
            case "Пятница":
                targetTextView = findViewById(R.id.timetable_tvFriday);
                break;
            case "Суббота":
                targetTextView = findViewById(R.id.timetable_tvSaturday);
                break;
            case "Воскресенье":
                targetTextView = findViewById(R.id.timetable_tvSunday);
                break;

        }

        // Замените R.id.targetTextView на ваш ID TextView
        ScrollView scrollView = findViewById(R.id.scroll_view); // Замените R.id.scrollView на ваш ID ScrollView

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                targetTextView.getLocationOnScreen(location);
                int targetY = location[1];
                scrollView.smoothScrollTo(0, targetY - 400);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        reset();
        updatePage();


    }

    private void setupDateRecyclerView() {


// Создаем адаптер и заполняем его данными

        RecyclerView rvDate = findViewById(R.id.timetable_rvDate);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvDate);
        List<DateAdapter.DateItem> dateItems = createDateItems();
        DateAdapter dateAdapter = new DateAdapter(dateItems, createDateSelectedListener());
        rvDate.setAdapter(dateAdapter);
    }

    private List<DateAdapter.DateItem> createDateItems() {
        List<DateAdapter.DateItem> dateItems = new ArrayList<>();
        for (int i = 5; i <= 26; i = i + 7) {
            String dateValue = (i < 10) ? String.format("от 0%s февраля", i) : String.format("от %s февраля", i);
            dateItems.add(new DateAdapter.DateItem(dateValue));


        }
        return dateItems;
    }

    private DateAdapter.OnDateSelectedListener createDateSelectedListener() {
        return dateItem -> {
            reset();
            updatePage();
        };
    }

    private void updatePage() {
        tt_tvDate_ = findViewById(R.id.item_date_tvDate);

        if (tt_tvDate_ != null) {
            String curr_tv_text = tt_tvDate_.getText().toString();
            String sub = curr_tv_text.substring(3);
            String transformedDate = DateTransformer.transformDate(String.format("%s 2024", sub));

            try {
                readJsonDataAndUpdateSchedule(transformedDate);
            } catch (Exception e) {
                readJsonDataAndUpdateSchedule(getMondayDate());
                transformedDate = getMondayDate_curr();
            }

            if (Objects.equals(transformedDate, getMondayDate_curr())) {
                scroll_to_date();
            } else {
                scroll_to_monday();
            }

        } else {

            readJsonDataAndUpdateSchedule(getMondayDate());
            Log.e("TextView", "TextView tt_tvDate_ is null");
        }
    }

    private void scroll_to_monday() {

        targetTextView = findViewById(R.id.timetable_tvMonday);


        // Замените R.id.targetTextView на ваш ID TextView
        ScrollView scrollView = findViewById(R.id.scroll_view); // Замените R.id.scrollView на ваш ID ScrollView

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                targetTextView.getLocationOnScreen(location);
                int targetY = location[1];
                scrollView.smoothScrollTo(0, targetY - 500);
            }
        });
    }

    public static String getMondayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d.%02d.%02d", year, month, dayOfMonth);
    }

    public static String getMondayDate_curr() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToAdd = 2 - dayOfWeek; // Вычитаем из текущего дня недели количество дней, чтобы вернуться к понедельнику
        if (daysToAdd > 0) {
            daysToAdd -= 7; // Если текущий день недели - воскресенье, учитываем, что нужно перейти к предыдущей неделе
        }
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d.%02d.%02d", year, month, dayOfMonth);
    }


    private void readJsonDataAndUpdateSchedule(String currentDate) {
        String[] savedValueSpinner1 = {"Выбрать"};
        String[] savedValueSpinner2 = {"Выбрать"};
        String[] savedValueSpinner3 = {"Выбрать"};
        String fileName = "data.json";
        try (FileInputStream fis = openFileInput(fileName)) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonData = new JSONObject(sb.toString());
            savedValueSpinner1[0] = jsonData.getString("spinner1");
            savedValueSpinner2[0] = jsonData.getString("spinner2");
            savedValueSpinner3[0] = jsonData.getString("spinner3");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.d("Error IOException | JSONException", Objects.requireNonNull(e.getMessage()));
        }
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("mainForFA");
//        PyObject result_void = pyObject.callAttr("update_disciplines", savedValueSpinner1[0],
//                savedValueSpinner2[0], "PI");

        PyObject result_void = pyObject.callAttr("update_disciplines", savedValueSpinner1[0],
                savedValueSpinner2[0], content);

        System.out.println(content);
        PyObject result = pyObject.callAttr("update_schedule", savedValueSpinner3[0], currentDate);

        parseJsonArray(String.valueOf(result));
    }

    private void initializeViews() {
        rvMonday = findViewById(R.id.timetable_rvMonday);
        rvTuesday = findViewById(R.id.timetable_rvTuesday);
        rvWednesday = findViewById(R.id.timetable_rvWednesday);
        rvThursday = findViewById(R.id.timetable_rvThurday);
        rvFriday = findViewById(R.id.timetable_rvFriday);
        rvSaturday = findViewById(R.id.timetable_rvSaturday);
        rvSunday = findViewById(R.id.timetable_rvSunday);
    }

    public void reset() {
        mondayItems.clear();
        tuesdayItems.clear();
        wednesdayItems.clear();
        thursdayItems.clear();
        fridayItems.clear();
        saturdayItems.clear();
        sundayItems.clear();

    }

    public void parseJsonArray(String jsonArrayString) {
        boolean check = true;
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
                String prepod_name = jsonObject.optString("lecturer_title", "");
                String group = jsonObject.optString("group", "");
                String stream = jsonObject.optString("stream", "");
                String date = jsonObject.optString("date", "");

                List<TimetableAdapter.TimetableItem> dayItems = scheduleMap.get(dayOfWeekString);
                if (dayItems != null) {

                    if(discipline.equals("Иностранный язык в профессиональной сфере")){
                        if (check) {
                            dayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, "Персонально", kindOfWork, "", group, stream));
                            check = false;

                        }else {
                            continue;
                        }

                    }else {
                        dayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork, prepod_name, group, stream));
                        System.out.println();
                    }


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Error JSONException", Objects.requireNonNull(e.getMessage()));
        }


        for (String day : weekdays) {
            List<TimetableAdapter.TimetableItem> dayItems = scheduleMap.get(day);
            if (dayItems != null && dayItems.isEmpty()) {
                dayItems.add(new TimetableAdapter.TimetableItem("Сегодня выходной", "", "", "", "", "", "", ""));
            }

            RecyclerView recyclerView = findRecyclerViewByDay(day);
            TimetableAdapter adapter = new TimetableAdapter(dayItems);
            assert recyclerView != null;
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);

        }

    }

    private RecyclerView findRecyclerViewByDay(String day) {
        switch (day) {
            case "Пн":
                return rvMonday;
            case "Вт":
                return rvTuesday;
            case "Ср":
                return rvWednesday;
            case "Чт":
                return rvThursday;
            case "Пт":
                return rvFriday;
            case "Сб":
                return rvSaturday;
            case "Вс":
                return rvSunday;
            default:
                return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", true);
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}