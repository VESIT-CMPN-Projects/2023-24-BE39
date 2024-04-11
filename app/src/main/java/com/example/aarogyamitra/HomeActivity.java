package com.example.aarogyamitra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void openBrainTumorActivity(View view) {
        Intent intent = new Intent(this, openBrainTumorActivity.class);
        startActivity(intent);
    }

    public void openSkinCancerActivity(View view) {
        Intent intent = new Intent(this, openSkinCancerActivity.class);
        startActivity(intent);
    }
    public void MapsActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openEducationCommunityActivity(View view) {
        Intent intent = new Intent(this, openEducationCommunityActivity.class);
        startActivity(intent);
    }
}