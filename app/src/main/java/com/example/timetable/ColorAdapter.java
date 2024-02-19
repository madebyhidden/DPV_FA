package com.example.timetable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_dvp_pi.R;

import java.util.List;
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<DateAdapter.DateItem> items; // список данных об элементах
    private int selectedPosition; // позиция выбранного элемента
    private int selectedColor; // цвет фона для выбранного элемента
    private int defaultColor; // цвет фона для остальных элементов

    public ColorAdapter(List<DateAdapter.DateItem> items, int selectedColor, int defaultColor) {
        this.items = items;
        this.selectedPosition = -1; // изначально ни один элемент не выбран
        this.selectedColor = selectedColor;
        this.defaultColor = defaultColor;
    }

    // Метод для установки позиции выбранного элемента
    public void setSelectedPosition(int position) {
        // если позиция изменилась
        if (position != selectedPosition) {
            // запоминаем старую позицию
            int oldPosition = selectedPosition;
            // обновляем новую позицию
            selectedPosition = position;
            // уведомляем об изменении элементов по старой и новой позиции
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        }
        System.out.println(position + " " + selectedPosition);
    }

    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // получаем данные об элементе по позиции
        DateAdapter.DateItem item = items.get(position);

        // проверяем, совпадает ли позиция элемента с выбранной позицией
        if (position == selectedPosition) {

            // устанавливаем цвет фона для выбранного элемента
            holder.card.setCardBackgroundColor(selectedColor);
        } else {
            // устанавливаем цвет фона для остальных элементов
            holder.card.setCardBackgroundColor(defaultColor);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Класс для хранения ссылок на элементы вью
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;


        public ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_date);
        }
    }
}
                // adapter.setSelectedPosition(position);

