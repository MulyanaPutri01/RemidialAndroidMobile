package com.remidial.myblogphoto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    String email, password;
    LocalStorage localStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle("LOGIN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = new LocalStorage(LoginActivity.this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private void checkLogin(){
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            alertFail("Email and Password is Required.");
        }
        else {
            sendLogin();
        }
    }

    private void sendLogin() {
        JSONObject params = new JSONObject();
        try{

            params.put("email", email);
            params.put("password", password);

        }catch (JSONException e){
            e.printStackTrace();
        }
        String data = params.toString();
        String url = getString(R.string.api_server)+"/login";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(LoginActivity.this, url);
                http.setMethod("post");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    Integer code = http.getStatusCode();
                    @Override
                    public void run() {
                        if(code == 200){
                            try{
                                JSONObject response = new JSONObject((http.getResponse()));
                                String token = response.getString("token");
                                localStorage.setToken(token);
                                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                startActivity(intent);
                                finish();

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else if (code == 422){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(msg);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else if (code == 401){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(msg);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Error"+code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }).start();
    }

    private void alertFail(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Failed")
                .setIcon(R.drawable.ic_warning)
                .setMessage(s)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}