package com.ferru97.beatbracelet;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.ferru97.beatbracelet.data.API;
import com.ferru97.beatbracelet.data.Bracelet;
import com.ferru97.beatbracelet.data.BraceletAdapter;
import com.ferru97.beatbracelet.utils.HTTPRequest;
import com.ferru97.beatbracelet.utils.HTTPResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements HTTPResponseHandler {
    ListView listView ;
    List<Bracelet> list = new LinkedList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        final HTTPResponseHandler resHandler = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBracieletDialog addDialog = new AddBracieletDialog(getLayoutInflater().inflate(R.layout.add_bracelet_dialod, null), resHandler);
                addDialog.show(getSupportFragmentManager(),"add_dialog");

            }
        });


        listView = (ListView)findViewById(R.id.bracelets_list);
        final BraceletAdapter adapter = new BraceletAdapter(this, R.layout.bracelet_element, list);
        listView.setAdapter(adapter);
        populateListRequest();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bracelet item = (Bracelet) adapter.getItem(position);
                Log.d("IDB",item.getId());
            }
        });

    }

    private void populateListRequest(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", API.client_id);
        HTTPRequest.POST_Request("get_bracelets",this, API.get_bracelets ,(HashMap<String, String>) params,this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void handleResponse(String type,String response) {
        if(type.equals("add_bracelet")){
            try{
                JSONObject res = new JSONObject(response);
                if(res.get("res").toString().equals("ok")){
                    alertMsg("Success!","New device added");
                    populateListRequest();
                }else{
                    alertMsg("Error","Invalid credentials");
                }
            }catch (JSONException e){Log.e("Json error",e.toString());}
        }

        if(type.equals("get_bracelets")){
            try{
                JSONObject res = new JSONObject(response);
                if(res.get("res").toString().equals("ok")){
                    JSONObject temp = null;
                    JSONArray array = new JSONArray(res.get("list").toString());
                    if(array.length()>0){
                        list.clear();
                        for(int i=0; i<array.length(); i++){
                            temp = new JSONObject(array.get(i).toString());
                            list.add(new Bracelet(temp.get("_id").toString(),temp.get("name").toString(),temp.get("last_activity").toString()));
                        }
                        listView.deferNotifyDataSetChanged();
                    }
                }
            }catch (JSONException e){Log.e("Json error",e.toString());}
        }

    }

    private void alertMsg(String title, String msg){
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
