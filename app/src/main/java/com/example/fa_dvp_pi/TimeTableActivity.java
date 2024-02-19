package com.example.fa_dvp_pi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.timetable.ColorAdapter;
import com.example.timetable.DateAdapter;
import com.example.timetable.DateTransformer;
import com.example.timetable.TimetableAdapter;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class TimeTableActivity extends AppCompatActivity {


    private int selectedPosition= -1;

    {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    private TextView tt_tvDate_;

    private TextView targetTextView;

    public boolean error = false;

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

    private TextView tvMon;
    private TextView tvTue;
    private TextView tvWed;
    private TextView tvThr;
    private TextView tvFri;
    private TextView tvSat;
    private TextView tvSun;

    private String content;

    private void make_spisok_intime() {
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

        AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.SNACKBAR)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("madebyhidden", "DPV_FA").showAppUpdated(true).setButtonUpdate(null);

        appUpdater.start();
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
        NestedScrollView scrollView = findViewById(R.id.scroll_view); // Замените R.id.scrollView на ваш ID ScrollView

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                targetTextView.getLocationOnScreen(location);
                int targetY = location[1];
                scrollView.smoothScrollTo(0, targetY - 450);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        reset();
        updatePage();


        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);


    }

    public static int getCurrentWeek() {
        // получаем текущую дату
        LocalDate date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.now();
        }
        // получаем объект WeekFields для текущей локали
        WeekFields weekFields = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            weekFields = WeekFields.of(Locale.getDefault());
        }
        // возвращаем номер недели в году по ISO-8601
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.get(weekFields.weekOfWeekBasedYear());
        }
        return 0;
    }
    public interface OnSnapPositionChangeListener {
        // метод, который вызывается, когда позиция элемента изменилась
        void onSnapPositionChange(int position);
    }

    public int snapPosition = RecyclerView.NO_POSITION;

    public class SnapOnScrollListener extends RecyclerView.OnScrollListener {

        // поле для хранения объекта SnapHelper
        private final SnapHelper snapHelper;
        // поле для хранения объекта OnSnapPositionChangeListener
        private final OnSnapPositionChangeListener onSnapPositionChangeListener;
        // поле для хранения текущей позиции элемента


        // конструктор класса
        public SnapOnScrollListener(SnapHelper snapHelper, OnSnapPositionChangeListener onSnapPositionChangeListener) {
            this.snapHelper = snapHelper;
            this.onSnapPositionChangeListener = onSnapPositionChangeListener;
        }

        // переопределяем метод, который вызывается при прокрутке RecyclerView
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            // получаем позицию элемента, к которому прикреплен SnapHelper
            int position = getSnapPosition(recyclerView);
            // проверяем, изменилась ли позиция
            if (position != snapPosition) {
                // обновляем текущую позицию
                snapPosition = position;
            }
        }

        // метод для получения позиции элемента, к которому прикреплен SnapHelper
        private int getSnapPosition(RecyclerView recyclerView) {
            // получаем LayoutManager RecyclerView
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // проверяем, что он не null
            if (layoutManager == null) {
                return RecyclerView.NO_POSITION;
            }
            // получаем видимый элемент, к которому прикреплен SnapHelper
            View snapView = snapHelper.findSnapView(layoutManager);
            // проверяем, что он не null
            if (snapView == null) {
                return RecyclerView.NO_POSITION;
            }
            // возвращаем позицию этого элемента
            return layoutManager.getPosition(snapView);
        }

        // метод для обработки изменения состояния прокрутки RecyclerView
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // если прокрутка остановилась
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // вызываем метод onSnapPositionChange с текущей позицией
                onSnapPositionChangeListener.onSnapPositionChange(snapPosition);
            }
        }
    }


    private void scroll_update(){

        RecyclerView rvDate = findViewById(R.id.timetable_rvDate);
        SnapHelper snapHelper = new PagerSnapHelper();
// прикрепляем его к RecyclerView
        snapHelper.attachToRecyclerView(rvDate);
        OnSnapPositionChangeListener onSnapPositionChangeListener = new OnSnapPositionChangeListener() {
            @Override
            public void onSnapPositionChange(int position) {

                Log.d("AAA", String.valueOf(snapPosition));
                selectedPosition = position;
                reset();
                updatePage();
            }
        };
        SnapOnScrollListener snapOnScrollListener = new SnapOnScrollListener(snapHelper, onSnapPositionChangeListener);
// добавляем этот объект в качестве слушателя прокрутки RecyclerView
        rvDate.addOnScrollListener(snapOnScrollListener);


    }

    private void setupDateRecyclerView() {



// Создаем адаптер и заполняем его данными

        RecyclerView rvDate = findViewById(R.id.timetable_rvDate);
        List<DateAdapter.DateItem> dateItems = createDateItems();
        System.out.println("TimeTavle "+ snapPosition);
        DateAdapter dateAdapter = new DateAdapter(dateItems, createDateSelectedListener(),  snapPosition);

        scroll_update();

        rvDate.smoothScrollToPosition(getCurrentWeek()-1);
        rvDate.setAdapter(dateAdapter);
    }




    private List<DateAdapter.DateItem> createDateItems() {
        List<DateAdapter.DateItem> dateItems = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());

        while (calendar.get(Calendar.MONTH) <= Calendar.JUNE) {
            String dateValue = dateFormat.format(calendar.getTime());
            dateItems.add(new DateAdapter.DateItem("от " +dateValue));
            calendar.add(Calendar.DAY_OF_MONTH, 7);
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
            Log.d("update", sub);
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
        NestedScrollView scrollView = findViewById(R.id.scroll_view); // Замените R.id.scrollView на ваш ID ScrollView

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
        String[] savedValueSpinner4 = {"Выбрать"};
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
            savedValueSpinner4[0] = jsonData.getString("spinner4");
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

        PyObject result = pyObject.callAttr("update_schedule", savedValueSpinner3[0], currentDate);

        if (String.valueOf(result).equals("NoMatch")) {
            error = true;
            Intent intent = new Intent(this, WayActivity.class);
            startActivity(intent);
        } else {
            parseJsonArray(String.valueOf(result), savedValueSpinner4[0]);

        }

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

    public void parseJsonArray(String jsonArrayString, String langTeacher) {


        tvMon = findViewById(R.id.timetable_tvMonday);
        tvTue = findViewById(R.id.timetable_tvTuesday);
        tvWed = findViewById(R.id.timetable_tvWednesday);
        tvThr = findViewById(R.id.timetable_tvThurday);
        tvFri = findViewById(R.id.timetable_tvFriday);
        tvSat = findViewById(R.id.timetable_tvSaturday);
        tvSun = findViewById(R.id.timetable_tvSunday);
        int count = 0;
        boolean check = true;
        boolean date_check = false;
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


                if(!date_check){
                    String date = jsonObject.optString("date", ""); // получаем строку вида "2024.02.19"
                    int year = Integer.parseInt(date.substring(0, 4)); // извлекаем год из строки
                    int month = Integer.parseInt(date.substring(5, 7)); // извлекаем месяц из строки
                    int day = Integer.parseInt(date.substring(8, 10)); // извлекаем день из строки

                    Calendar c = Calendar.getInstance(); // создаем объект календаря
                    c.set(year, month - 1, day); // устанавливаем дату в календаре
                    int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH); // получаем максимальное количество дней в месяце
                    int offset = monthMaxDays - day; // получаем смещение от конца месяца
                    if (offset >= 6) { // если смещение больше или равно 6, то просто прибавляем дни к текущему дню
                        tvMon.setText(String.format("Понедельник, %s", day));
                        tvTue.setText(String.format( "Вторник, %s", day + 1));
                        tvWed.setText(String.format( "Среда, %s", day + 2));
                        tvThr.setText(String.format( "Четверг, %s", day + 3));
                        tvFri.setText(String.format( "Пятница, %s", day + 4));
                        tvSat.setText(String.format( "Суббота, %s", day + 5));
                        tvSun.setText(String.format( "Воскресенье, %s", day + 6));
                    } else { // иначе, если смещение меньше 6, то нужно учитывать переход на следующий месяц
                        int nextMonth = (month % 12) + 1; // получаем номер следующего месяца
                        int nextDay = 1; // начинаем с первого дня следующего месяца
                        tvMon.setText(String.format("Понедельник, %s", day));
                        tvTue.setText(String.format( "Вторник, %s", day + 1));
                        tvWed.setText(String.format( "Среда, %s", day + 2));
                        tvThr.setText(String.format( "Четверг, %s", day + 3));
                        tvFri.setText(String.format( "Пятница, %s", day + 4));
                        tvSat.setText(String.format( "Суббота, %s", day + 5));
                        tvSun.setText(String.format( "Воскресенье, %s", day + 6));
                        for (int q = day + 6; q > monthMaxDays; q--) { // для каждого дня, который выходит за пределы месяца
                            switch (q - monthMaxDays) { // в зависимости от номера дня, меняем текст соответствующего дня недели
                                case 1:
                                    tvSun.setText(String.format( "Воскресенье, %s", nextDay));
                                    break;
                                case 2:
                                    tvSat.setText(String.format( "Суббота, %s", nextDay));
                                    break;
                                case 3:
                                    tvFri.setText(String.format( "Пятница, %s", nextDay));
                                    break;
                                case 4:
                                    tvThr.setText(String.format( "Четверг, %s", nextDay));
                                    break;
                                case 5:
                                    tvWed.setText(String.format( "Среда, %s", nextDay));
                                    break;
                                case 6:
                                    tvTue.setText(String.format( "Вторник, %s", nextDay));
                                    break;
                            }
                            nextDay++; // увеличиваем день следующего месяца
                        }
                    }
                    date_check = true;
                }






                List<TimetableAdapter.TimetableItem> dayItems = scheduleMap.get(dayOfWeekString);
                if (dayItems != null) {
                    boolean c = Objects.equals(prepod_name, langTeacher);

                    if (discipline.equals("Иностранный язык в профессиональной сфере") && c) {
                        if (check) {
                            dayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork, langTeacher, group, stream));
                            check = false;

                        } else {
                            continue;
                        }

                    } else if (discipline.equals("Иностранный язык в профессиональной сфере")) {
                        continue;
                    } else if (!c) {
                        dayItems.add(new TimetableAdapter.TimetableItem(discipline, beginLesson, endLesson, auditorium, kindOfWork, prepod_name, group, stream));

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
        final PackageInfo[] packageInfo = {null};
        if (item.getItemId() == R.id.action_settings) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", true);
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_update) {
            AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                    .setUpdateFrom(UpdateFrom.GITHUB)
                    .setGitHubUserAndRepo("madebyhidden", "DPV_FA")
                    .withListener(new AppUpdaterUtils.UpdateListener() {
                        @Override
                        public void onSuccess(Update update, Boolean isUpdateAvailable) {
                            PackageManager packageManager = getPackageManager();
                            try {
                                packageInfo[0] = packageManager.getPackageInfo(getPackageName(), 0);
                            } catch (PackageManager.NameNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            String v = packageInfo[0].versionName;
                            if (v.equals(update.getLatestVersion())) {
                                return;
                            } else {
                                // Получаем ссылку на Telegram
                                String telegramUrl = "https://t.me/fa_dpv"; // Замените на свою ссылку
                                // Создаем намерение для открытия ссылки
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                // Устанавливаем данные для намерения
                                intent.setData(Uri.parse(telegramUrl));
                                // Запускаем намерение
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onFailed(AppUpdaterError error) {
                            // Обрабатываем ошибку проверки обновления
                        }
                    });

            appUpdaterUtils.start();
        }
        return super.onOptionsItemSelected(item);
    }
}