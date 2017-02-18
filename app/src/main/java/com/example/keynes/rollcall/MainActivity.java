package com.example.keynes.rollcall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button teacherBtn = (Button)findViewById(R.id.button_teacher);
        Button studentBtn = (Button)findViewById(R.id.button_student);

        teacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                startActivity(intent);
            }
        });

        studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        });
    }
}
