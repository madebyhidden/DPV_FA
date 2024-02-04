package com.example.fa_dvp_pi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fa_dvp_pi.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Spinner mySpinner1;
    private Spinner mySpinner2;
    private Spinner mySpinner3;
    private Button btnSave__;
    private TextView text;

    private TextView tvDate_;

    private String monday_finder;

    private Button btnDecreaseDate_;
    private Button btnIncreaseDate_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mySpinner1 = (Spinner) findViewById(R.id.spinner1);

        mySpinner2 = (Spinner) findViewById(R.id.spinner2);
        mySpinner3 = (Spinner) findViewById(R.id.spinner_bi);

        text = (TextView) findViewById(R.id.textview_first);
        tvDate_ = (TextView) findViewById(R.id.tvDate);

        btnSave__= (Button)findViewById(R.id.btnSave);
        btnSave__.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        Date currentDate = new Date();
        monday_finder = findPreviousMonday(currentDate);
        tvDate_.setText(monday_finder);

        loadData();
        btnDecreaseDate_= (Button)findViewById(R.id.btnDecreaseDate);
        btnDecreaseDate_.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Decrease();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnIncreaseDate_= (Button)findViewById(R.id.btnIncreaseDate);
        btnIncreaseDate_.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Increase();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void Decrease() throws ParseException{
        // Получаем текущее значение даты из TextView
        String curr_val = (String) tvDate_.getText();

        // Форматируем строку с датой в Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date currentDate = sdf.parse(curr_val);

        // Создаем объект Calendar и устанавливаем текущую дату
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Вычитаем одну неделю из даты
        calendar.add(Calendar.WEEK_OF_YEAR, -1);

        // Получаем новую дату
        Date decreasedDate = calendar.getTime();

        // Форматируем новую дату обратно в строку
        String decreasedDateString = sdf.format(decreasedDate);
        tvDate_.setText(decreasedDateString);

    }

    private void Increase() throws ParseException{
        // Получаем текущее значение даты из TextView
        String curr_val = (String) tvDate_.getText();

        // Форматируем строку с датой в Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date currentDate = sdf.parse(curr_val);

        // Создаем объект Calendar и устанавливаем текущую дату
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.add(Calendar.WEEK_OF_YEAR, +1);

        // Получаем новую дату
        Date decreasedDate = calendar.getTime();

        // Форматируем новую дату обратно в строку
        String decreasedDateString = sdf.format(decreasedDate);
        tvDate_.setText(decreasedDateString);

    }
    private void saveData() {
        String curr_tv_text = (String) tvDate_.getText();
        Log.d("MyActivity", curr_tv_text);
        // Получите выбранные значения из выпадающих списков
        String selectedValueSpinner1 = mySpinner1.getSelectedItem().toString();
        String selectedValueSpinner2 = mySpinner2.getSelectedItem().toString();
        String selectedValueSpinner3 = mySpinner3.getSelectedItem().toString();

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("mainForFA");
        PyObject result = pyObject.callAttr("update_disciplines", selectedValueSpinner1, selectedValueSpinner2);
        // получение результата из Python в Java
//        String resultInJava = result.toJava(String.class);
//        Log.d("MyActivity", resultInJava);
        PyObject connection = pyObject.callAttr("update_schedule", selectedValueSpinner3, curr_tv_text);
        if(connection == null){
            text.setText("Расписание не найдено");
        }
        else {
            text.setText(connection.toString());
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("spinner1", selectedValueSpinner1);
            editor.putString("spinner2", selectedValueSpinner2);
            editor.putString("spinner3", selectedValueSpinner3);
            editor.apply();
        }



    }
    private void loadData() {
        // Загрузка данных из файла
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String savedValueSpinner1 = sharedPreferences.getString("spinner1", "");
        String savedValueSpinner2 = sharedPreferences.getString("spinner2", "");
        String savedValueSpinner3 = sharedPreferences.getString("spinner3", "");

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

    public static String findPreviousMonday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Получаем номер дня недели (воскресенье - 1, понедельник - 2, ..., суббота - 7)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Вычисляем разницу между текущим днем и понедельником
        int daysToSubtract = dayOfWeek - Calendar.MONDAY;
        if (daysToSubtract < 0) {
            // Если текущий день недели - воскресенье, вычитаем 6 дней
            daysToSubtract = 6;
        }

        // Вычитаем разницу, чтобы получить предыдущий понедельник
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        // Форматируем результат в строку
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


}