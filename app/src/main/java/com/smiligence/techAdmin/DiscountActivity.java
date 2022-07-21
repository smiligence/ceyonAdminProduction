package com.smiligence.techAdmin;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.techAdmin.Adapter.NormalDiscountAdapter;
import com.smiligence.techAdmin.bean.Discount;
import com.smiligence.techAdmin.bean.ItemReviewAndRatings;
import com.smiligence.techAdmin.common.CommonMethods;
import com.smiligence.techAdmin.common.DateUtils;
import com.smiligence.techAdmin.common.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.smiligence.techAdmin.common.Constant.ACTIVE_STATUS;
import static com.smiligence.techAdmin.common.Constant.BILL_DISCOUNT;
import static com.smiligence.techAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.techAdmin.common.Constant.BOOLEAN_TRUE;
import static com.smiligence.techAdmin.common.Constant.DISCOUNT_CATEGORY;
import static com.smiligence.techAdmin.common.Constant.DISCOUNT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.techAdmin.common.Constant.DISCOUNT_NAME_COLUMN;
import static com.smiligence.techAdmin.common.Constant.DISCOUNT_STATUS_COLUMN;
import static com.smiligence.techAdmin.common.Constant.INACTIVE_STATUS;
import static com.smiligence.techAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.techAdmin.common.Constant.PERCENT_DISCOUNT;
import static com.smiligence.techAdmin.common.Constant.PRICE_DISCOUNT;
import static com.smiligence.techAdmin.common.Constant.TEXT_BLANK;
import static com.smiligence.techAdmin.common.Constant.TITLE_DISCOUNT;
import static com.smiligence.techAdmin.common.MessageConstant.PLEASE_SELECT_IMAGE;
import static com.smiligence.techAdmin.common.MessageConstant.REQUIRED_MSG;

