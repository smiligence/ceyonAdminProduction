package com.smiligence.techAdmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.techAdmin.bean.ItemDetails;
import com.smiligence.techAdmin.bean.ItemReviewAndRatings;
import com.smiligence.techAdmin.bean.OrderDetails;
import com.smiligence.techAdmin.common.CommonMethods;
import com.smiligence.techAdmin.common.FcmNotificationsSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.smiligence.techAdmin.common.Constant.DASHBOARD;
import static com.smiligence.techAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;


public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public String userNameStr, passwordStr;
    TextView sales, items, quantity, customer, bill_report, storeNameText;
    Button viewSalesReport, viewItemsReport;
    DatabaseReference billdataref, logindatabase;
    BarChart barChart, salesBarChart;
    final ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();
    final ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    List<String> customerList = new ArrayList<>();
    List<String> billTimeArrayList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    List<String> storeList = new ArrayList<>();
    List<String> shippingPincodeList = new ArrayList<>();
    final ArrayList<String> itemName = new ArrayList<>();
    ArrayList<String> billList = new ArrayList<>();

    int Expense = 0;
    int uniqueItemCount = 0;
    int todaysTotalSalesAmt = 0;

    int todaysTotalQty = 0;
    int uniqueCustomerCount = 0;
    int totalItemCount = 0;
    final boolean[] onDataChangeCheck = {false};
    public static String saved_username, saved_password, saved_Id;


    HashMap<String, Integer> billAmountHashMap = new HashMap<>();
    Query query;
    NavigationView navigationView;

    public static String saved_productKey, saved_businessName, saved_userName, saved_email;
    TextView categoryText, locationText, deliveryBoyText;
    ArrayList<String> categoryList = new ArrayList<>();

    boolean check = false;

    public static TextView textViewUsername;
    public static TextView textViewEmail;
    public static ImageView imageView;
    public static Menu menuNav;
    public static View mHeaderView;
    DatabaseReference logindataRef;
    static String roleName, userName;
    TextView reviewCount;
    boolean isCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);





        viewSalesReport = findViewById(R.id.viewreport);
        sales = findViewById(R.id.sales);
        items = findViewById(R.id.items);
        // quantity = findViewById(R.id.quantity);
        customer = findViewById(R.id.customer);
        bill_report = findViewById(R.id.bill);
        //  storeNameText = findViewById(R.id.storeName);
        barChart = findViewById(R.id.barChart);
        viewItemsReport = findViewById(R.id.itemReports);
        salesBarChart = findViewById(R.id.salesBarChart);
        categoryText = findViewById(R.id.category);
        locationText = findViewById(R.id.location);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(DASHBOARD);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DashBoardActivity.this);
        navigationView.setCheckedItem(R.id.dashboard);

        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));
    //    onStart();

        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_userName = loginSharedPreferences.getString("userName", "");
        saved_productKey = loginSharedPreferences.getString("productkeyStr", "");
        saved_businessName = loginSharedPreferences.getString("businessNameStr", "");
        saved_email = loginSharedPreferences.getString("email", "");


        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);

        textViewUsername.setText(roleName);
        textViewEmail.setText(userName);


     //  CommonMethods.fetchFirebaseDatabaseReference("OrderDetails").removeValue();


     //   logindataRef = CommonMethods.fetchFirebaseDatabaseReference("UserDetails");
