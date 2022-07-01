package com.remidial.myblogphoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvCreated;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvCreated = findViewById(R.id.tvCreated);
        btnLogout = findViewById(R.id.btnLogout);

        getUser();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    private void getUser(){
        String url = getString(R.string.api_server)+"/user";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http (UserActivity.this, url);
                http.setToken(true);
                http.send();

                runOnUiThread(new Runnable() {
                    Integer code = http.getStatusCode();
                    @Override
                    public void run() {
                        if(code == 200){
                            try{
                                JSONObject response = new JSONObject((http.getResponse()));
                                String name = response.getString("name");
                                String email = response.getString("email");
                                String create_at = response.getString("created_at");
                                tvName.setText(name);
                                tvEmail.setText(email);
                                tvCreated.setText(create_at);


                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        else {
                            Toast.makeText(UserActivity.this, "Error"+code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }).start();
    }
    private void logout(){
        String url = getString(R.string.api_server)+"/logout";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http (UserActivity.this, url);
                http.setMethod("post");
                http.setToken(true);
                http.send();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if(code == 200){
                            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }
                });


            }
        }).start();
    }
}