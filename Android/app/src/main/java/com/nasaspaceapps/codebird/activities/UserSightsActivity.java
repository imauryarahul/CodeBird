package com.nasaspaceapps.codebird.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nasaspaceapps.codebird.R;
import com.nasaspaceapps.codebird.adapters.DataAdpater;
import com.nasaspaceapps.codebird.interfaces.VolleyCallback;
import com.nasaspaceapps.codebird.utils.UserRegistration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserSightsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> data_ = new ArrayList<String>();
    DataAdpater adapter;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sights);
        UserRegistration getShopResults = new UserRegistration(getApplicationContext());
        getShopResults.getSightData(new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("user_details");
                    String data = jsonArray.getJSONObject(0).getString("sighting_data");
                    data = data.replaceAll("(?<!https:)\\/\\/", "/");
                    JSONArray jsonArray1 = new JSONArray(data);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        data_.add(jsonArray1.getJSONObject(i).toString());

                    }
                    adapter = new DataAdpater(getApplicationContext(), data_);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new DataAdpater.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            Log.e("Clicked", "Clicked");
                        }
                    });
                    spinner.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        setContentView(R.layout.activity_user_sights);
        spinner = (ProgressBar) findViewById(R.id.progress_bar);
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}