//        final Query roleNameQuery = logindataRef.orderByChild(PHONE_NUM_COLUMN).equalTo(String.valueOf(saved_userName));
//        roleNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    UserDetails loginUserDetailslist = snap.getValue(UserDetails.class);
//                    roleName = loginUserDetailslist.getRoleName();
//                    userName = loginUserDetailslist.getFirstName();
//                    textViewUsername.setText(roleName);
//                    textViewEmail.setText(userName);
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        billdataref = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
//
//        final Query billDetailsQuery = billdataref.orderByChild("formattedDate").equalTo(DateUtils.fetchCurrentDate());
//
//
//        billDetailsQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                clearData();
//
//                if (dataSnapshot.getChildrenCount() > 0) {
//                    for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {
//
//
//                        billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));
//                        itemDetailsArrayList.add(billSnapShot.getValue(ItemDetails.class));
//
//                        onDataChangeCheck[0] = true;
//                    }
//
//
//                    //  if (onDataChangeCheck[0] == true) {
//                    todaysTotalSalesAmt = 0;
//                    Iterator billIterator = billDetailsArrayList.iterator();
//                    customerList.clear();
//                    while (billIterator.hasNext()) {
//
//                        OrderDetails orderDetails = (OrderDetails) billIterator.next();
//                        if (!orderDetails.getOrderStatus().equals("Order Canceled")) {
//                            customerList.add(orderDetails.getCustomerName());
//
//                            storeList.add(orderDetails.getStoreName());
//                            shippingPincodeList.add(orderDetails.getShippingPincode());
//
//                            todaysTotalSalesAmt += (orderDetails.getPaymentamount());
//                            sales.setText(String.valueOf(todaysTotalSalesAmt));
//                            billList.add(orderDetails.getOrderStatus());
//                            billTimeArrayList.add(DateUtils.fetchTimewithSeconds(orderDetails.getOrderCreateDate()));
//                            billAmountHashMap.put(DateUtils.fetchTimewithSeconds(orderDetails.getOrderCreateDate()), orderDetails.getTotalAmount());
//
//                            Iterator itemIterator = orderDetails.getItemDetailList().iterator();
//                            uniqueItemCount = 0;
//                            uniqueCustomerCount = 0;
//
//                            while (itemIterator.hasNext()) {
//
//                                ItemDetails itemDetails = (ItemDetails) itemIterator.next();
//                                itemName.add(itemDetails.getItemName());
//
//                                todaysTotalQty = todaysTotalQty + itemDetails.getItemBuyQuantity();
//                                itemList.add(itemDetails.getItemName());
//
//                                Iterator categoryIterator = itemDetails.getCategoryDetailsArrayList().iterator();
//                                while (categoryIterator.hasNext()) {
//                                    CategoryDetails categoryDetails = (CategoryDetails) categoryIterator.next();
//                                    categoryList.add(categoryDetails.getCategoryName());
//
//                                }
//
//                            }
//
//
//                            //   quantity.setText(String.valueOf(todaysTotalQty));
//                            removeDuplicatesList(categoryList);
//                            removeDuplicatesList(shippingPincodeList);
//
//
//                            ArrayList<String> newItemList = TextUtils.removeDuplicates((ArrayList<String>) itemList);
//                            ArrayList<String> newCustomerList = TextUtils.removeDuplicates((ArrayList<String>) customerList);
//                            ArrayList<String> newItemNameList = TextUtils.removeDuplicates((ArrayList<String>) itemName);
//                            ArrayList<String> newStoreNameList = TextUtils.removeDuplicates((ArrayList<String>) storeList);
//
//                            uniqueItemCount = uniqueItemCount + newItemList.size();
//                            uniqueCustomerCount = uniqueCustomerCount + newCustomerList.size();
//                            items.setText(String.valueOf(uniqueItemCount));
//
//
//                            int noOfBills = billList.size();
//                            bill_report.setText(String.valueOf(noOfBills));
//                            customer.setText(String.valueOf(uniqueCustomerCount));
//                            //  storeNameText.setText(String.valueOf(newStoreNameList.size()));
//                            categoryText.setText(String.valueOf(categoryList.size()));
//                            locationText.setText(String.valueOf(shippingPincodeList.size()));
//
//                            CommonMethods.loadBarChart(barChart, (ArrayList<String>) billTimeArrayList);
//                            barChart.animateXY(7000, 5000);
//                            barChart.invalidate();
//                            barChart.getDrawableState();
//                            barChart.setPinchZoom(true);
//
//
//                            CommonMethods.loadSalesBarChart(salesBarChart, billAmountHashMap);
//
//                            salesBarChart.animateXY(7000, 5000);
//                            salesBarChart.invalidate();
//                            salesBarChart.getDrawableState();
//
//                        }
//                    }
//
//                }
//            }
//
//            private void clearData() {
//
//                billDetailsArrayList.clear();
//                customerList.clear();
//                itemList.clear();
//                itemDetailsArrayList.clear();
//                billTimeArrayList.clear();
//                storeList.clear();
//                billList.clear();
//                itemName.clear();
//                shippingPincodeList.clear();
//                uniqueItemCount = 0;
//                uniqueCustomerCount = 0;
//                todaysTotalSalesAmt = 0;
//                todaysTotalQty = 0;
//                totalItemCount = 0;
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        viewSalesReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashBoardActivity.this, ReportGenerationActivity.class);
//                intent.putExtra("Indicator", "1");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });
//
//        viewItemsReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashBoardActivity.this, ReportGenerationActivity.class);
//                intent.putExtra("Indicator", "2");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public void onBackPressed() {
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DashBoardActivity.this);
//        bottomSheetDialog.setContentView(R.layout.application_exiting_dialog);
//        Button quit = bottomSheetDialog.findViewById(R.id.quit_dialog);
//        Button cancel = bottomSheetDialog.findViewById(R.id.cancel_dialog);
//
//        bottomSheetDialog.show();
//        bottomSheetDialog.setCancelable(false);
//
//        quit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                moveTaskToBack(true);
//                bottomSheetDialog.dismiss();
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetDialog.dismiss();
//            }
//        });

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
        } else if (id == R.id.updatestatus) {
            Intent intent = new Intent(getApplicationContext(), UpdateOrderStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DashBoardActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences myPrefs = getSharedPreferences("LOGIN",
                            MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(DashBoardActivity.this,
                            LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            stayinapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    navigationView.setCheckedItem(R.id.dashboard);
                }
            });
        } else if (id == R.id.contactdetails) {

            Intent intent = new Intent(getApplicationContext(), DeliverySettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.CustomerDetails) {
            Intent intent = new Intent(getApplicationContext(), CustomerDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.legal) {
            Intent intent = new Intent(getApplicationContext(), LegalDetailsUploadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.reviewApproval) {
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.billingScreen) {
            Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        ArrayList<ItemReviewAndRatings> list = new ArrayList<>();
//        DatabaseReference
//                itemReviewDataRef = CommonMethods.fetchFirebaseDatabaseReference(ITEM_RATING_REVIEW_TABLE);
//        Query itemApprovalStatusQuery = itemReviewDataRef.orderByChild("itemRatingReviewStatus").equalTo("Waiting For Approval");
//        itemApprovalStatusQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    ItemReviewAndRatings itemReviewAndRatings = dataSnapshot1.getValue(ItemReviewAndRatings.class);
//
//                    list.add(itemReviewAndRatings);
//                }
//                reviewCount.setText(String.valueOf(list.size()));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }
}