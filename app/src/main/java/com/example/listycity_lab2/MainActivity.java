package com.example.listycity_lab2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView cityList;

    Button addButton;
    Button deleteButton;

    EditText newCityEditText;
    EditText newProvinceEditText;

    CityAdapter cityAdapter;
    ArrayList<City> dataList;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cityList = findViewById(R.id.city_list);
        dataList = new ArrayList<>();
        dataList.add(new City("Karachi", "Sindh"));
        dataList.add(new City("Lahore", "Punjab"));
        dataList.add(new City("Islamabad", "Capital Territory"));

        cityAdapter = new CityAdapter(this, dataList);

        cityList.setAdapter(cityAdapter);

        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);


        addButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog, null);

            newCityEditText = dialogView.findViewById(R.id.new_city_dialog_editText);
            newProvinceEditText = dialogView.findViewById(R.id.new_province_dialog_editText);
            Button confirmButton = dialogView.findViewById(R.id.confirm_add_button);

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.show();

            confirmButton.setOnClickListener(cv -> {
               String newCity = newCityEditText.getText().toString();
               String newProvince = newProvinceEditText.getText().toString();

               if (!newCity.isEmpty() && !newProvince.isEmpty()){
                   dataList.add(new City(newCity, newProvince));
                   cityAdapter.notifyDataSetChanged();
               }

               dialog.dismiss();

            });
        });

        cityList.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.edit_alert_dialog, null);

            EditText editCityEditText = dialogView.findViewById(R.id.edit_city_dialog_editText);
            EditText editProvinceEditText = dialogView.findViewById(R.id.edit_province_dialog_editText);
            Button confirmEditButton = dialogView.findViewById(R.id.confirm_edit_button);

            editCityEditText.setText(dataList.get(selectedPosition).getCityName());
            editProvinceEditText.setText(dataList.get(selectedPosition).getProvinceName());

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.show();

            confirmEditButton.setOnClickListener(cv -> {
                String newCity = editCityEditText.getText().toString();
                String newProvince = editProvinceEditText.getText().toString();

                if (!newCity.isEmpty() && !newProvince.isEmpty()){
                    dataList.get(selectedPosition).setCityName(newCity);
                    dataList.get(selectedPosition).setProvinceName(newProvince);
                    cityAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            });
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                dataList.remove(selectedPosition);
                cityAdapter.notifyDataSetChanged();
                selectedPosition = -1; // Reset selection
            }
        });
    }
}