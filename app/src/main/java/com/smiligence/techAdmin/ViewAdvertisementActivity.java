package com.smiligence.techAdmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.techAdmin.Adapter.Advertisementadapter;
import com.smiligence.techAdmin.bean.AdvertisementDetails;
import com.smiligence.techAdmin.common.CommonMethods;

import java.util.ArrayList;

import static com.smiligence.techAdmin.common.Constant.ACTIVE_STATUS;
import static com.smiligence.techAdmin.common.Constant.ADVERTISEMT_DETAILS_FIREBASE_TABLE;
import static com.smiligence.techAdmin.common.Constant.INACTIVE_STATUS;

public class ViewAdvertisementActivity extends AppCompatActivity {
RecyclerView recyclerView;
DatabaseReference advertisementRef;
ArrayList<AdvertisementDetails> advertisementDetailsList=new ArrayList<>();
Advertisementadapter advertisementAdapter;
    AdvertisementDetails advertisementDetails;
    ImageView backButton;
    TextView reviewCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advertisement);
        backButton=findViewById(R.id.redirecttohome);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(ViewAdvertisementActivity.this, 1));
        recyclerView.setHasFixedSize(true);




        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewAdvertisementActivity.this, AddAdvertisementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        advertisementRef= CommonMethods.fetchFirebaseDatabaseReference(ADVERTISEMT_DETAILS_FIREBASE_TABLE);

        advertisementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    advertisementDetailsList.clear();
                    for(DataSnapshot advertisementSnap:dataSnapshot.getChildren()){
                         advertisementDetails=advertisementSnap.getValue(AdvertisementDetails.class);
                        advertisementDetailsList.add(advertisementDetails);
                    }

                    if (advertisementDetailsList!=null) {
                        advertisementAdapter = new Advertisementadapter(ViewAdvertisementActivity.this, advertisementDetailsList);
                        advertisementAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(advertisementAdapter);
                    }

                    if (advertisementAdapter != null) {
                        advertisementAdapter.setOnItemclickListener(new Advertisementadapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                advertisementDetails=advertisementDetailsList.get(Position);


                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ViewAdvertisementActivity.this);
                                builder.setTitle("Confrimation Status")
                                        .setMessage("Are sure, want to Inactive the Banner ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Inactive", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                advertisementRef.child(String.valueOf(advertisementDetails.getId())).child("advertisementstatus").setValue(INACTIVE_STATUS);
                                                Intent intent = new Intent(ViewAdvertisementActivity.this, ViewAdvertisementActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).setNegativeButton("Active", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        advertisementRef.child(String.valueOf(advertisementDetails.getId())).child("advertisementstatus").setValue(ACTIVE_STATUS);
                                        Intent intent = new Intent(ViewAdvertisementActivity.this, ViewAdvertisementActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                });
                                android.app.AlertDialog dialogBuiler = builder.create();
                                dialogBuiler.show();


                            }
                        });

                    }

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
        Intent intent = new Intent(ViewAdvertisementActivity.this, DashBoardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}