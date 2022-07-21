package com.smiligence.techAdmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.smiligence.techAdmin.Adapter.ItemReportsAdapter;
import com.smiligence.techAdmin.Adapter.ReportAdapter;
import com.smiligence.techAdmin.bean.ItemDetails;
import com.smiligence.techAdmin.bean.OrderDetails;
import com.smiligence.techAdmin.bean.StoreDetails;
import com.smiligence.techAdmin.common.CommonMethods;
import com.smiligence.techAdmin.common.DateUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.smiligence.techAdmin.common.Constant.BILLED_DATE_COLUMN;
import static com.smiligence.techAdmin.common.Constant.FORMATED_BILLED_DATE;
import static com.smiligence.techAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;


public class ReportGenerationActivity extends AppCompatActivity {

    Button generate_pdf;

    ArrayList<OrderDetails> reportsList = new ArrayList<OrderDetails>();
    ArrayList<ItemDetails> itemReportList = new ArrayList<ItemDetails>();

    ListView reportsListView;
    ImageView backButton;
    private File pdfFile;
    int todaysTotalQty;
    int todaysItemCount;
    ReportAdapter reportAdapter;
    ItemReportsAdapter itemReportsAdapter;
    DatabaseReference billdataref, storeNameRef, dbref;
    StorageReference storeNameStorage;
    StoreDetails storeDetails = new StoreDetails();
    Spinner dropdown;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    final boolean[] onDataChangeCheck = {false};
    HashMap<String, Integer> itemDetailsHashMap = new HashMap<>();
    ItemDetails itemDetails;
    ArrayList<Integer> itemQuantityList = new ArrayList<>();
    int itemQuantity;
    Map<String, String[]> itemDetailsMap = new HashMap<String, String[]>();
    String[] itemQuantityBillCountList = new String[]{"quantity", "BillCount"};
    String temp;
    int billCountInt;
    String qtyString;
    String indicator;
    LinearLayout headerReportLinearLayout, headerItemReportLinearLayout;

    ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generation);

        indicator = getIntent().getStringExtra("Indicator");

        reportsListView = findViewById(R.id.reportsListView);
        generate_pdf = findViewById(R.id.generate_pdf);
        backButton = findViewById(R.id.btn_back);
        headerReportLinearLayout = findViewById(R.id.header_report);
        headerItemReportLinearLayout = findViewById(R.id.header_itemreport);
        if ("2".equalsIgnoreCase(indicator)) {
            headerReportLinearLayout.setVisibility(View.INVISIBLE);
            headerItemReportLinearLayout.setVisibility(View.VISIBLE);
        } else if ("1".equalsIgnoreCase(indicator)) {
            headerReportLinearLayout.setVisibility(View.VISIBLE);
            headerItemReportLinearLayout.setVisibility(View.INVISIBLE);
        }


        dropdown = findViewById(R.id.reportSpinner);
        String[] reportPeriodDropDown = new String[]{"Today", "Yesterday", "Last 7 days", "Last 30 days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reportPeriodDropDown);

        dropdown.setAdapter(adapter);
        billdataref = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dashboard = new Intent(ReportGenerationActivity.this, DashBoardActivity.class);
                intent_dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_dashboard);
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportsList.clear();
                itemReportList.clear();

                switch (position) {
                    case 0:
                        final Query todayQuery = billdataref.orderByChild(FORMATED_BILLED_DATE).
                                equalTo(DateUtils.fetchCurrentDate());
                        fetchBillDetails(todayQuery);
                        break;
                    case 1:

                        final Query yesterdayQuery = billdataref.orderByChild(FORMATED_BILLED_DATE)
                                .equalTo(DateUtils.fetchYesterdayDate());
                        fetchBillDetails(yesterdayQuery);
                        break;
                    case 2:

                        final Query last7daysQuery = billdataref.orderByChild(FORMATED_BILLED_DATE)
                                .startAt(DateUtils.fetchLast7days()).endAt(DateUtils.fetchTommorow());
                        System.out.println("nnnnn7 " + DateUtils.fetchLast7days());
               /*         for(int i=0;i<7;i++){
                            LocalDate now=LocalDate.now();
                            LocalDate lastSeven =now.minusDays(i-1);
                            String lastSevenDays = lastSeven.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));


                        }*/

                        fetchBillDetails(last7daysQuery);
                        break;
                    case 3:

                        String last30Date = DateUtils.fetchLast30Days();

                        final Query last30daysQuery = billdataref.orderByChild(FORMATED_BILLED_DATE)
                                .startAt(last30Date).endAt(DateUtils.fetchTommorow());
                        System.out.println("nnnnn30 " + DateUtils.fetchLast30Days());
                        fetchBillDetails(last30daysQuery);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                itemReportList.clear();
                dropdown.setSelection(0);
                final Query todayQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
                fetchBillDetails(todayQuery);
            }
        });

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reportsList == null || reportsList.isEmpty() && itemReportList == null || itemReportList.isEmpty()) {
                    Toast.makeText(ReportGenerationActivity.this, "No datas found", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        createPdfWrapper();

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi...");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Reports From FRIO");
                        File root = Environment.getExternalStorageDirectory();
                        String pathToMyAttachedFile = "/Download/ceyonReports.pdf";
                        File file = new File(root, pathToMyAttachedFile);
                        Uri uri = Uri.fromFile(file);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();

                    }

                }

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dropdown.setSelection(0);
        reportsList.clear();
        itemReportList.clear();
        final Query billDetailsQuery = billdataref.orderByChild(FORMATED_BILLED_DATE).equalTo(DateUtils.fetchCurrentDate());

        fetchBillDetails(billDetailsQuery);
    }

    private void fetchBillDetails(Query billDetailsQuery) {


        billDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                reportsList.clear();
                billDetailsArrayList.clear();
                itemDetailsHashMap.clear();
                itemDetailsMap.clear();
                itemQuantityList.clear();
                itemReportList.clear();


                if (reportAdapter != null) {
                    reportAdapter.clear();

                }
                if (itemReportsAdapter != null) {
                    itemReportList.clear();
                }

                for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {
                    billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));
                    System.out.println("billDetailsArrayList "+billDetailsArrayList.size());
                    onDataChangeCheck[0] = true;
                }

                if (onDataChangeCheck[0] == true) {

                    Iterator billIterator = billDetailsArrayList.iterator();


                    while (billIterator.hasNext()) {

                        todaysTotalQty = 0;
                        todaysItemCount = 0;
                        itemQuantityList.clear();
                        billCountInt = 0;
                        itemReportList.clear();

                        OrderDetails billDetails = (OrderDetails) billIterator.next();

                        Iterator itemIterator = billDetails.getItemDetailList().iterator();

                        while (itemIterator.hasNext()) {

                            itemDetails = (ItemDetails) itemIterator.next();
                            todaysTotalQty = todaysTotalQty + itemDetails.getItemBuyQuantity();
                            todaysItemCount = todaysItemCount + 1;

                            if (!itemDetailsHashMap.isEmpty()) {


                                if (itemDetailsHashMap.containsKey(itemDetails.getItemName())) {

                                    itemQuantity = itemDetailsHashMap.get(itemDetails.getItemName());
                                    itemQuantity = itemQuantity + itemDetails.getItemBuyQuantity();

                                    qtyString = String.valueOf(itemQuantity);

                                    billCountInt = Integer.parseInt(itemDetailsMap.get(itemDetails.getItemName())[1]) + 1;
                                    itemQuantityBillCountList = new String[]{String.valueOf(itemQuantity), String.valueOf(billCountInt)};

                                    itemDetailsMap.put(itemDetails.getItemName(), itemQuantityBillCountList);

                                    itemDetailsHashMap.put(itemDetails.getItemName(), itemQuantity);

                                    temp = itemDetails.getItemName();

                                } else {

                                    itemDetailsHashMap.put(itemDetails.getItemName(), itemDetails.getItemBuyQuantity());

                                    qtyString = String.valueOf(itemDetails.getItemBuyQuantity());

                                    if (temp.equalsIgnoreCase(itemDetails.getItemName())) {
                                        billCountInt = 0;
                                        itemQuantityBillCountList = new String[]{qtyString, String.valueOf(billCountInt)};

                                    } else {
                                        billCountInt = 1;
                                        itemQuantityBillCountList = new String[]{qtyString, String.valueOf(billCountInt)};

                                    }

                                    itemDetailsMap.put(itemDetails.getItemName(), itemQuantityBillCountList);

                                }

                            } else {

                                billCountInt = billCountInt + 1;
                                qtyString = String.valueOf(itemDetails.getItemBuyQuantity());

                                itemQuantityBillCountList = new String[]{qtyString, String.valueOf(billCountInt)};

                                itemDetailsMap.put(itemDetails.getItemName(), itemQuantityBillCountList);

                                itemDetailsHashMap.put(itemDetails.getItemName(), itemDetails.getItemBuyQuantity());

                                temp = itemDetails.getItemName();

                            }

                        }

                        OrderDetails newbillDetails = new OrderDetails();
                        newbillDetails.setOrderId(billDetails.getOrderId());
                        newbillDetails.setOrderId((billDetails.getOrderId()));
                        newbillDetails.setPaymentDate(billDetails.getPaymentDate());
                        newbillDetails.setDiscountAmount(billDetails.getDiscountAmount());
                        newbillDetails.setPaymentamount(billDetails.getPaymentamount());
                        newbillDetails.setTotalItem(todaysTotalQty);

                        ItemDetails newitemDetails = new ItemDetails();

                        newitemDetails.setItemBuyQuantity(todaysItemCount);
                        newbillDetails.setItemDetails(newitemDetails);
                        reportsList.add(newbillDetails);

                        System.out.println("LIISt " + reportsList.size());

                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                     //   Collections.sort(reportsList);
                    }


                    Iterator listIterator = itemDetailsMap.keySet().iterator();


                    while (listIterator.hasNext()) {
                        String key = (String) listIterator.next();
                        if (itemDetailsMap.containsKey(key)) {
                            ItemDetails itemDetailsFinal = new ItemDetails();
                            itemDetailsFinal.setItemName(key);
                           // itemDetailsFinal.setBillCount(Integer.parseInt(itemDetailsMap.get(key)[1]));
                            itemDetailsFinal.setItemBuyQuantity(Integer.parseInt(itemDetailsMap.get(key)[0]));

                            itemReportList.add(itemDetailsFinal);

                        }


                    }
                    if ("2".equalsIgnoreCase(indicator)) {

                        itemReportsAdapter = new ItemReportsAdapter(ReportGenerationActivity.this, itemReportList);
                        reportsListView.setAdapter(itemReportsAdapter);
                    }

                    if ("1".equalsIgnoreCase(indicator)) {

                        reportAdapter = new ReportAdapter(ReportGenerationActivity.this, reportsList);
                        reportsListView.setAdapter(reportAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createPdfWrapper() throws FileNotFoundException, IOException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else
            createPdf();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, IOException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();

        }
        File docsFolder1 = new File(Environment.getExternalStorageDirectory() + "/Save");
        if (!docsFolder1.exists()) {
            docsFolder1.mkdir();

        }
        pdfFile = new File(docsFolder.getAbsolutePath(), "ceyonReports.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, output);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
        try {


            Bitmap bm_header = null;


            /*if (storeDetails.getBusinessType() == null || storeDetails.getBusinessType().equals("")) {

                bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_others);

                ByteArrayOutputStream stream_header = new ByteArrayOutputStream();
                bm_header.compress(Bitmap.CompressFormat.JPEG, 50, stream_header);
                Image img_header = null;
                byte[] byteArray_header = stream_header.toByteArray();
                try {
                    img_header = Image.getInstance(byteArray_header);
                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                img_header.setAbsolutePosition(0, 650);
                img_header.scaleAbsolute(PageSize.A4.getWidth(), 200);
                img_header.setScaleToFitLineWhenOverflow(true);

                try {
                    document.add(img_header);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }
                img_header.setAbsolutePosition(0, 650);
                img_header.scaleAbsolute(PageSize.A4.getWidth(), 200);
                img_header.setScaleToFitLineWhenOverflow(true);

                try {
                    document.add(img_header);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }

            } else {

                if (storeDetails.getBusinessType().equals("Ice Cream")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_icecream);
                } else if (storeDetails.getBusinessType().equals("Burger Shop")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_fastfood);
                } else if (storeDetails.getBusinessType().equals("Coffee Shop")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_coffee);
                } else if (storeDetails.getBusinessType().equals("Biriyani")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_biriyani);
                } else if (storeDetails.getBusinessType().equals("Grilled chicken Shop")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_biriyani);
                } else if (storeDetails.getBusinessType().equals("Bakery")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_bakery);
                } else if (storeDetails.getBusinessType().equals("Pizza")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_pizza);
                } else if (storeDetails.getBusinessType().equals("kids Boutique")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_kids);
                } else if (storeDetails.getBusinessType().equals("Boutique for Girls")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_boutique);
                } else if (storeDetails.getBusinessType().equals("Momos Shop")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_momos);
                } else if (storeDetails.getBusinessType().equals("veg Restaurant")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_veg_restaurant);
                } else if (storeDetails.getBusinessType().equals("Sweet Shop")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_sweets);
                } else if (storeDetails.getBusinessType().equals("Others")) {
                    bm_header = BitmapFactory.decodeResource(getResources(), R.drawable.header_others);
                }

                ByteArrayOutputStream stream_header = new ByteArrayOutputStream();
                bm_header.compress(Bitmap.CompressFormat.JPEG, 50, stream_header);
                Image img_header = null;
                byte[] byteArray_header = stream_header.toByteArray();
                try {
                    img_header = Image.getInstance(byteArray_header);
                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                img_header.setAbsolutePosition(0, 650);
                img_header.scaleAbsolute(PageSize.A4.getWidth(), 200);
                img_header.setScaleToFitLineWhenOverflow(true);

                try {
                    document.add(img_header);
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }
            }

            Paragraph spacing = new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n");
            document.add(spacing);*/

            if (storeDetails.getStoreLogo() != null && !"".equals(storeDetails.getStoreLogo())) {
                try {

                    StrictMode.ThreadPolicy logopolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(logopolicy);
                    URL logourl = new URL(storeDetails.getStoreLogo());

                    Bitmap bm_logo = BitmapFactory.decodeStream((InputStream) logourl.getContent());
                    ByteArrayOutputStream stream_logo = new ByteArrayOutputStream();
                    bm_logo.compress(Bitmap.CompressFormat.JPEG, 50, stream_logo);
                    Image img_logo = null;
                    byte[] byteArray_logo = stream_logo.toByteArray();
                    try {
                        img_logo = Image.getInstance(byteArray_logo);

                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    img_logo.scaleToFit(80, 80);
                    img_logo.setAlignment(Element.ALIGN_CENTER);

                    Paragraph logo = new Paragraph();
                    logo.add(img_logo);
                    document.add(logo);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (storeDetails.getStoreName() != null && !"".equals(storeDetails.getStoreName())) {
                String storeName = storeDetails.getStoreName();
                int length = storeName.length();
                Font f;
                Chunk c;
                ;
                Paragraph leftname;
                f = new Font(Font.FontFamily.TIMES_ROMAN, 18.0f, Font.BOLD, BaseColor.BLACK);
                c = new Chunk(storeName + "\n", f);
                leftname = new Paragraph(c);
                leftname.setAlignment(Element.ALIGN_TOP);

                leftname.setAlignment(Element.ALIGN_CENTER);
                leftname.setIndentationLeft(2);
                document.add(leftname);


                Font newf = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
                Chunk chunk = new Chunk(storeDetails.getStoreAddress() + " " + storeDetails.getZipCode() + "\n", newf);
                Font newfont = new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.BOLD, BaseColor.BLACK);


                Paragraph residence = new Paragraph(chunk);

                residence.setAlignment(Element.ALIGN_CENTER);
                residence.setIndentationLeft(2);


                document.add(residence);


            } else if (storeDetails.getStoreName() == null || "".equals(storeDetails.getStoreName())) {

                Font f = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c = new Chunk("" + "\n", f);
                Font newf = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
                Chunk chunk = new Chunk("" + "\n", newf);
                Font newfont = new Font(Font.FontFamily.TIMES_ROMAN, 15.0f, Font.BOLD, BaseColor.BLACK);

                Paragraph leftname = new Paragraph(c);
                leftname.setAlignment(Element.ALIGN_TOP);

                leftname.setAlignment(Element.ALIGN_CENTER);
                Paragraph residence = new Paragraph(chunk);
                residence.setAlignment(Element.ALIGN_CENTER);
                document.add(leftname);
                document.add(residence);
            }
            if (storeDetails.getGstNumber() != null && !"".equals(storeDetails.getGstNumber())) {
                Font fontGst = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
                Chunk chunckGst = new Chunk("GST NO:" + storeDetails.getGstNumber() + "\n", fontGst);
                Paragraph gst = new Paragraph(chunckGst);
                gst.setAlignment(Element.ALIGN_CENTER);
                document.add(gst);
            } else if (storeDetails.getGstNumber() == null && "".equals(storeDetails.getGstNumber())) {
                Font fontGst = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
                Chunk chunckGst = new Chunk("" + "\n", fontGst);
                Paragraph gst = new Paragraph(chunckGst);
                gst.setAlignment(Element.ALIGN_CENTER);
                document.add(gst);
            }
            if ("1".equalsIgnoreCase(indicator)) {
                Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
                Font bfBold1 = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, new BaseColor(0, 0, 0));
                Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
                Chunk glue = new Chunk(new VerticalPositionMark());
                DecimalFormat df = new DecimalFormat("0");


                Paragraph spaceshell = new Paragraph("\n");
                document.add(spaceshell);

                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 19.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c_font = new Chunk("SALES REPORTS" + "\n" + "\n", font);
                Paragraph name = new Paragraph(c_font);
                name.setAlignment(Element.ALIGN_TOP);
                name.setAlignment(Element.ALIGN_CENTER);
                document.add(name);

                float[] columnWidths = {22f, 29f, 27f, 27f, 37f, 37f};
                PdfPTable table = new PdfPTable(columnWidths);
                table.setWidthPercentage(90f);
                insertCell(table, "BILL NO", Element.ALIGN_LEFT, 1, bfBold12);
                insertCell(table, "BILL DATE", Element.ALIGN_LEFT, 1, bfBold12);
                insertCell(table, "ITEM", Element.ALIGN_LEFT, 1, bfBold12);
                insertCell(table, "ITEM QUANTITY", Element.ALIGN_LEFT, 1, bfBold12);
                insertCell(table, "DISCOUNT AMOUNT", Element.ALIGN_LEFT, 1, bfBold12);
                insertCell(table, "BILL AMOUNT", Element.ALIGN_LEFT, 1, bfBold12);

                table.setHeaderRows(1);

                Iterator billDetailsIterator = reportsList.iterator();

                while (billDetailsIterator.hasNext()) {
                    OrderDetails billDetails = (OrderDetails) billDetailsIterator.next();

                    insertCell(table, (billDetails.getOrderId()), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, (billDetails.getPaymentDate()), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, String.valueOf(billDetails.getTotalItem()), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, String.valueOf(billDetails.getItemDetails().getItemBuyQuantity()), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, String.valueOf(billDetails.getDiscountAmount()), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, String.valueOf(billDetails.getPaymentamount()), Element.ALIGN_CENTER, 1, bf12);
                }

                Paragraph pv = new Paragraph("");
                pv.add(table);
                pv.setAlignment(Element.ALIGN_CENTER);
                document.add(pv);
            } else if ("2".equalsIgnoreCase(indicator)) {
                Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
                Font bfBold1 = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, new BaseColor(0, 0, 0));
                Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
                Chunk glue = new Chunk(new VerticalPositionMark());
                DecimalFormat df = new DecimalFormat("0");


                Paragraph spaceshell = new Paragraph("\n");
                document.add(spaceshell);

                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 19.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c_font = new Chunk("ITEM REPORTS" + "\n" + "\n", font);
                Paragraph name = new Paragraph(c_font);
                name.setAlignment(Element.ALIGN_TOP);
                name.setAlignment(Element.ALIGN_CENTER);
                document.add(name);


                float[] columnWidths = {20f, 15f, 20f};
                PdfPTable table = new PdfPTable(columnWidths);
                table.setWidthPercentage(90f);
                insertCell(table, "ITEM NAME", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "NO OF BILLS", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "ITEM QUANTITY", Element.ALIGN_CENTER, 1, bfBold12);


                table.setHeaderRows(1);

                Iterator billDetailsIterator = itemReportList.iterator();

                while (billDetailsIterator.hasNext()) {
                    ItemDetails itemDetails = (ItemDetails) billDetailsIterator.next();

                    insertCell(table, (itemDetails.getItemName()), Element.ALIGN_CENTER, 1, bf12);
                  //  insertCell(table, (String.valueOf(itemDetails.getBillCount())), Element.ALIGN_CENTER, 1, bf12);
                    insertCell(table, String.valueOf(itemDetails.getItemBuyQuantity()), Element.ALIGN_CENTER, 1, bf12);

                }

                Paragraph pv = new Paragraph("");
                pv.add(table);
                pv.setAlignment(Element.ALIGN_CENTER);
                document.add(pv);
            }


            Font newfonts = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);

            Chunk chunknew = new Chunk("POWERED BY SMILIGENCE", newfonts);

            Paragraph Smiligence = new Paragraph(chunknew);
            Smiligence.setAlignment(Element.ALIGN_BOTTOM);
            Smiligence.setAlignment(Element.ALIGN_CENTER);
            document.add(Smiligence);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();

        previewPdf();
    }

    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");

        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);

        } else {
            Toast.makeText(this, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }

    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);

        cell.setColspan(colspan);
        table.getDefaultCell().setBorderColor(BaseColor.WHITE);
        cell.setBorderColor(BaseColor.BLACK);

        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(25f);
        }

        table.addCell(cell);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReportGenerationActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}