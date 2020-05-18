package com.ferru97.beatbracelet;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements HTTPResponseHandler, MqttCallback {
    ListView listView ;
    List<Bracelet> list = new LinkedList<>();
    MqttAndroidClient client;

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

        FloatingActionButton fabInfo = findViewById(R.id.aboutFab);
        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertMsg("About","text");
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
                goBraceletPage(item.getId());
            }
        });

        client = new MqttAndroidClient(this.getApplicationContext(), API.broker_url, API.client_id);
        client.setCallback(this);
        connect2MQTTbroker();

    }


    private void goBraceletPage(String bid){
        Intent intent = new Intent(this, BraceletActivity.class);
        intent.putExtra("bid",bid);
        startActivity(intent);
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
                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
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


    private void connect2MQTTbroker(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("*"+API.client_id);
        options.setPassword(API.client_psw.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MQTT", "onSuccess");

                    for(int i=0; i<list.size(); i++){
                        sub2Alert(list.get(i).getId()+API.mqtt_subAlert);
                        sub2Alert(list.get(i).getId()+API.mqtt_hbAlert);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MQTT", "onFailure");
                    connect2MQTTbroker();
                }

            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sub2Alert(final String topic){
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("MQTT SUB OK", topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d("MQTT SUB ERRt", topic);
                    sub2Alert(topic);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        connect2MQTTbroker();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String[] tokens = topic.split("/");
        if(tokens[1].equals("alert")){
            Log.d(tokens[1],message.toString());
            String name = "";
            for(int i=0; i<list.size() && name.equals("");i++){
                if(list.get(i).getId().equals(message.toString()))
                    name = list.get(i).getName();
            }
            notifyAlert("New button alert sent from "+name+" !",message.toString());
        }
        if(tokens[1].equals("alert_hb")){
            Log.d(tokens[1],message.toString());
            String name = "";
            for(int i=0; i<list.size() && name.equals("");i++){
                if(list.get(i).getId().equals(tokens[0]))
                    name = list.get(i).getName();
            }
            notifyAlert("Abnormal measurement from "+name+": "+message.toString()+" BPM","");
        }
    }

    public void notifyAlert(String title, String bid){
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("01",
                    "ALERT_CH",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.alert_button_beatbracelet) // notification icon
                .setContentTitle(title) // title for notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.alert_button_beatbracelet))
                //.setContentText()// message for notification
                .setAutoCancel(true); // clear notification after click

        Intent intent = new Intent(this, Bracelet.class);
        intent.putExtra("bid",bid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
