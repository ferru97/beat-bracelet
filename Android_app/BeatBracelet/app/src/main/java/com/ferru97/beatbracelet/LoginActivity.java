package com.ferru97.beatbracelet;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.ferru97.beatbracelet.data.API;
import com.ferru97.beatbracelet.utils.HTTPRequest;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import com.ferru97.beatbracelet.utils.HTTPResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements HTTPResponseHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goRegister(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void Login(View v){
        EditText email = (EditText) findViewById(R.id.email);
        EditText psw = (EditText) findViewById(R.id.psw);

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email.getText().toString());
        params.put("psw", psw.getText().toString());
        HTTPRequest.POST_Request("login",this, API.login, (HashMap<String, String>) params,this);

    }

    @Override
    public void handleResponse(String type, String response) {
        try{
            JSONObject res = new JSONObject(response);
            if(res.get("res").toString().equals("ok")){
                API.client_id = res.get("id").toString();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                faildLogin();
            }
            Log.d("Handle Response",res.get("res").toString());
        }catch (JSONException e){Log.e("Json error",e.toString());}

    }


    private void faildLogin(){
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Invalid credentials");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
