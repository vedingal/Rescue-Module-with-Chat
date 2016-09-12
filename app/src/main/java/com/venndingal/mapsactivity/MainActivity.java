package com.venndingal.mapsactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_inNeed, btn_rescuer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_inNeed = (Button) findViewById(R.id.btn_inNeed);
        btn_rescuer = (Button) findViewById(R.id.btn_rescuer);

        btn_inNeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(in);
            }
        });

        btn_rescuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), Main1.class);
                in.putExtra("userName", "Rescuer");
                startActivity(in);
            }
        });
    }
}
