package com.nasaspaceapps.codebird.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nasaspaceapps.codebird.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dubey's on 25-06-2016.
 */
public class DataAdpater_ extends RecyclerView.Adapter<DataAdpater_.ViewHolder> {

    private Context context;
    List<String> sights = new ArrayList<String>();
    private static MyClickListener myClickListener;


    public DataAdpater_(Context context, List<String> sightsss) {
        this.context = context;
        this.sights = sightsss;

    }

    @Override
    public DataAdpater_.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_top_, viewGroup, false);
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


    @Override
    public void onBindViewHolder(final DataAdpater_.ViewHolder viewHolder, int i) {
        viewHolder.address.setText(sights.get(i));
        viewHolder.distance.setText(7 - i + "");


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


}
