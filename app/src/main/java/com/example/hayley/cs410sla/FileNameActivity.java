package com.example.hayley.cs410sla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hayley on 3/14/18.
 */

public class FileNameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naming);

        final EditText title = (EditText) findViewById(R.id.title);
        final EditText pageNum = (EditText) findViewById(R.id.page_number);
        Button submit = (Button) findViewById(R.id.submit_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(title) || isEmpty(pageNum)) {
                    Toast.makeText(FileNameActivity.this, "Enter a title and a page number before submitting.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("Title", title.getText().toString());
                    Log.d("Page number", pageNum.getText().toString());
                    Intent intentExtras = new Intent(FileNameActivity.this, RecordActivity.class);
                    intentExtras.putExtra("title", title.getText().toString().trim());
                    intentExtras.putExtra("page number", pageNum.getText().toString().trim());
                    startActivity(intentExtras);
                }
            }
        });
    }

    private boolean isEmpty(EditText text) {
        return text.getText().toString().trim().length() == 0;
    }


}
