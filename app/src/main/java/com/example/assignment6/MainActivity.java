package com.example.assignment6;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    DataBase myDb;
    TextView balance;
    EditText editDay;
    EditText editPrice;
    EditText filterDayTo;
    Button btnFilter;
    Button btnClearFilter;
    EditText editYear;
    EditText editItem;
    EditText filterDayFrom;
    EditText filterMonthFrom;
    EditText filterMonthTo;
    EditText filterYearTo;
    EditText editMonth;
    EditText filterYearFrom;
    EditText filterPriceFrom;
    EditText filterPriceTo;
    Button btnAdd;
    Button btnSub;
    TableLayout history;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DataBase(this);

        btnFilter =  findViewById(R.id.btnFilter);
        btnClearFilter =  findViewById(R.id.btnClearFilter);
        filterDayFrom =  findViewById(R.id.filterDayFrom);
        balance =  findViewById(R.id.balance);
        filterPriceFrom =  findViewById(R.id.filterPriceFrom);
        filterPriceTo =  findViewById(R.id.filterPriceTo);
        editMonth =  findViewById(R.id.editMonth);
        filterDayTo =  findViewById(R.id.filterDayTo);
        editPrice =  findViewById(R.id.editPrice);
        editItem =  findViewById(R.id.editItem);
        filterMonthFrom =  findViewById(R.id.filterMonthFrom);
        filterYearFrom =  findViewById(R.id.filterYearFrom);
        filterMonthTo =  findViewById(R.id.filterMonthTo);
        filterYearTo =  findViewById(R.id.filterYearTo);
        history = findViewById(R.id.tableHistory);
        editDay =  findViewById(R.id.editDay);
        btnAdd =  findViewById(R.id.btnAdd);
        btnSub =  findViewById(R.id.btnSub);
        editYear =  findViewById(R.id.editYear);
        GetAllHistory();
        AddTransaction();
        Filter();
        ClearFilter();
    }

    public void ClearText(){
        MainActivity.this.editDay.setText("");
        MainActivity.this.editMonth.setText("");
        MainActivity.this.editYear.setText("");
        MainActivity.this.editPrice.setText("");
        MainActivity.this.editItem.setText("");
        MainActivity.this.filterDayFrom.setText("");
        MainActivity.this.filterMonthFrom.setText("");
        MainActivity.this.filterYearFrom.setText("");
        MainActivity.this.filterDayTo.setText("");
        MainActivity.this.filterMonthTo.setText("");
        MainActivity.this.filterYearTo.setText("");
        MainActivity.this.filterPriceFrom.setText("");
        MainActivity.this.filterPriceTo.setText("");
    }

    public void AddTransaction(){
        btnSub.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = -1 * Double.parseDouble(editPrice.getText().toString());
                        TransModel model = new TransModel();
                        String day = editDay.getText().toString();
                        String month = editMonth.getText().toString();
                        String year = editYear.getText().toString();
                        model.mDate =  CreateDate(day, month, year);
                        model.mItem = editItem.getText().toString();
                        model.mPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetAllHistory();
                        ClearText();
                    }
                }
        );
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = Double.parseDouble(editPrice.getText().toString());
                        TransModel model = new TransModel();
                        String day = editDay.getText().toString();
                        String month = editMonth.getText().toString();
                        String year = editYear.getText().toString();
                        model.mDate =  CreateDate(day, month, year);
                        model.mItem = editItem.getText().toString();
                        model.mPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetAllHistory();
                        ClearText();
                    }
                }
        );

    }

    public void GetAllHistory(){
        Cursor result = myDb.getAllData();
        DisplayHistory(result, false);
    }

    public void DisplayHistory(Cursor result, boolean filtered){
        if (result == null){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;
        ClearTable();
        while(result.moveToNext()){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams columnLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            columnLayout.weight = 1;

            TextView date = new TextView(this);
            date.setLayoutParams(columnLayout);
            date.setText(result.getString(2));
            tr.addView(date);

            TextView priceView = new TextView(this);
            priceView.setLayoutParams(columnLayout);
            priceView.setText(result.getString(3));
            tr.addView(priceView);

            TextView item = new TextView(this);
            item.setLayoutParams(columnLayout);
            item.setText(result.getString(1));
            tr.addView(item);

            history.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // get price for balance
            double price = Double.parseDouble(result.getString(3));
            balance += price;
        }
        if (!filtered){
            MainActivity.this.balance.setText("Current Balance: $" + df.format(balance));
        }
    }

    public String CreateDate(String day, String month, String year){
        if (month.isEmpty() || day.isEmpty() || year.isEmpty()) {
            return "";
        }
        else {
            int dayIn = Integer.parseInt(day);
            int monthIn = Integer.parseInt(month);
            if (dayIn < 10 && monthIn >= 10) {
                return year + "-" + month + "-0" + day;
            }
            else if (dayIn >= 10 && monthIn < 10){
                return year + "-0" + month + "-" + day;
            }
            else if (dayIn < 10 && monthIn < 10){
                return year + "-0" + month + "-0" + day;
            }
            else {
                return year + "-" + month + "-" + day;
            }
        }
    }

    public void Filter(){
        btnFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String priceFromString = filterPriceFrom.getText().toString();
                        String priceToString = filterPriceTo.getText().toString();
                        String day = filterDayFrom.getText().toString();
                        String month = filterMonthFrom.getText().toString();
                        String year = filterYearFrom.getText().toString();
                        String dateFrom = CreateDate(day, month, year);
                        day = filterDayTo.getText().toString();
                        month = filterMonthTo.getText().toString();
                        year = filterYearTo.getText().toString();
                        String dateTo = CreateDate(day, month, year);


                        Cursor result = myDb.getFilteredData(priceFromString, priceToString, dateFrom, dateTo);
                        DisplayHistory(result, true);
                    }
                }
        );
    }

    public void ClearFilter(){
        btnClearFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClearText();
                        GetAllHistory();
                    }
                }
        );
    }

    public void ClearTable(){
        int count = history.getChildCount();
        for (int i = 1; i < count; i++) {
            history.removeViewAt(1);
        }
    }
}