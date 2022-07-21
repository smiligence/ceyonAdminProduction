package com.smiligence.techAdmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.techAdmin.Adapter.PincodeDetailsAdapter;
import com.smiligence.techAdmin.bean.DeliveryFareDetails;
import com.smiligence.techAdmin.bean.ItemReviewAndRatings;
import com.smiligence.techAdmin.bean.PincodeDetails;
import com.smiligence.techAdmin.common.CommonMethods;
import com.smiligence.techAdmin.common.DateUtils;
import com.smiligence.techAdmin.common.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;

import static com.smiligence.techAdmin.ViewOrderActivity.mHeaderView;
import static com.smiligence.techAdmin.common.Constant.ACTIVE_STATUS;
import static com.smiligence.techAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.techAdmin.common.Constant.BOOLEAN_TRUE;
import static com.smiligence.techAdmin.common.Constant.ENTER_VALID_PRICE;
import static com.smiligence.techAdmin.common.Constant.INACTIVE_STATUS;
import static com.smiligence.techAdmin.common.Constant.ITEM_RATING_REVIEW_TABLE;
import static com.smiligence.techAdmin.common.Constant.REQUIRED;
import static com.smiligence.techAdmin.common.Constant.RESTRICTED_PINCODE;

public class DeliverySettings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button uploadContactDetails;

    DatabaseReference pincodeRef;
    Button button, insta;
    NavigationView navigationView;
    TextView textViewUsername, textViewEmail;
    Button addDeliveryFare;
    EditText maxCartValue, deliveryFee;
    DatabaseReference deliveryFareRef;
    EditText emailText;
    TextView reviewCount;
    long maxid;
    ListView pincodeListview;
    FloatingActionButton addPinCode;
    EditText pincodeEditText;
    ImageView cancelImage;
    Button addPincodeButton;
    PincodeDetails pincodeDetails;
    ArrayList<PincodeDetails> pincodeDetailsArrayList = new ArrayList<>();
    public static boolean isValidPinCode = BOOLEAN_FALSE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        addDeliveryFare = findViewById(R.id.add_delivery);
        maxCartValue = findViewById(R.id.max_cart_value);
        deliveryFee = findViewById(R.id.deliveryfee);
        pincodeListview = findViewById(R.id.pincodeListview);
        addPinCode = findViewById(R.id.fab);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle("Delivery Settings");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DeliverySettings.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.contactdetails);
        mHeaderView = navigationView.getHeaderView(0);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.username);
        textViewEmail = (TextView) mHeaderView.findViewById(R.id.roleName);
        textViewUsername.setText(DashBoardActivity.roleName);
        textViewEmail.setText(DashBoardActivity.userName);
        reviewCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.reviewApproval));
        reviewCount.setGravity(Gravity.CENTER_VERTICAL);
        reviewCount.setTypeface(null, Typeface.BOLD);
        reviewCount.setTextColor(getResources().getColor(R.color.redColor));

        pincodeRef = CommonMethods.fetchFirebaseDatabaseReference(RESTRICTED_PINCODE);
        deliveryFareRef = CommonMethods.fetchFirebaseDatabaseReference("DeliveryFareDetails");
        deliveryFareRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DeliveryFareDetails deliveryFareDetails = dataSnapshot.getValue(DeliveryFareDetails.class);
                    maxCartValue.setText(String.valueOf(deliveryFareDetails.getCartValue()));
                    deliveryFee.setText(String.valueOf(deliveryFareDetails.getDeliveryFare()));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        addDeliveryFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (maxCartValue.getText().toString().trim() == null || maxCartValue.getText().toString().trim().isEmpty()) {
                    maxCartValue.setError(REQUIRED);
                    Toast.makeText(DeliverySettings.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                    return;
                } else if (maxCartValue.getText().toString().trim() != null && !TextUtils.isValidPrice(maxCartValue.getText().toString().trim())) {
                    maxCartValue.setError(ENTER_VALID_PRICE);
                    Toast.makeText(DeliverySettings.this, ""+ENTER_VALID_PRICE, Toast.LENGTH_SHORT).show();
                    return;
                } else if (maxCartValue.getText().toString().trim() != null && Integer.parseInt(maxCartValue.getText().toString().trim()) == 0) {
                    maxCartValue.setError(ENTER_VALID_PRICE);
                    Toast.makeText(DeliverySettings.this, ""+ENTER_VALID_PRICE, Toast.LENGTH_SHORT).show();
                    return;
                } else if (deliveryFee.getText().toString().trim() == null || deliveryFee.getText().toString().trim().isEmpty()) {
                    deliveryFee.setError(REQUIRED);
                    Toast.makeText(DeliverySettings.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                    return;
                } else if (deliveryFee.getText().toString().trim() != null && !TextUtils.isValidPrice(deliveryFee.getText().toString().trim())) {
                    deliveryFee.setError(ENTER_VALID_PRICE);
                    Toast.makeText(DeliverySettings.this, ""+ENTER_VALID_PRICE, Toast.LENGTH_SHORT).show();
                    return;
                } else if (deliveryFee.getText().toString().trim() != null && Integer.parseInt(deliveryFee.getText().toString().trim()) == 0) {
                    deliveryFee.setError(ENTER_VALID_PRICE);
                    Toast.makeText(DeliverySettings.this, ""+ENTER_VALID_PRICE, Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(deliveryFee.getText().toString().trim()) >= Integer.parseInt(maxCartValue.getText().toString().trim())) {
                    Toast.makeText(DeliverySettings.this, "Delivery fare should not be greater than or equal to MaxCart value",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    deliveryFareRef = CommonMethods.fetchFirebaseDatabaseReference("DeliveryFareDetails");

                    DeliveryFareDetails deliveryFareDetails = new DeliveryFareDetails();
                    deliveryFareDetails.setCartValue(Integer.parseInt(maxCartValue.getText().toString().trim()));
                    deliveryFareDetails.setDeliveryFare(Integer.parseInt(deliveryFee.getText().toString().trim()));
                    deliveryFareDetails.setCreatedDate(DateUtils.fetchCurrentDateAndTime());

                    deliveryFareRef.setValue(deliveryFareDetails);

                    Toast.makeText(DeliverySettings.this, "Delivery Fare details Added Successfully", Toast.LENGTH_SHORT).show();
                }


            }
        });

        addPinCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DeliverySettings.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.pincode_restriction, null);
                dialogBuilder.setView(dialogView);

                pincodeEditText = dialogView.findViewById(R.id.pincodeEditText);
                addPincodeButton = dialogView.findViewById(R.id.addpincode);
                cancelImage = dialogView.findViewById(R.id.cancel);


                final AlertDialog b = dialogBuilder.create();
                b.show();
                b.setCancelable(false);

                cancelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.cancel();
                    }
                });
                addPincodeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ("".equalsIgnoreCase(pincodeEditText.getText().toString().trim())) {
                            pincodeEditText.setError(REQUIRED);
                            Toast.makeText(DeliverySettings.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!pincodeEditText.getText().toString().trim().isEmpty() &&
                                !TextUtils.isValidIndiaPincode(pincodeEditText.getText().toString().trim())) {
                            pincodeEditText.setError("Enter Valid PinCode");
                            Toast.makeText(DeliverySettings.this, "Enter Valid PinCode", Toast.LENGTH_SHORT).show();
                            return;

                        } else {

                            if (pincodeDetailsArrayList != null && pincodeEditText.getText().toString().trim() != null) {
                                Iterator itemIterator = pincodeDetailsArrayList.iterator();

                                while (itemIterator.hasNext()) {

                                    PincodeDetails pincodeDetails = (PincodeDetails) itemIterator.next();
                                    if (pincodeEditText.getText().toString().trim().equalsIgnoreCase(pincodeDetails.getPinCode())) {

                                        isValidPinCode = BOOLEAN_TRUE;
                                        break;
                                    } else {
                                        isValidPinCode = BOOLEAN_FALSE;
                                    }
                                }
                            }
                            if (isValidPinCode) {
                                Toast.makeText(DeliverySettings.this, "Pincode Already Exists", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                PincodeDetails pincodeDetails = new PincodeDetails();
                                pincodeDetails.setPinCode(pincodeEditText.getText().toString().trim());
                                pincodeDetails.setCreatedDate(DateUtils.fetchCurrentDateAndTime());
                                pincodeDetails.setPincodeStatus(ACTIVE_STATUS);
                                pincodeDetails.setPincodeId((int) maxid + 1);

                                pincodeRef.child(String.valueOf(maxid + 1)).setValue(pincodeDetails);
                                Toast.makeText(DeliverySettings.this, "Uploaded sucessfully", Toast.LENGTH_SHORT).show();
                                b.cancel();
                            }


                        }

                    }
                });
            }
        });

        pincodeListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                PincodeDetails pincodeDetails = pincodeDetailsArrayList.get(i);
                CharSequence[] items = {"update", "Change Status"};
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(DeliverySettings.this);
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, final int item) {

                        if (item == 0) {


                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DeliverySettings.this);
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.update_pincode, null);
                            dialogBuilder.setView(dialogView);

                            ImageView cancel_dialogPincode = dialogView.findViewById(R.id.cancel_dialogPincode);
                            EditText updatePincode = dialogView.findViewById(R.id.updatepincodeEditText);
                            Button upadteButton = dialogView.findViewById(R.id.updatePincode);


                            final AlertDialog b = dialogBuilder.create();
                            if (!isFinishing()) {
                                b.show();
                            }
                            cancel_dialogPincode.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    b.cancel();
                                }
                            });

                            updatePincode.setText(pincodeDetails.getPinCode());
                            upadteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    if ("".equalsIgnoreCase(updatePincode.getText().toString().trim())) {
                                        updatePincode.setError(REQUIRED);
                                        Toast.makeText(DeliverySettings.this, ""+REQUIRED, Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (!updatePincode.getText().toString().trim().isEmpty() &&
                                            !TextUtils.isValidIndiaPincode(updatePincode.getText().toString().trim())) {
                                        updatePincode.setError("Enter Valid PinCode");
                                        Toast.makeText(DeliverySettings.this, "Enter Valid PinCode", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        if (pincodeDetailsArrayList != null && updatePincode.getText().toString().trim() != null) {
                                            Iterator itemIterator = pincodeDetailsArrayList.iterator();

                                            while (itemIterator.hasNext()) {

                                                PincodeDetails pincodeDetails = (PincodeDetails) itemIterator.next();
                                                if (updatePincode.getText().toString().trim().equalsIgnoreCase(pincodeDetails.getPinCode())) {

                                                    isValidPinCode = BOOLEAN_TRUE;
                                                    break;
                                                } else {
                                                    isValidPinCode = BOOLEAN_FALSE;
                                                }
                                            }
                                        }
                                        if (isValidPinCode) {
                                            Toast.makeText(DeliverySettings.this, "Pincode Already Exists", Toast.LENGTH_SHORT).show();
                                            return;
                                        }else {
                                            pincodeDetails.setPinCode(updatePincode.getText().toString().trim());
                                            pincodeDetails.setCreatedDate(DateUtils.fetchCurrentDateAndTime());

                                            pincodeRef.child(String.valueOf(pincodeDetails.getPincodeId())).setValue(pincodeDetails);
                                            Toast.makeText(DeliverySettings.this, "Uploaded sucessfully", Toast.LENGTH_SHORT).show();
                                            b.cancel();
                                        }


                                    }


                                }
                            });

                        }


                        if (item == 1) {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DeliverySettings.this);
                            builder.setTitle("Confrimation Status")
                                    .setMessage("Are sure, want to Inactive the PinCode ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Remove Restriction", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            pincodeRef.child(String.valueOf(pincodeDetails.getPincodeId())).child("pincodeStatus").setValue(INACTIVE_STATUS);
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setNegativeButton("Add Restriction", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    pincodeRef.child(String.valueOf(pincodeDetails.getPincodeId())).child("pincodeStatus").setValue(ACTIVE_STATUS);

                                }
                            });
                            android.app.AlertDialog dialogBuiler = builder.create();
                            dialogBuiler.show();


                        }


                    }


                });


                dialog.show();


            }

        });


    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
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
        } else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.discounts) {
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
        }/*else if(id==R.id.deliveryfare){
            Intent intent = new Intent(getApplicationContext(), AddDeliveryFareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/ else if (id == R.id.logoutscreen) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DeliverySettings.this);
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
                    Intent intent = new Intent(DeliverySettings.this,
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
                    navigationView.setCheckedItem(R.id.vieworders);
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
        }  else if (id == R.id.reviewApproval) {
            Intent intent = new Intent(getApplicationContext(), ItemRatingReviewApprovalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.legal) {
            Intent intent = new Intent(getApplicationContext(), LegalDetailsUploadActivity.class);
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

        pincodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxid = dataSnapshot.getChildrenCount();

                    pincodeDetailsArrayList.clear();
                    for (DataSnapshot pinCodeSnap : dataSnapshot.getChildren()) {
                        pincodeDetails = pinCodeSnap.getValue(PincodeDetails.class);
                        pincodeDetailsArrayList.add(pincodeDetails);
                    }
                }
                PincodeDetailsAdapter categoryAdapter = new PincodeDetailsAdapter(DeliverySettings.this, pincodeDetailsArrayList);
                categoryAdapter.notifyDataSetChanged();
                pincodeListview.setAdapter(categoryAdapter);

                if (categoryAdapter != null) {
                    int totalHeight = 0;

                    for (int i = 0; i < categoryAdapter.getCount(); i++) {
                        View listItem = categoryAdapter.getView(i, null, pincodeListview);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = pincodeListview.getLayoutParams();
                    params.height = totalHeight + (pincodeListview.getDividerHeight() * (categoryAdapter.getCount() - 1));
                    pincodeListview.setLayoutParams(params);
                    pincodeListview.requestLayout();
                    pincodeListview.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DeliverySettings.this, DashBoardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}