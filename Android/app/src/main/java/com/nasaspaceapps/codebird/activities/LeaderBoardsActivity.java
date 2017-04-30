package com.nasaspaceapps.codebird.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.nasaspaceapps.codebird.R;
import com.nasaspaceapps.codebird.adapters.DataAdpater_;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> data_ = new ArrayList<String>();
    DataAdpater_ adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_boards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        data_.add("Abhishek Dubey");
        data_.add("Anurag Kumar");
        data_.add("Rahul Maurya");

        adapter = new DataAdpater_(getApplicationContext(), data_);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new DataAdpater_.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.e("Clicked", "Clicked");
            }
        });

    }
}
