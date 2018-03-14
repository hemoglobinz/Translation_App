package com.example.hayley.cs410sla;

/**
 * Created by hayley on 3/14/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void recordFile(View view) {
        Intent intent = new Intent(this, FileNameActivity.class);
        startActivity(intent);
    }

    public void translateFile(View view) {
        Intent intent = new Intent(this, TranscribeFileActivity.class);
        startActivity(intent);
    }
}
