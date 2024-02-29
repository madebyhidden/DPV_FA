package com.example.timetable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.fa_dvp_pi.R;
import com.example.fa_dvp_pi.TimeTableActivity;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {


    // Создал класс для хранения данных об одном элементе
    public static class DateItem {
        private String date; // дата

        // Создал конструктор для инициализации полей
        public DateItem(String date) {
            this.date = date;

        }

        // Создал геттеры для получения значений полей
        public String getDate() {
            return date;
        }


    }
    private  SnapHelper snapHelper;

    // Создал список для хранения данных обо всех элементах
    private List<DateItem> dateItems;

    // Создал переменную для хранения позиции выбранного элемента
    private int selectedPosition;

    // Создал интерфейс для передачи данных о выбранном элементе в другой класс
    public interface OnDateSelectedListener {
        void onDateSelected(DateItem dateItem);
    }

    // Создал переменную для хранения ссылки на объект, который реализует этот интерфейс
    private OnDateSelectedListener onDateSelectedListener;

    // Создал конструктор для инициализации списка и слушателя
    public DateAdapter(List<DateItem> dateItems, OnDateSelectedListener onDateSelectedListener, int selectedPosition) {
        this.dateItems = dateItems;
        this.onDateSelectedListener = onDateSelectedListener;
        this.selectedPosition = selectedPosition;

    }

    // Перенесите код создания и присоединения SnapHelper из вашего активити в метод onAttachedToRecyclerView вашего адаптера
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // Создайте SnapHelper объект и присоедините его к вашему RecyclerView
//        snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView (recyclerView);
    }

    // Создал класс для хранения ссылок на элементы разметки
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate; // TextView для даты
        private TextView tvDay; // TextView для дня недели


        // Создал конструктор для инициализации ссылок
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.item_date_tvDate);

        }
    }
    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Получил ссылку на разметку элемента
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        // Создал и вернул объект DateViewHolder с этой ссылкой
        return new DateViewHolder(view);
    }

    // Переопределил метод для связи данных с элементом
    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, @SuppressLint("RecyclerView") int position) {



        // Получил данные об одном элементе из списка по позиции
        DateItem item = dateItems.get(position);


        // Установил текст для каждого TextView из данных
        holder.tvDate.setText(item.getDate());
        // Добавил слушатель для обработки нажатий на элемент

        CardView card = holder.itemView.findViewById(R.id.card_date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Изменил позицию выбранного элемента на текущую
                selectedPosition = position;
                // Уведомил адаптер об изменении данных
                notifyDataSetChanged();
                // Передал данные о выбранном элементе в другой класс через интерфейс
                onDateSelectedListener.onDateSelected(item);
            }
        });

    }

    // Переопределил метод для получения количества элементов в списке
    @Override
    public int getItemCount() {
        return dateItems.size();
    }
}
