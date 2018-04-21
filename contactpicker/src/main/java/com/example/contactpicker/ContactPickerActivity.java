package com.example.contactpicker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 21/4/18.
 */

public class ContactPickerActivity extends AppCompatActivity implements View.OnClickListener {
    ContactDetails contactDetails;
    PhoneNumber phoneNumber;
    EmailId emailId;
    List<PhoneNumber> phoneNumbers;
    List<EmailId> emailIds;
    public static final int CONTACT_REQ_CODE = 2;
    List<ContactDetails> contacts = new ArrayList<>();
    List<ContactDetails> selectedContacts = new ArrayList<>();
    public ListView lvContacts;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private CustomEditText edtSeach;
    public CustomAdapterContacts customAdapterContacts;
    Toolbar toolbar;
    View view;
    LinearLayout llHScrollView;
    FloatingActionButton fabDone,fabClose;
    Animation fadeIn,fadeOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Contacts");
        checkPermissionAndProceed();
        lvContacts = (ListView)findViewById(R.id.lv_contacts);
        fabDone = (FloatingActionButton)findViewById(R.id.fab_done);
        fabClose = (FloatingActionButton)findViewById(R.id.fab_close);
        //fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        //fadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        customAdapterContacts = new CustomAdapterContacts(contacts,selectedContacts,ContactPickerActivity.this);
        lvContacts.setAdapter(customAdapterContacts);
        llHScrollView = (LinearLayout)findViewById(R.id.ll_h_scrollview);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selectedContacts.contains(view.getTag())){
                    llHScrollView.removeViewAt(selectedContacts.indexOf(view.getTag()));
                    refreshView();
                    selectedContacts.remove(view.getTag());
                    customAdapterContacts.notifyDataSetChanged();
                    checkSelctedContacts();
                }else{
                    selectedContacts.add((ContactDetails) view.getTag());
                    customAdapterContacts.notifyDataSetChanged();
                    checkSelctedContacts();
                    addContactChip((ContactDetails)view.getTag());
                }
            }
        });
        fabDone.setOnClickListener(this);
        fabClose.setOnClickListener(this);
    }
    private void addContactChip(ContactDetails contactDetails) {
        LayoutInflater anInflater = ContactPickerActivity.this.getLayoutInflater();

        view = anInflater.inflate(R.layout.contact_chip, null);
        view.setTag(contactDetails);
        TextView textViewTitle = (TextView) view.findViewById(R.id.tv_contact_title);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        textViewTitle.setText(String.valueOf(contactDetails.getContactName().charAt(0)));
        tvName.setText(contactDetails.getContactName());
        llHScrollView.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedContacts.remove(v.getTag());
                checkSelctedContacts();
                llHScrollView.removeViewInLayout(v);
                refreshView();
                customAdapterContacts.notifyDataSetChanged();
            }
        });

    }

    private void refreshView() {
        llHScrollView.setVisibility(View.GONE);
        llHScrollView.setVisibility(View.VISIBLE);
    }
    public void getContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                contactDetails = new ContactDetails();
                InputStream inputStream = null;

                if(id!=null){
                    inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ContactPickerActivity.this.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
                }
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    phoneNumbers = new ArrayList<>();
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String category = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        if (phoneNo.length() > 9) {
                            phoneNumber = new PhoneNumber();
                            phoneNumber.setPhoneNumber(phoneNo);
                            phoneNumber.setCategory(category);
                            phoneNumbers.add(phoneNumber);
                        }
                    }
                    pCur.close();
                }
                Cursor emailCur = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                emailIds = new ArrayList<>();
                while (emailCur.moveToNext()) {
                    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    emailId = new EmailId();
                    emailId.seteMailId(email);
                    emailId.setCategory(emailType);
                    emailIds.add(emailId);
                }
                emailCur.close();

                Cursor nNameCr = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID +" = ?", new String[]{id}, null);
                nNameCr.moveToNext();
                String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] params = new String[] {id, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE};
                Cursor nickname = ContactPickerActivity.this.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, params, null);
                String nickNameName = null;
                if(nickname.moveToNext()) {
                    nickNameName = nickname.getString(nickname.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                }
                contactDetails.setContactNickName(nickNameName);
                contactDetails.setPhoneNumbers(phoneNumbers);
                contactDetails.setEmailIds(emailIds);
                contactDetails.setContactName(name);
                //contactDetails.setContactPicture(inputStream);
                contacts.add(contactDetails);
            }
        }
        cursor.close();
    }

    private void checkPermissionAndProceed(){
        if (Build.VERSION.SDK_INT > 22) {

            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                ActivityCompat.requestPermissions(ContactPickerActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_REQ_CODE);
            }
        } else {
            getContacts();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACT_REQ_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();

                } else {
                    Toast.makeText(ContactPickerActivity.this, "Permission denied to read your Contact Details", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            handleMenuSearch();
        }
        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open
            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true);  //show the title in the action bar
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));
            isSearchOpened = false;
            doSearch("");
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            action.setDisplayShowTitleEnabled(false);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            View view = LayoutInflater
                    .from(action.getThemedContext())
                    .inflate(R.layout.layout_search, null);
            action.setCustomView(view, params);//add the custom view
            edtSeach = (CustomEditText) getSupportActionBar().getCustomView().findViewById(R.id.edtSearch);
            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    doSearch(edtSeach.getText().toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(edtSeach.getText().toString().trim());
                        return true;
                    }
                    return false;
                }
            });
            edtSeach.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close2, null));
            isSearchOpened = true;
        }
    }
    private void doSearch(String searchQuery) {
        ContactPickerActivity.this.customAdapterContacts.getFilter().filter(searchQuery);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onBackPressed() {
        if(edtSeach!=null) {
            if (edtSeach.isFocused()) {
                getSupportActionBar().setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
                getSupportActionBar().setDisplayShowTitleEnabled(true);  //show the title in the action bar
                View view = this.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));
                isSearchOpened = false;
                doSearch("");
            }
            else{
                super.onBackPressed();
                Toast.makeText(ContactPickerActivity.this,"Not Selected any contact",Toast.LENGTH_LONG).show();
            }
        }else {
            super.onBackPressed();
            Toast.makeText(ContactPickerActivity.this,"Not Selected any contact",Toast.LENGTH_LONG).show();
        }
    }
    void checkSelctedContacts(){
        if(selectedContacts.size()>0){
            fabClose.setVisibility(View.VISIBLE);
            fabDone.setVisibility(View.VISIBLE);
        }else{
            fabClose.setVisibility(View.GONE);
            fabDone.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.fab_done){
            Intent intent = getIntent();
            intent.putExtra("key", (Serializable) selectedContacts);
            setResult(RESULT_OK, intent);
            finish();
        }else if(v.getId()==R.id.fab_close){
            Toast.makeText(ContactPickerActivity.this,"Not Selected any contact",Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
