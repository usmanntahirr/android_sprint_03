package com.example.listycity_lab2;

import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView cityList;
    Button addButton;
    Button deleteButton;

    CityAdapter cityAdapter;
    ArrayList<City> dataList;

    private int selectedPosition = -1;
    private FirebaseFirestore db;
    private CollectionReference citiesRef;

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        // Initialize UI and Data
        cityList = findViewById(R.id.city_list);
        dataList = new ArrayList<>();
        cityAdapter = new CityAdapter(this, dataList);
        cityList.setAdapter(cityAdapter);

        // Firestore Listener - Automatically updates the list when Firestore changes
        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (value != null) {
                dataList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");
                    dataList.add(new City(name, province));
                }
                cityAdapter.notifyDataSetChanged();
            }
        });

        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);

        addButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog, null);

            EditText newCityEditText = dialogView.findViewById(R.id.new_city_dialog_editText);
            EditText newProvinceEditText = dialogView.findViewById(R.id.new_province_dialog_editText);
            Button confirmButton = dialogView.findViewById(R.id.confirm_add_button);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            confirmButton.setOnClickListener(cv -> {
                String newCityName = newCityEditText.getText().toString();
                String newProvinceName = newProvinceEditText.getText().toString();

                if (!newCityName.isEmpty() && !newProvinceName.isEmpty()) {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("name", newCityName);
                    data.put("province", newProvinceName);

                    // Add to Firestore (using city name as document ID)
                    citiesRef.document(newCityName)
                            .set(data)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "City added successfully"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error adding city", e));
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

            City selectedCity = dataList.get(selectedPosition);
            String oldCityName = selectedCity.getCityName();
            editCityEditText.setText(oldCityName);
            editProvinceEditText.setText(selectedCity.getProvinceName());

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            confirmEditButton.setOnClickListener(cv -> {
                String newCityName = editCityEditText.getText().toString();
                String newProvinceName = editProvinceEditText.getText().toString();

                if (!newCityName.isEmpty() && !newProvinceName.isEmpty()) {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("name", newCityName);
                    data.put("province", newProvinceName);

                    // If city name changed, delete old document and create new one
                    if (!newCityName.equals(oldCityName)) {
                        citiesRef.document(oldCityName).delete();
                    }
                    
                    citiesRef.document(newCityName).set(data);
                }
                dialog.dismiss();
            });
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                String cityName = dataList.get(selectedPosition).getCityName();
                // Delete from Firestore
                citiesRef.document(cityName)
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "City deleted successfully"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error deleting city", e));

                selectedPosition = -1; // Reset selection
            }
        });
    }
}