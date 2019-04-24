package com.grafixartist.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    Button submitbtn;
    EditText fname,lname,email;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences=getSharedPreferences(Content.PREF_NAME,MODE_PRIVATE);
        initialization();
        if(isValiduser()){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public Boolean isValiduser(){
        String firsname=sharedPreferences.getString(Content.KEY_FIRSTNAME,Content.INVALID_VALUE);
        if(!firsname.contentEquals(Content.INVALID_VALUE))
            return true;
        return false;
    }

    public void initialization(){
        fname= (EditText) findViewById(R.id.fname);
        lname= (EditText) findViewById(R.id.lname);
        email= (EditText) findViewById(R.id.email);

        submitbtn= (Button) findViewById(R.id.submitbtn);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname=fname.getText().toString();
                String lastname=lname.getText().toString();
                String useremail=email.getText().toString();



                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(Content.KEY_FIRSTNAME,firstname);
                editor.putString(Content.KEY_LASTNAME,lastname);
                editor.putString(Content.KEY_EMAIL,useremail);

                editor.commit();

                if(firstname.isEmpty() || lastname.isEmpty() || useremail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Fill out all the required fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}
