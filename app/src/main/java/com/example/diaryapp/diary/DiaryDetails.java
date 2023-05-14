package com.example.diaryapp.diary;

import android.content.Intent;
import android.os.Bundle;

import com.example.diaryapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DiaryDetails extends AppCompatActivity {
    Intent data;
    //displays single diary entry
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        TextView content = findViewById(R.id.diaryDetailsContent);
        TextView title = findViewById(R.id.diaryDetailsTitle);
        content.setMovementMethod(new ScrollingMovementMethod());

        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), EditEntry.class);
                i.putExtra("title",data.getStringExtra("title"));
                i.putExtra("content",data.getStringExtra("content"));
                i.putExtra("entryId",data.getStringExtra("entryId"));
                startActivity(i);
            }
        });
    }
    //sends back to home screen
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}