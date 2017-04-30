package com.nasaspaceapps.codebird.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nasaspaceapps.codebird.R;
import com.nasaspaceapps.codebird.interfaces.VolleyCallback;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.android.volley.VolleyLog.TAG;


/**
 * Created by Dubey's on 25-06-2016.
 */
public class DataAdpater extends RecyclerView.Adapter<DataAdpater.ViewHolder> {

    private Context context;
    private RequestQueue distanceRequest, getRating;
    List<String> sights = new ArrayList<String>();
    private static MyClickListener myClickListener;


    public DataAdpater(Context context, List<String> sightsss) {
        this.context = context;
        this.sights = sightsss;
        distanceRequest = Volley.newRequestQueue(context);
        Log.e("Sights", sightsss.toString());

    }

    @Override
    public DataAdpater.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_top, viewGroup, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView distance, address;

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);


        }

        public ViewHolder(View view) {
            super(view);

            distance = (TextView) view.findViewById(R.id.distance);
            address = (TextView) view.findViewById(R.id.address);

            view.setOnClickListener(this);


        }
    }


    private String Addresss(Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String add = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            add = addresses.get(0).getAddressLine(0);
            add = add + " " + addresses.get(0).getLocality();
            add = add + " " + addresses.get(0).getAdminArea();
            add = add + " " + addresses.get(0).getCountryName();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return add;

    }

    @Override
    public void onBindViewHolder(final DataAdpater.ViewHolder viewHolder, int i) {
        try {
            JSONObject jsonObject = new JSONObject(sights.get(0));
            Log.e("Object", jsonObject.toString());
            Double lati = Double.parseDouble(jsonObject.getString("latitude"));
            Double longitude = Double.parseDouble(jsonObject.getString("longitude"));
            final String address = Addresss(lati, longitude);
            sendSecondRequest(jsonObject.getString("latitude"), jsonObject.getString("longitude"), new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    viewHolder.distance.setText(result);
                    viewHolder.address.setText(address);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount() {
        return sights.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }

    public void sendSecondRequest(String latitude, String logitude, final VolleyCallback callback) {
        final String[] dist = {""};
        final String distanceUrl = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=" + Prefs.getString("latitude", "") + "," + Prefs.getString("longitude", "") + "&destinations=" + latitude + "," + logitude + "&mode=driving&language=en-EN&sensor=false";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                distanceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");
                    dist[0] = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text").replace(" km", "");
                    callback.onSuccess(dist[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        distanceRequest.add(strReq);

    }


}
