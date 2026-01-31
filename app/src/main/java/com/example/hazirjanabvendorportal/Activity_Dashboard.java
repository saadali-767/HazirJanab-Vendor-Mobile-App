package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Activity_Dashboard extends AppCompatActivity {

    Spinner CategoryItems;
    RecyclerView servicesRecyclerView;
    Adapter_ServiceRegistration serviceAdapter;
    List<Class_ServiceRegistration> services = new ArrayList<>();
    List<Class_ServiceRegistration> allServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        CategoryItems = findViewById(R.id.CategoryItems);
//        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);

//        populateAllServices();

//        serviceAdapter = new Adapter_ServiceRegistration(services);
//        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        servicesRecyclerView.setAdapter(serviceAdapter);

        String[] CategoryList = {"Electrician", "Plumber", "Carpenter", "Mechanic"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CategoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CategoryItems.setAdapter(spinnerAdapter);
        CategoryItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                if(Class_SingletonVendor.getInstance().getId()==-1){
                    Class_SingletonVendor.getInstance().setCategory(selectedCategory);
                }
//                Toast.makeText(Activity_Dashboard.this, "category: "+selectedCategory, Toast.LENGTH_SHORT).show();
//                filterServicesByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                services.clear();
                serviceAdapter.notifyDataSetChanged();
            }
        });

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Dashboard.this, Activity_FaceRegistration.class);
                startActivity(intent);
            }
        });
        
    }

    private void filterServicesByCategory(String selectedCategory) {
        services.clear();
        //Toast allservices
//        Toast.makeText(this, "allservices: "+allServices.size(), Toast.LENGTH_SHORT).show();
        for (Class_ServiceRegistration service : allServices) {
            if (service.getCategory().equals(selectedCategory)) {
//                Toast.makeText(this, "service: "+service.getName(), Toast.LENGTH_SHORT).show();
                services.add(service);
            }
        }
        serviceAdapter.notifyDataSetChanged();
    }

    private void populateAllServices() {
        allServices.add(new Class_ServiceRegistration("Service 1", "Electrician", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 2", "Electrician", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 3", "Electrician", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 4", "Electrician", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 1", "Plumber", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 2", "Plumber", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 3", "Plumber", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 4", "Plumber", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 1", "Carpenter", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 2", "Carpenter", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 3", "Carpenter", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 4", "Carpenter", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 1", "Mechanic", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 2", "Mechanic", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 3", "Mechanic", "Emergency", false));
        allServices.add(new Class_ServiceRegistration("Service 4", "Mechanic", "Emergency", false));
    }
}