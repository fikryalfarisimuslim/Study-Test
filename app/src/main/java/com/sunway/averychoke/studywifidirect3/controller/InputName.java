package com.sunway.averychoke.studywifidirect3.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.util.PreferenceHelper;

public class InputName extends AppCompatActivity {
    private Button btnSubmit;
    private EditText nim, name;
    PreferenceHelper mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name);

        mPref = new PreferenceHelper(InputName.this);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        nim = (EditText) findViewById(R.id.input_nim);
        name = (EditText) findViewById(R.id.input_nama);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( TextUtils.isEmpty(nim.getText())){
                    nim.setError( "NIM harus di isi!" );
                }else if(TextUtils.isEmpty(name.getText())){
                    name.setError( "Nama harus di isi!" );
                }else {
                    mPref.setNIM(nim.getText().toString());
                    mPref.setName(name.getText().toString());
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            }
        });

        //Button btn_submit = findViewById(R.id.btn_submit);

    }
}
