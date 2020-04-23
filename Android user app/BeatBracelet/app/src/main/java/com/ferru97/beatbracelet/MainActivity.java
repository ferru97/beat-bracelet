package com.ferru97.beatbracelet;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.ferru97.beatbracelet.data.Bracelet;
import com.ferru97.beatbracelet.data.BraceletAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AddBracieletDialog addDialog = new AddBracieletDialog(getLayoutInflater().inflate(R.layout.add_bracelet_dialod, null));
        addDialog.show(getSupportFragmentManager(),"add_dialog");

        ListView listView = (ListView)findViewById(R.id.bracelets_list);
        List<Bracelet> list = new LinkedList<>();
        list.add(new Bracelet("ID 1","NAME 1","LAST 2"));
        list.add(new Bracelet("ID 2","NAME 2","LAST 2"));
        BraceletAdapter adapter = new BraceletAdapter(this, R.layout.bracelet_element, list);
        listView.setAdapter(adapter);
    }

}
