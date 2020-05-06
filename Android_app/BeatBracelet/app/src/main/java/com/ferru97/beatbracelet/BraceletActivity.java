package com.ferru97.beatbracelet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.ferru97.beatbracelet.data.API;
import com.ferru97.beatbracelet.utils.HTTPRequest;
import com.ferru97.beatbracelet.utils.HTTPResponseHandler;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BraceletActivity extends AppCompatActivity implements HTTPResponseHandler {
    private EditText brc_name;
    private EditText brc_interval;
    private EditText monitor_date;
    private String bid;
    private LineChart mChart;
    private ListView alerts;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> alertList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracelet);

        brc_name = (EditText) findViewById(R.id.brc_name);
        brc_interval = (EditText) findViewById(R.id.brc_inter);
        monitor_date = (EditText) findViewById(R.id.monitor_date);

        alertList.add("3");
        alertList.add("1");

        alerts = (ListView) findViewById(R.id.alerts_list);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alertList );
        alerts.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        bid = intent.getStringExtra("bid");
        getBrcInfo("");

        mChart = findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);



    }

    private void getBrcInfo(String filter){
        Map<String, String> params = new HashMap<String, String>();
        params.put("bid", bid);
        params.put("filter", filter);
        HTTPRequest.POST_Request("get_brcInfo",this, API.get_braceletInfo ,(HashMap<String, String>) params,this);
    }


    public void updateInfo(View v){
        Map<String, String> params = new HashMap<String, String>();
        params.put("bid", bid);
        params.put("name", brc_name.getText().toString());
        params.put("interval", brc_interval.getText().toString());
        HTTPRequest.POST_Request("set_brcInfo",this, API.set_braceletInfo ,(HashMap<String, String>) params,this);

    }

    public void updateMonitorValues(View v){

        String date = monitor_date.getText().toString();
        if (!date.matches("(([0-9]{4}-[0-9]{2})-([0-9]{2}))")){
            alertMsg("Error", "Incorrect Date Format");
            monitor_date.setText("");
            return;
        }
        getBrcInfo(date);

    }

    @Override
    public void handleResponse(String type, String response) {
        if(type.equals("get_brcInfo")){
            try{
                Log.d("BrcINFO",response);
                JSONObject res = new JSONObject(response);
                if(res.get("res").toString().equals("ok")){
                    brc_name.setText(res.get("name").toString());
                    brc_interval.setText(res.get("intrval").toString());

                    JSONArray array = new JSONArray(res.get("monitors").toString());

                    ArrayList<Entry> entries = new ArrayList<>();
                    String[] months = new String[array.length()];

                    JSONObject temp;
                    for(int i=0; i<array.length(); i++){
                        temp = new JSONObject(array.get(i).toString());
                        Date date=new Date(Long.parseLong(temp.get("timestamp").toString()));
                        entries.add(new Entry(i, Integer.parseInt(temp.get("value").toString())));
                        months[i] = date.toString();
                    }

                   if(entries.size()==0){
                       entries.add(new Entry(0,0));
                       months = new String[]{"."};
                   }
                    plotLineChart(entries,months);


                    alertList.clear();
                    array = new JSONArray(res.get("alerts").toString());
                    for(int i=0; i<array.length(); i++){
                        alertList.add(array.get(i).toString());
                        Log.d("alert",alertList.get(i));
                    }

                    arrayAdapter.notifyDataSetChanged();

                }
            }catch (JSONException e){
                Log.e("Json error",e.toString());}
        }

        if(type.equals("set_brcInfo")){
            try{
                JSONObject res = new JSONObject(response);
                if(res.get("res").toString().equals("ok"))
                    alertMsg("Success!","Device's info updated");
                else
                    alertMsg("Error","Something went wrong...");

            }catch (JSONException e){
                Log.e("Json error",e.toString());}
        }
    }


    private void alertMsg(String title, String msg){
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(BraceletActivity.this).create();
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

    private void plotLineChart(ArrayList<Entry> entries,final String[] months){
        /*mChart.invalidate();
        mChart.clear();
        LineDataSet set1;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Beat Rate");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    Date date=new Date((long)value);
                    return date.toString();
                }
            });
        }*/

        LineDataSet dataSet = new LineDataSet(entries, "Customized values");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //****
        // Controlling X axis
        XAxis xAxis = mChart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        //***
        // Controlling right side of y axis
        YAxis yAxisRight = mChart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        LineData data = new LineData(dataSet);
        mChart.setData(data);
        mChart.animateX(2500);
        //refresh
        mChart.invalidate();
    }
}
