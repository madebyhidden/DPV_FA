package com.example.timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_dvp_pi.R;

import java.util.List;
import java.util.Objects;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder> {

    // Создал класс для хранения данных об одном элементе
    public static class TimetableItem {

        private String stream;
        private String group;
        private String prepod_name;
        private String subject; // дисциплина
        private String startTime; // начало времени
        private String endTime; // конец времени
        private String room; // аудитория
        private String type; // тип дисциплины

        // Создал конструктор для инициализации полей
        public TimetableItem(String subject, String startTime, String endTime, String room, String type, String prepod_name, String group, String stream) {
            this.subject = subject;
            this.startTime = startTime;
            this.endTime = endTime;
            this.room = room;
            this.type = type;
            this.prepod_name = prepod_name;
            this.group = group;
            this.stream = stream;
        }

        // Создал геттеры для получения значений полей
        public String getSubject() {
            return subject;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getRoom() {
            return room;
        }

        public String getType() {
            return type;
        }

        public String getPrepod_name() {
            return prepod_name;
        }

        public String getGroup() {
            return group;
        }

        public String getStream() {
            return stream;
        }
    }

    // Создал список для хранения данных обо всех элементах
    private List<TimetableItem> timetableItems;

    // Создал конструктор для инициализации списка
    public TimetableAdapter(List<TimetableItem> timetableItems) {

        this.timetableItems = timetableItems;
    }

    // Создал класс для хранения ссылок на элементы разметки
    public static class TimetableViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject; // TextView для дисциплины
        private TextView tvTime; // TextView для начала и конца времени
        private TextView tvRoom; // TextView для аудитории
        private TextView tvType; // TextView для типа дисциплины
        private TextView tvPrepod; // TextView для типа дисциплины

        private TextView tvGroup;

        // Создал конструктор для инициализации ссылок
        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.item_timetable_tvSubject);
            tvTime = itemView.findViewById(R.id.item_timetable_tvTime);
            tvRoom = itemView.findViewById(R.id.item_timetable_tvRoom);
            tvType = itemView.findViewById(R.id.item_timetable_tvType);
            tvPrepod = itemView.findViewById(R.id.item_timetable_tvPrepod);
            tvGroup = itemView.findViewById(R.id.item_timetable_tvGroup);
        }
    }


    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Получил ссылку на разметку элемента
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        // Создал и вернул объект TimetableViewHolder с этой ссылкой
        return new TimetableViewHolder(view);
    }

    // Переопределил метод для связи данных с элементом
    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {

        // Получил данные об одном элементе из списка по позиции
        TimetableItem item = timetableItems.get(position);
        // Установил текст для каждого TextView из данных
        if (item.getSubject().equals("Сегодня выходной")) {
            holder.tvSubject.setText(item.getSubject());
        } else {
            holder.tvSubject.setText(item.getSubject());
            holder.tvTime.setText(item.getStartTime() + " - " + item.getEndTime());
            holder.tvRoom.setText(item.getRoom());
            if (item.getType().equals("Практические (семинарские) занятия")) {
                holder.tvType.setText("Семинар");
            } else {
                holder.tvType.setText(item.getType());
            }
            holder.tvPrepod.setText(item.getPrepod_name());
            if (Objects.equals(item.getGroup(), "None")) {
                holder.tvGroup.setText("Поток");
            } else {
                if (item.getGroup().endsWith("(1 п/гр)")) {
                    holder.tvGroup.setText("Подгруппа: 1");
                } else if (item.getGroup().endsWith("(2 п/гр)")) {
                    holder.tvGroup.setText("Подгруппа: 2");
                } else if (item.getGroup().endsWith("(3 п/гр)")) {
                    holder.tvGroup.setText("Подгруппа: 3");
                } else if (item.getGroup().endsWith("(4 п/гр)")) {
                    holder.tvGroup.setText("Подгруппа: 4");
                } else if (item.getSubject().startsWith("Иностранный язык")) {
                    holder.tvGroup.setText("Персонально");
                } else {
                    holder.tvGroup.setText(String.format("Группа: %s", item.getGroup()));
                }

            }
        }

    }

    // Переопределил метод для получения количества элементов в списке
    @Override
    public int getItemCount() {
        return timetableItems.size();
    }
}