public class DiscountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NormalDiscountAdapter normalDiscountAdapter;
    DatabaseReference databaseReference;
    Spinner value_spinner;
    StorageReference storageReference;
    private NumberFormat nbFmt = NumberFormat.getInstance();
    private BigDecimal value;
    ListView dis_grid;
    String selected_Offer;
    private Uri mimageuri;

    // TODO FREE OFFER need to be implemented and Tested

    String[] OfferSpinner = {DISCOUNT_CATEGORY, BILL_DISCOUNT};
    private ArrayList<Discount> discountArrayList = new ArrayList<Discount>();

    TextView  textStorename;
    String saved_username;
    FloatingActionButton add_discount_button;
    RadioButton radioButton;

    EditText discountName, e_price, min_cash_amount, maxCash;
    RadioGroup percentPrice;
    Button b_choosefile, b_upload;
    String price_percentString;
    ImageView image_discount;

    final static int PICK_IMAGE_REQUEST = 10;
    ProgressBar progressBar;
    DatabaseReference discountDatabaseRefs, storeNameRef;
    StorageReference discountStorageRefs;

    private StorageTask storageTask;

    String discount_Name;
    int maxid = 0;
    AlertDialog dialog;
    TextInputLayout max_price_text;
    TextView reviewCount;
    Button setDate, setTime, setDateoneweek;

    NavigationView navigationView;
    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    static int year, month, day;
    TimePickerDialog timepickerdialog;
    String startTime, endTime;
    String format;
    String timecal;
    String hourcal;
    TextView dateText, hourText, datetextoneWeek;
    private int CalendarHour, CalendarMinute;
    private DatePickerDialog.OnDateSetListener purchaseDateSetListener;
    String dateDayCal, monthCal;
    RadioGroup discountUsage,visibilityGroup;
    String discountUsageStr,visiblityStr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_discount);
        dis_grid = findViewById(R.id.dis_mainGridView);
        add_discount_button = findViewById(R.id.add_discount_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableAutofill();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(TITLE_DISCOUNT);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.discounts);
        disableAutofill();


        navigationView.setNavigationItemSelectedListener(DiscountActivity.this);

        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);

        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName); reviewCount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));
        dateText = findViewById(R.id.setdatetext);
        hourText = findViewById(R.id.hourText);


        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginSharedPreferences.edit();
        saved_username = loginSharedPreferences.getString("userNameStr", "");


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(DiscountActivity.this, android.R.layout.simple_spinner_item, OfferSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        discountDatabaseRefs = CommonMethods.fetchFirebaseDatabaseReference(DISCOUNT_DETAILS_FIREBASE_TABLE);
        discountStorageRefs = CommonMethods.fetchFirebaseStorageReference(DISCOUNT_DETAILS_FIREBASE_TABLE);


        discountDatabaseRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxid = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        add_discount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiscountActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View alertdialogView = inflater.inflate(R.layout.add_discount_dialog, null);
                alertDialogBuilder.setView(alertdialogView);


                discountName = alertdialogView.findViewById(R.id.d_name);
                e_price = alertdialogView.findViewById(R.id.text_priceper);
                percentPrice = alertdialogView.findViewById(R.id.pricepercent);
                min_cash_amount = alertdialogView.findViewById(R.id.min_amount);
                progressBar = alertdialogView.findViewById(R.id.progressBar);
                max_price_text = alertdialogView.findViewById(R.id.max_price_text);
                setDate = alertdialogView.findViewById(R.id.setdate);
                setTime = alertdialogView.findViewById(R.id.settime);
                dateText = alertdialogView.findViewById(R.id.setdatetext);
                hourText = alertdialogView.findViewById(R.id.hourText);

                maxCash = alertdialogView.findViewById(R.id.max_amount);
                b_choosefile = alertdialogView.findViewById(R.id.pick);
                image_discount = alertdialogView.findViewById(R.id.imageupload);
                b_upload = alertdialogView.findViewById(R.id.upload);

                discountUsage=alertdialogView.findViewById(R.id.discountUsage);

                visibilityGroup=alertdialogView.findViewById(R.id.visiblityLayout);


                ImageView cancel = alertdialogView.findViewById(R.id.iv_cancel);


                dialog = alertDialogBuilder.create();
                dialog.show();
                discountUsageStr="One Time Discount";
                visiblityStr="Visible";

                discountUsage.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
                    checkedId = discountUsage.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton)discountUsage.findViewById(checkedId);
                    discountUsageStr = radioButton.getText().toString();

                });
                visibilityGroup.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
                    checkedId = visibilityGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton)visibilityGroup.findViewById(checkedId);
                    visiblityStr = radioButton.getText().toString();

                });


                setTime.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        Calendar mcurrentTime = Calendar.getInstance ();
                        final int hour = mcurrentTime.get ( Calendar.HOUR_OF_DAY );
                        int minute = mcurrentTime.get ( Calendar.MINUTE );
                        TimePickerDialog mTimePicker;

                        mTimePicker = new TimePickerDialog ( DiscountActivity.this, new TimePickerDialog.OnTimeSetListener () {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if (selectedHour < 12) {
                                    hourText.setText(pad(selectedHour) + ":" + pad(selectedMinute)+ " AM");

                                } else {
                                    hourText.setText(pad(selectedHour) + ":" + pad(selectedMinute)+ " PM");

                                }
                            }
                        }, hour, minute, true );//Yes 24 hour time

                        mTimePicker.setTitle ( "Select Time" );
                        mTimePicker.show ();

                    }
                } );

                setDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);

                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog dialog = new DatePickerDialog(
                                DiscountActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                purchaseDateSetListener,
                                year, month, day);

                        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                purchaseDateSetListener = (datePicker, year, month, day) -> {


                    month = month + 1;
                    if (day == 0 || day < 10) {
                        dateDayCal = "0" + day;
                    } else if (day >= 10) {
                        dateDayCal = String.valueOf(day);
                    }
                    if (month == 0 || month < 10) {
                        monthCal = "0" + month;
                    } else if (month >= 10) {
                        monthCal = String.valueOf(month);
                    }


                    String date = dateDayCal + "-" + monthCal + "-" + year;
                    dateText.setText(date);
                };

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                b_choosefile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
                    }
                });

                percentPrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        checkedId = percentPrice.getCheckedRadioButtonId();
                        radioButton = (RadioButton) dialog.findViewById(checkedId);
                        price_percentString = radioButton.getText().toString();

                        if ("price".equals(price_percentString)) {
                            int maxTextLength = 4;
                            e_price.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTextLength)});
                            if (e_price.getText().toString().length() > 4) {
                                e_price.setText("");
                            }
                            maxCash.setTextIsSelectable(BOOLEAN_FALSE);
                            maxCash.setFocusable(BOOLEAN_FALSE);
                            maxCash.setVisibility(View.INVISIBLE);
                            max_price_text.setVisibility(View.INVISIBLE);
                        }else if ("percent".equals(price_percentString)) {
                            int maxTextLength = 2;
                            e_price.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTextLength)});
                            if (e_price.getText().toString().length() > 2) {
                                e_price.setText("");
                            }
                            maxCash.setTextIsSelectable(BOOLEAN_TRUE);
                            maxCash.setFocusable(BOOLEAN_TRUE);
                            maxCash.setVisibility(View.VISIBLE);
                            max_price_text.setVisibility(View.VISIBLE);
                        }
                    }
                });

                b_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String amount_min_cash_amount = min_cash_amount.getText().toString().trim();
                        String amount_maxCash = maxCash.getText().toString().trim();
                        if (discountName == null || "".equalsIgnoreCase(discountName.getText().toString())) {
                            discountName.setError("Enter Discount Name");

                            return;
                        } else if (!"".equalsIgnoreCase(discountName.getText().toString()) && !TextUtils.validDiscountName(discountName.getText().toString())) {
                            discountName.setError("Enter valid Discount Name");

                            return;
                        } else if (price_percentString == null || "".equalsIgnoreCase(price_percentString)) {
                            Toast.makeText(DiscountActivity.this, "Please select the Discount Type.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hourText.getText().toString().equals("") || hourText.getText().toString()==null || hourText.getText().toString().isEmpty())
                        {
                            Toast.makeText(DiscountActivity.this, "Please select discount validity Time", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (dateText.getText().toString().equals("") || dateText.getText().toString()==null || dateText.getText().toString().isEmpty())
                        {
                            Toast.makeText(DiscountActivity.this, "Please select discount validity Date", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (discountUsageStr==null || discountUsageStr.equals(""))
                        {
                            Toast.makeText(DiscountActivity.this, "Please select discount usage period", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (visiblityStr==null || visiblityStr.equals(""))
                        {
                            Toast.makeText(DiscountActivity.this, "Please select discount visibility", Toast.LENGTH_SHORT).show();
                            return;
                        }else if ("price".equals(price_percentString)) {
                            maxCash.setText(TEXT_BLANK);

                            if (e_price == null || "".equalsIgnoreCase(e_price.getText().toString())) {
                                e_price.setError("Enter valid Discount Price");
                                return;
                            } else if (!e_price.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(e_price.getText().toString().trim())) == 0) {
                                e_price.setError("Enter valid amount");

                                return;
                            } else if (min_cash_amount == null || "".equalsIgnoreCase(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError(REQUIRED_MSG);

                                return;
                            } else if (!"".equalsIgnoreCase(min_cash_amount.getText().toString()) && !TextUtils.isValidPrice(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError("Enter valid minimum Bill amount");

                                return;
                            } else if (!maxCash.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(maxCash.getText().toString().trim())) == 0) {
                                maxCash.setError("Enter valid amount");


                                return;
                            } else if (!min_cash_amount.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(min_cash_amount.getText().toString().trim())) == 0) {
                                min_cash_amount.setError("Enter valid amount");

                                return;
                            }
                            else if (Integer.parseInt(e_price.getText().toString())>=Integer.parseInt(min_cash_amount.getText().toString()))
                            {
                                e_price.setError("Discount amount must be less than minimum bill amount");
                                return;
                            }
                        } else if ("percent".equals(price_percentString)) {

                            if (e_price == null || "".equalsIgnoreCase(e_price.getText().toString())) {
                                e_price.setError("Enter valid Discount Percentage");
                                return;
                            } else if (!e_price.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(e_price.getText().toString().trim())) == 0) {
                                e_price.setError("Enter valid Percent");
                                return;
                            } else if (min_cash_amount == null || "".equalsIgnoreCase(min_cash_amount.getText().toString())) {
                                min_cash_amount.setError(REQUIRED_MSG);

                                return;
                            } else if (!min_cash_amount.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(min_cash_amount.getText().toString().trim())) == 0) {
                                min_cash_amount.setError("Enter valid amount");
                                return;
                            } else if (maxCash == null || "".equalsIgnoreCase(maxCash.getText().toString())) {
                                maxCash.setError(REQUIRED_MSG);

                                return;
                            } else if (!maxCash.getText().toString().isEmpty() && Integer.parseInt(String.valueOf(maxCash.getText().toString().trim())) == 0) {
                                maxCash.setError("Enter valid amount");
                                return;
                            } else if (!"".equalsIgnoreCase(maxCash.getText().toString()) && !(Integer.parseInt(maxCash.getText().toString()) < Integer.parseInt(min_cash_amount.getText().toString()))) {
                                Toast.makeText(DiscountActivity.this, "Minimum Bill Amount cannot less or equal than Discount Amount", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if (Integer.parseInt(e_price.getText().toString())>=Integer.parseInt(min_cash_amount.getText().toString()))
                            {
                                e_price.setError("Discount amount must be less than minimum bill amount");
                                return;
                            }
                        }

                            if (!"".equals(discountName.getText().toString().trim())
                                    && !"".equals(e_price.getText().toString().trim())) {
                                b_upload.setVisibility(View.INVISIBLE);
                                e_price.setTextIsSelectable(BOOLEAN_FALSE);
                                e_price.setFocusable(BOOLEAN_FALSE);
                                maxCash.setTextIsSelectable(BOOLEAN_FALSE);
                                maxCash.setFocusable(BOOLEAN_FALSE);
                                min_cash_amount.setTextIsSelectable(BOOLEAN_FALSE);
                                min_cash_amount.setFocusable(BOOLEAN_FALSE);
                                maxCash.setTextIsSelectable(BOOLEAN_FALSE);
                                maxCash.setFocusable(BOOLEAN_FALSE);
                                UploadFile();
                            }


                    }
                });
            }
        });

        databaseReference = CommonMethods.fetchFirebaseDatabaseReference(DISCOUNT_DETAILS_FIREBASE_TABLE);
        storageReference = CommonMethods.fetchFirebaseStorageReference(DISCOUNT_DETAILS_FIREBASE_TABLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                discountArrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Discount discount = postSnapshot.getValue(Discount.class);
                    discountArrayList.add(discount);
                }

                if (!discountArrayList.isEmpty() || discountArrayList != null) {
                    normalDiscountAdapter = new NormalDiscountAdapter(DiscountActivity.this, discountArrayList);
                    dis_grid.setAdapter(normalDiscountAdapter);
                    normalDiscountAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dis_grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Discount displayDiscount = discountArrayList.get(position);
                final String discountId = String.valueOf(displayDiscount.getDiscountId());
                final AlertDialog.Builder builder = new AlertDialog.Builder(DiscountActivity.this);
                builder.setMessage("Select the state of Discount");
                builder.setCancelable(true);
                builder.setNegativeButton("InActive", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference.child(discountId).child(DISCOUNT_STATUS_COLUMN).setValue("InActive");
                        normalDiscountAdapter = new NormalDiscountAdapter(DiscountActivity.this, discountArrayList);
                        dis_grid.setAdapter(normalDiscountAdapter);
                        dis_grid.invalidateViews();
                        normalDiscountAdapter.notifyDataSetInvalidated();

                    }
                });

                builder.setPositiveButton("Active", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference.child(discountId).child(DISCOUNT_STATUS_COLUMN).setValue("Active");
                        normalDiscountAdapter = new NormalDiscountAdapter(DiscountActivity.this, discountArrayList);

                        dis_grid.setAdapter(normalDiscountAdapter);
                        dis_grid.invalidateViews();

                        normalDiscountAdapter.notifyDataSetInvalidated();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mimageuri = data.getData();
            Glide.with(DiscountActivity.this).load(mimageuri).into(image_discount);
        }
    }

    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void timePicker() {

        endTime = "";
        timepickerdialog = new TimePickerDialog(DiscountActivity.this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay == 0) {
                        hourOfDay += 12;
                        format = "AM";
                    } else if (hourOfDay == 12) {
                        format = "PM";
                    } else if (hourOfDay > 12) {
                        hourOfDay -= 12;
                        format = "PM";
                    } else {
                        format = "AM";
                    }
                    if (minute == 0 || minute < 10) {
                        timecal = "0" + minute;
                    } else if (minute > 10) {
                        timecal = String.valueOf(minute);
                    } else if (minute == 10) {

                        timecal = String.valueOf(minute);
                    }

                    if (hourOfDay == 0 || hourOfDay < 10) {
                        hourcal = "0" + hourOfDay;
                    } else if (hourOfDay > 10) {
                        hourcal = "" + hourOfDay;
                    } else if (hourOfDay == 10) {
                        hourcal = "" + hourOfDay;
                    }


                    endTime = hourcal + ":" + timecal + " " + format;
                    hourText.setText(endTime);

                }, CalendarHour, CalendarMinute, false);
        timepickerdialog.show();

    }
    private void UploadFile() {

        if (mimageuri != null) {
            StorageReference fileRef = discountStorageRefs.child(System.currentTimeMillis() + "." + getExtenstion(mimageuri));


            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            storageTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 5000);

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();

                    discount_Name = discountName.getText().toString().trim();
                    Query query = discountDatabaseRefs.orderByChild(DISCOUNT_NAME_COLUMN).equalTo(discount_Name);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getChildrenCount() > 0) {

                                Toast.makeText(DiscountActivity.this, "Discount Name Already Exists", Toast.LENGTH_LONG).show();
                                discountName.setText(TEXT_BLANK);
                                b_upload.setVisibility(View.VISIBLE);
                                e_price.setTextIsSelectable(BOOLEAN_TRUE);
                                e_price.setFocusable(BOOLEAN_TRUE);
                                min_cash_amount.setTextIsSelectable(BOOLEAN_TRUE);
                                min_cash_amount.setFocusable(BOOLEAN_TRUE);
                                maxCash.setTextIsSelectable(BOOLEAN_TRUE);
                                maxCash.setFocusable(BOOLEAN_TRUE);
                            } else {

                                Discount discount = new Discount();
                                discount.setDiscountId(maxid + 1);
                                discount.setDiscountName(discount_Name);
                                discount.setDiscountType(BILL_DISCOUNT);

                                if (price_percentString.equals("price")) {
                                    discount.setTypeOfDiscount(PRICE_DISCOUNT);
                                    discount.setDiscountPrice(e_price.getText().toString());
                                    discount.setDiscountPercentageValue("");
                                } else if (price_percentString.equals("percent")) {
                                    discount.setTypeOfDiscount(PERCENT_DISCOUNT);
                                    discount.setDiscountPercentageValue(e_price.getText().toString());
                                    discount.setDiscountPrice("");
                                } else {
                                    discount.setDiscountPercentageValue("");
                                    discount.setDiscountPrice("");
                                }

                                discount.setDiscountImage(downloadUrl.toString());
                                discount.setDiscountStatus("Active");
                                discount.setBuydiscountItem("");
                                discount.setGetdiscountItem("");
                                discount.setBuyOfferCount(0);
                                discount.setGetOfferCount(0);
                                discount.setDiscountCoupon("");
                                discount.setMinmumBillAmount("");
                                discount.setValidTillDate(dateText.getText().toString());
                                discount.setValidTillTime(hourText.getText().toString());
                                discount.setVisibility(visiblityStr);
                                discount.setUsage(discountUsageStr);

                                if (!(maxCash.getText().toString().isEmpty())) {
                                    discount.setMaxAmountForDiscount(maxCash.getText().toString());
                                } else {
                                    discount.setMaxAmountForDiscount("");
                                }

                                if (!(min_cash_amount.getText().toString().isEmpty())) {
                                    discount.setMinmumBillAmount(min_cash_amount.getText().toString());
                                    min_cash_amount.setText("");
                                } else {
                                    discount.setMinmumBillAmount("");
                                }

                                discount.setCreateDate(DateUtils.fetchCurrentDateAndTime());
                                discountDatabaseRefs.child(String.valueOf(maxid + 1)).setValue(discount);
                                dialog.dismiss();
                                Toast.makeText(DiscountActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();

                                clearData();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DiscountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(DiscountActivity.this, PLEASE_SELECT_IMAGE, Toast.LENGTH_LONG).show();
            b_upload.setVisibility(View.VISIBLE);
        }
    }

    public void clearData() {
        discountName.setText(TEXT_BLANK);
        e_price.setText(TEXT_BLANK);
        min_cash_amount.setText(TEXT_BLANK);
        maxCash.setText(TEXT_BLANK);
        mimageuri = null;
        image_discount.setImageResource(R.drawable.b_chooseimage);
        b_upload.setVisibility(View.VISIBLE);
        e_price.setTextIsSelectable(BOOLEAN_TRUE);
        e_price.setFocusable(BOOLEAN_TRUE);
        min_cash_amount.setTextIsSelectable(BOOLEAN_TRUE);
        min_cash_amount.setFocusable(BOOLEAN_TRUE);
        maxCash.setTextIsSelectable(BOOLEAN_TRUE);
        maxCash.setFocusable(BOOLEAN_TRUE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.vieworders) {
            Intent intent = new Intent(getApplicationContext(), ViewOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.Add_Advertisment) {

            Intent intent = new Intent(getApplicationContext(), AddAdvertisementActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.add_items) {
            Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.discounts) {
            Intent intent = new Intent(getApplicationContext(), DiscountActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.add_seller) {
            Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.updatestatus) {
            Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }/*else if(id==R.id.deliveryfare){
            Intent intent = new Intent(getApplicationContext(), AddDeliveryFareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/else  if(id==R.id.logoutscreen){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( DiscountActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    SharedPreferences myPrefs = getSharedPreferences ( "LOGIN",
                            MODE_PRIVATE );
                    SharedPreferences.Editor editor = myPrefs.edit ();
                    editor.clear ();
                    editor.commit ();
                    Intent intent = new Intent( DiscountActivity.this,
                            LoginActivity.class );
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity ( intent );
                    bottomSheetDialog.dismiss ();
                }
            } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss ();
                    navigationView.setCheckedItem(R.id.discounts);
                }
            } );
        }else  if(id==R.id.contactdetails){

            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else  if(id==R.id.CustomerDetails){
            Intent intent = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else  if(id==R.id.reviewApproval){
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else  if(id==R.id.legal){
            Intent intent = new Intent(getApplicationContext(), LegalDetailsUploadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else  if(id==R.id.dashboard){
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayList<ItemReviewAndRatings> list = new ArrayList<>();
        DatabaseReference
                itemReviewDataRef = CommonMethods.fetchFirebaseDatabaseReference(ITEM_RATING_REVIEW_TABLE);
        Query itemApprovalStatusQuery = itemReviewDataRef.orderByChild("itemRatingReviewStatus").equalTo("Waiting For Approval");
        itemApprovalStatusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ItemReviewAndRatings itemReviewAndRatings = dataSnapshot1.getValue(ItemReviewAndRatings.class);

                    list.add(itemReviewAndRatings);
                }
                reviewCount.setText(String.valueOf(list.size()));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public String pad(int input)
    {

        String str = "";

        if (input >= 10) {

            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);

        }
        return str;
    }
}
