package com.example.listycity_lab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityAdapter extends ArrayAdapter<City> {

    public CityAdapter(Context context, ArrayList<City> cities) {
        super(context, 0, cities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        City city = getItem(position);

        TextView cityNameTextView = view.findViewById(R.id.city_name_textView);
        TextView provinceNameTextView = view.findViewById(R.id.province_name_textView);

        cityNameTextView.setText(city.getCityName());
        provinceNameTextView.setText(city.getProvinceName());

        return view;
    }
}
