package com.guru.app.projectguru.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guru.app.projectguru.R;
import com.guru.app.projectguru.database.DataBaseUtils;
import com.guru.app.projectguru.database.FireBaseHelper;
import com.guru.app.projectguru.database.LocalPreferences;
import com.guru.app.projectguru.interfaces.LazyRequestCallback;
import com.guru.app.projectguru.interfaces.RequestCallback;
import com.guru.app.projectguru.models.DataModel;
import com.guru.app.projectguru.models.Identity;
import com.guru.app.projectguru.models.Master;
import com.guru.app.projectguru.utils.AnimateUtils;
import com.guru.app.projectguru.utils.Constants;

import java.util.HashMap;
import java.util.regex.Pattern;

public class InternRegistrationActivity extends AppCompatActivity {
    private EditText etName, etWorkingFor, etMobileNo, etEmail;
    private TextInputLayout tilName, tilWorkingFor, tilMobileNo, tilEmail;
    Spinner spCity, spProfessionalOf, spGraduatedFrom, spExcellenceIn;
    private Context context = InternRegistrationActivity.this;
    final static int PICK_PDF_CODE = 2342;
    final static String APPL_PDF = "application/pdf";
    final static String CHOOSER_TITLE = "Select Pdf";
    View progressOverlay;
    StorageReference mStorageReference;
    Master master;
    private TextView tvUploadedDoc;
    private Uri uri;
    LinearLayout lLName, lLWorkingFor, lLMobileNo, lLEmail, lLCity, lLProfOf, lLGradFrom, lLexcel, lLPdfButton;
    String uploadUrl;
    boolean mIsIntern = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupVars();
        uploadUrl = Constants.FireBasePaths.DATABASE_PATH_UPLOADS;
    }

    private void setupVars() {

        mStorageReference = FireBaseHelper.getFirebaseStorageReference();
        master = DataBaseUtils.getMasterData(context);
        progressOverlay = findViewById(R.id.progress_overlay_layout);
        Button btRegister;
        ImageButton btChooser;
        etName = findViewById(R.id.et_act_reg_name);
        tvUploadedDoc = findViewById(R.id.tv_uploaded);
        etWorkingFor = findViewById(R.id.et_act_reg_working_for);
        etMobileNo = findViewById(R.id.et_act_reg_mobile_no);
        etEmail = findViewById(R.id.et_act_reg_email);
        tilName = findViewById(R.id.input_layout_act_reg_name);
        tilEmail = findViewById(R.id.input_layout_act_reg_email);
        tilWorkingFor = findViewById(R.id.input_act_reg_working_for);
        tilMobileNo = findViewById(R.id.input_layout_act_reg_mobile_no);
        spCity = findViewById(R.id.sp_act_reg_city);
        spProfessionalOf = findViewById(R.id.sp_act_reg_professsional_of);
        spGraduatedFrom = findViewById(R.id.sp_act_reg_graduated_from);
        spExcellenceIn = findViewById(R.id.sp_act_reg_excellence_in);
        btRegister = findViewById(R.id.bt_act_reg_register);
        btChooser = findViewById(R.id.bt_act_reg_chooser);
        lLName = findViewById(R.id.ll_act_reg_name);
        lLCity = findViewById(R.id.ll_act_reg_city);
        lLWorkingFor = findViewById(R.id.ll_act_reg_cw);
        lLMobileNo = findViewById(R.id.ll_act_reg_mob);
        lLEmail = findViewById(R.id.ll_act_reg_email);
        lLProfOf = findViewById(R.id.ll_act_reg_prof);
        lLGradFrom = findViewById(R.id.ll_act_reg_grad);
        lLexcel = findViewById(R.id.ll_act_reg_exc);
        lLPdfButton = findViewById(R.id.ll_act_reg_ib);
        spExcellenceIn.setEnabled(false);
        spCity.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, master.getCities().toArray(new String[0])));
        spProfessionalOf.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, master.getSectors().keySet().toArray(new String[0])));
        spGraduatedFrom.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, master.getUniversities().toArray(new String[0])));
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmit(mIsIntern);
            }
        });
        btChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPdf();
            }
        });
        spProfessionalOf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spExcellenceIn.setEnabled(true);
                spExcellenceIn.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, (master.getSectors().get(spProfessionalOf.getSelectedItem().toString())).toArray(new String[0])));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /*AnimateUtils.showLoadingView(progressOverlay);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AnimateUtils.hideLoadingView(progressOverlay);
                    }
                });
            }
        },5000);*/
        RadioGroup radioGroup = findViewById(R.id.rg_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeLayout(checkedId == R.id.rb_intern);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            etEmail.setText(bundle.getString(Constants.IntentKeys.EMAIL));
            etEmail.setEnabled(false);
        }
    }

    private void changeLayout(boolean isIntern) {
        mIsIntern = isIntern;
        if (!isIntern) {
            lLWorkingFor.setVisibility(View.GONE);
            lLProfOf.setVisibility(View.GONE);
            lLGradFrom.setVisibility(View.GONE);
            lLexcel.setVisibility(View.GONE);
            lLPdfButton.setVisibility(View.GONE);
        } else {
            lLWorkingFor.setVisibility(View.VISIBLE);
            lLProfOf.setVisibility(View.VISIBLE);
            lLGradFrom.setVisibility(View.VISIBLE);
            lLexcel.setVisibility(View.VISIBLE);
            lLPdfButton.setVisibility(View.VISIBLE);
        }
        changeUploadUrl(isIntern);
    }

    private void changeUploadUrl(boolean isIntern) {
        if(isIntern)
            uploadUrl = Constants.FireBasePaths.DATABASE_PATH_UPLOADS;
        else
            uploadUrl = Constants.FireBasePaths.DATABASE_PATH_UNIV_TREE;

    }

    private void getPdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }
        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType(APPL_PDF);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, CHOOSER_TITLE), PICK_PDF_CODE);
    }

    private void validateAndSubmit(boolean isIntern) {

        if (!validateName()) {
            return;
        }

        if (!validateMobileNo()) {
            return;
        }
        if (!validateEmailNo()) {
            return;
        }
        if(isIntern){
            if (!validateWorkingFor()) {
                return;
            }
            if (!validateDocument()) {
                return;
            }
            uploadPdfFile(uri);
        }else{
            DataModel dataModel = new DataModel();
            dataModel.setCity(spCity.getSelectedItem().toString());
            dataModel.setName(etName.getText().toString());
            dataModel.seteMail(etEmail.getText().toString());
            dataModel.setMobileNo(etMobileNo.getText().toString());
            saveDetails(dataModel);
        }

        //Toast.makeText(context, Constants.ErrorMessages.VALID_DATA, Toast.LENGTH_LONG).show();
    }

    private void saveEmail(Identity identity) {
        FireBaseHelper.sendObjectToServer(Constants.FireBasePaths.DATABASE_PATH_EMAILS, etEmail.getText().toString().split("\\.")[0], identity, new RequestCallback() {
            @Override
            public void onStart() {
                AnimateUtils.showLoadingView(progressOverlay);
            }

            @Override
            public void onSuccess(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                //new LocalPreferences(context).setIsEmailSaved(true);
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra(Constants.IntentKeys.IS_INTERN,mIsIntern);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                new LocalPreferences(context).setIsEmailSaved(true);
            }
        });
    }

    private boolean validateEmailNo() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError(Constants.ErrorMessages.EMPTY_EMAIL);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
            etEmail.setError(Constants.ErrorMessages.INVALID_EMAIL);
            return false;
        } else {
            tilEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDocument() {
        if (!TextUtils.isEmpty(tvUploadedDoc.getText())) {
            String[] str = tvUploadedDoc.getText().toString().split("\\.");
            if (str.length > 0 && str != null) {
                if (!str[str.length - 1].equalsIgnoreCase("pdf")) {
                    Toast.makeText(this, Constants.ErrorMessages.SELECT_PDF, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(this, Constants.ErrorMessages.SELECT_PDF, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, Constants.ErrorMessages.SELECT_PDF, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateMobileNo() {
        if (TextUtils.isEmpty(etMobileNo.getText())) {
            etMobileNo.setError(Constants.ErrorMessages.EMPTY_PHONE);
            return false;
        } else if (!Patterns.PHONE.matcher(etMobileNo.getText()).matches()) {
            etMobileNo.setError(Constants.ErrorMessages.INVALID_PHONE);
            return false;
        } else {
            tilMobileNo.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateWorkingFor() {
        if (TextUtils.isEmpty(etWorkingFor.getText())) {
            etWorkingFor.setError(Constants.ErrorMessages.EMPTY_FIELD);
            return false;
        } else if (!Pattern.compile(Constants.Regex.SIMPLE_TEXT).matcher(etWorkingFor.getText()).matches()) {
            etWorkingFor.setError(Constants.ErrorMessages.INVALID_FIELD);
            return false;
        } else {
            tilWorkingFor.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateName() {
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError(Constants.ErrorMessages.EMPTY_NAME);
            return false;
        } else if (!Pattern.compile(Constants.Regex.SIMPLE_TEXT).matcher(etName.getText()).matches()) {
            etName.setError(Constants.ErrorMessages.INVALID_NAME);
            return false;
        } else {
            tilName.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                tvUploadedDoc.setText(data.getData().getLastPathSegment());
                uri = data.getData();
                //Toast.makeText(this, "File Found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, Constants.ErrorMessages.NO_FILE_CHOSEN, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPdfFile(Uri data) {
        FireBaseHelper.sendFileToServer(Constants.FireBasePaths.STORAGE_PATH_UPLOADS + etMobileNo.getText().toString() + ".pdf", data, new LazyRequestCallback() {
            @Override
            public void onStart() {
                AnimateUtils.showLoadingView(progressOverlay);
            }

            @Override
            public void onSuccess(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                //textViewStatus.setText("File Uploaded Successfully");
                DataModel dataModel = new DataModel();
                dataModel.setCity(spCity.getSelectedItem().toString());
                dataModel.setName(etName.getText().toString());
                dataModel.setOrganization(etWorkingFor.getText().toString());
                dataModel.setSkill(spProfessionalOf.getSelectedItem().toString());
                dataModel.setUniversity(spGraduatedFrom.getSelectedItem().toString());
                dataModel.seteMail(etEmail.getText().toString());
                dataModel.setMobileNo(etMobileNo.getText().toString());
                dataModel.setExcel(spExcellenceIn.getSelectedItem().toString());
                HashMap<String, String> resume = new HashMap<>();
                resume.put(etMobileNo.getText().toString(), ((UploadTask.TaskSnapshot) object).getMetadata().getReference().getDownloadUrl().toString());
                dataModel.setResumes(resume);
                saveDetails(dataModel);
                //mDatabaseReference.child("uploads").child(intern.getMobileNo()).setValue(intern);
            }

            @Override
            public void onError(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                Toast.makeText(getApplicationContext(), ((Exception) object).getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(Object object) {
               /* double progress = (100.0 * ((UploadTask.TaskSnapshot)object).getBytesTransferred()) / ((UploadTask.TaskSnapshot)object).getTotalByteCount();
                progressBar.setProgress((int) progress);*/
                //textViewStatus.setText((int) progress + "% Uploading...");
            }
        });
    }

    private void saveDetails(DataModel dataModel) {
        FireBaseHelper.sendObjectToServer(uploadUrl, dataModel.geteMail().split("\\.")[0], dataModel, new RequestCallback() {
            @Override
            public void onStart() {
                AnimateUtils.showLoadingView(progressOverlay);
            }

            @Override
            public void onSuccess(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
                Toast.makeText(context, "Double Success", Toast.LENGTH_LONG).show();
                //finish();
                Identity identity = new Identity();
                identity.setRegerstered(true);
                identity.setIntern(mIsIntern);
                saveEmail(identity);
            }

            @Override
            public void onError(Object object) {
                AnimateUtils.hideLoadingView(progressOverlay);
            }
        });
    }
}
