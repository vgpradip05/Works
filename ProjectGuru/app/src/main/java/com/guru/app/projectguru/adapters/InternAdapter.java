package com.guru.app.projectguru.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guru.app.projectguru.R;
import com.guru.app.projectguru.models.DataModel;
import com.guru.app.projectguru.models.Identity;

import java.util.HashMap;
import java.util.List;


public class InternAdapter extends RecyclerView.Adapter<InternAdapter.MyViewHolder> {

    private List<DataModel> internList;
    private HashMap<String,Identity> reqEmails;
    Activity context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, city, organization;
        public View row;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            organization = view.findViewById(R.id.organization);
            city = view.findViewById(R.id.city);
            row = view;
        }
    }


    public InternAdapter(List<DataModel> internList, HashMap<String,Identity> reqEmails,Activity context) {
        this.internList = internList;
        this.reqEmails = reqEmails;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.intern_list_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel dataModel = internList.get(position);
        if(reqEmails.containsKey(dataModel.geteMail().split("\\.")[0])) {
            internList.get(position).setRequested(true);
            holder.row.setBackgroundColor(context.getResources().getColor(R.color.primary_light));

        }
            holder.name.setText(dataModel.getName());
            holder.organization.setText(dataModel.getOrganization());
            holder.city.setText(dataModel.getCity());
    }

    @Override
    public int getItemCount() {
        return internList.size();
    }
}