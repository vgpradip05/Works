package com.example.contactpicker;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgpradip05 on 21/4/18.
 */

public class CustomAdapterContacts extends BaseAdapter {
    private List<ContactDetails> contacts,selectedContacts;
    private Activity context;
    private List<ContactDetails> filtredListContact;
    private ItemFilter mFilter = new ItemFilter();
    private String filteredString = "";
    private CustomAdapterContacts(List<ContactDetails> contacts, List<ContactDetails> selectedContacts, ContactPickerActivity context) {
        this.contacts = contacts;
        this.selectedContacts = selectedContacts;
        this.context = context;
        this.filtredListContact = contacts;
    }
    @Override
    public int getCount() {
        return contacts.size();
    }
    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater anInflater = context.getLayoutInflater();
        View mViewGp = anInflater.inflate(R.layout.item_layout, null);
        TextView tvName =  mViewGp.findViewById(R.id.tv_name);
        TextView tvNumber =  mViewGp.findViewById(R.id.tv_number);
        TextView tvTitle = mViewGp.findViewById(R.id.tv_contact_title);
        ImageView ivCheck = mViewGp.findViewById(R.id.iv_check);

        if(selectedContacts.contains(contacts.get(position)))
            ivCheck.setVisibility(View.VISIBLE);
        else
            ivCheck.setVisibility(View.GONE);
        SpannableString cName = new SpannableString(contacts.get(position).getContactName());
        String name = contacts.get(position).getContactName();
        if(filteredString.length()>0) {
            cName.setSpan(new BackgroundColorSpan(Color.YELLOW), name.toLowerCase().indexOf(filteredString), name.toLowerCase().indexOf(filteredString)+filteredString.length(), 0);
            tvName.setText(cName);
        }else{
            tvName.setText(name);
        }
        if(contacts.get(position).getPhoneNumbers()!=null && contacts.get(position).getPhoneNumbers().size()>0)
            tvNumber.setText(contacts.get(position).getPhoneNumbers().get(0).getPhoneNumber());
        tvTitle.setText(String.valueOf(contacts.get(position).getContactName().charAt(0)));

        mViewGp.setTag(contacts.get(position));
        return mViewGp;
    }
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            filteredString = filterString;
            FilterResults results = new FilterResults();
            final List<ContactDetails> list = filtredListContact;
            int count = list.size();
            final List<ContactDetails> newList = new ArrayList<>();
            String filterableString ;
            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getContactName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    newList.add(list.get(i));
                }
            }
            results.values = newList;
            results.count = newList.size();
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts = (ArrayList<ContactDetails>) results.values;
            notifyDataSetChanged();
        }
    }

}


