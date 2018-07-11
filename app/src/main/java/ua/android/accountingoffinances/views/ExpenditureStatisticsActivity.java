package ua.android.accountingoffinances.views;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ua.android.accountingoffinances.R;
import ua.android.accountingoffinances.database.DBExpenseAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.model.Expense;
import ua.android.accountingoffinances.util.Constants;
import ua.android.accountingoffinances.util.LoadExpensesAsyncTask;

/** Class responsible for work xpenditureStaticsActivity, and create the table in it.*/

public class ExpenditureStatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    int mode;
    long currentTime = System.currentTimeMillis();
    ArrayList<Category> categories;
    ArrayList<ArrayMap<Category, Double>> sumsArrayList;

    Spinner spinner;
    RelativeLayout rlMonthSwitcher;
    TextView tvMonth;
    ImageView imvSwitchMonthLeft;
    ImageView imvSwitchMonthRight;
    LinearLayout llDayChooser;
    TextView tvDay;
    TableLayout tableLayout;
    TextView tvSumTitle;
    TextView tvTotalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar_activity_expenditure_statics);
        setSupportActionBar(toolbar);

        mode = (int) getIntent().getSerializableExtra(Constants.ID_PUT_EXTRA);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tvSumTitle = findViewById(R.id.sum_by_period_title_activity_expenditure_statics);
        tvTotalSum = findViewById(R.id.sum_value_activity_expenditure_statics);

        spinner = findViewById(R.id.spinner_activity_expenditure_statics);
        setSpinner();

        tableLayout = findViewById(R.id.tableLayout_activity_expenditure_statics);
        tvMonth = findViewById(R.id.month_switcher_title_activity_expenditure_statics);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void fillData(ArrayList<Category> categories,
                         ArrayList<ArrayMap<Category, Double>> sumsArrayList,
                         ArrayMap<Category, Double> sumsByCategoryMap,
                         ArrayMap<Integer, Double> sumsByDayMap, long dateInMills){
        if (mode == Constants.MONTH_STATISTICS){
            if (llDayChooser != null){
                llDayChooser.setVisibility(View.GONE);
            }
            rlMonthSwitcher = findViewById(R.id.month_switcher_activity_expenditure_statics);
            rlMonthSwitcher.setVisibility(View.VISIBLE);

            imvSwitchMonthLeft = findViewById(R.id.month_switcher_arrow_left_activity_expenditure_statics);
            imvSwitchMonthRight = findViewById(R.id.month_switcher_arrow_right_activity_expenditure_statics);

            imvSwitchMonthLeft.setOnClickListener(this);
            imvSwitchMonthRight.setOnClickListener(this);

            setMonth();
        }else {
            if (rlMonthSwitcher != null){
                rlMonthSwitcher.setVisibility(View.GONE);
            }
            llDayChooser = findViewById(R.id.day_chooser_ll_activity_expenditure_statics);
            llDayChooser.setVisibility(View.VISIBLE);
            tvDay = findViewById(R.id.day_chooser_activity_expenditure_statics);
            tvDay.setPaintFlags(tvDay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvDay.setOnClickListener(this);
            setDate();
        }

        if (setSum(dateInMills)) {
            if (mode == Constants.MONTH_STATISTICS){
                setExpensesByMonth(categories, sumsArrayList, sumsByCategoryMap, sumsByDayMap);
            }else {
                setExpensesByDay(sumsArrayList);
            }
        }else {
            tableLayout.removeAllViews();
            Toast.makeText(this, R.string.no_expenses, Toast.LENGTH_SHORT).show();
        }
    }

    public void setSpinner(){
        String[] data = {getString(R.string.per_month), getString(R.string.per_day)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, data);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner.setAdapter(adapter);
        if (mode == Constants.MONTH_STATISTICS){
            spinner.setSelection(0);
        }else {
            spinner.setSelection(1);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        mode = Constants.MONTH_STATISTICS;
                        break;
                    case 1:
                        mode = Constants.DAY_STATISTICS;
                        break;
                }
                currentTime = System.currentTimeMillis();
                new LoadExpensesAsyncTask(ExpenditureStatisticsActivity.this, mode, currentTime).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void setExpensesByMonth(final ArrayList<Category> categories,
                                   ArrayList<ArrayMap<Category, Double>> sumsArrayList,
                                   ArrayMap<Category, Double> sumsByCategoryMap,
                                   ArrayMap<Integer, Double> sumsByDayMap) {
        this.sumsArrayList = sumsArrayList;
        this.categories = categories;
        int rows = sumsArrayList.size();
        tableLayout.removeAllViews();

        setTableHeaderForMonth(categories);

        for (int i = 1; i <= rows; i++) {
            ArrayMap<Category, Double> tmpSumsMap = sumsArrayList.get(i-1);

            if (tmpSumsMap.size()>0) {

                TableRow tableRow = new TableRow(this);
                ArrayList<TextView> tvSumsList = new ArrayList<>(categories.size());

                // CREATE PARAM FOR MARGINING
                TableRow.LayoutParams lParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                lParams.topMargin = 3;
                lParams.rightMargin = 3;

                // CREATE TEXTVIEW
                TextView tvDate = new TextView(this);
                TextView tvSumPerDay = new TextView(this);

                final Calendar date = Calendar.getInstance();
                date.setTimeInMillis(currentTime);
                date.set(Calendar.DAY_OF_MONTH, i);

                for (final Category category : categories) {
                    Double sum = 0.0;
                    if (tmpSumsMap.containsKey(category)) {
                        sum = tmpSumsMap.get(category);
                    }
                    TextView tvSum = new TextView(this);
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    String sumString = format.format(sum);
                    if (sum != 0) {
                        tvSum.setTypeface(tvSum.getTypeface(), Typeface.BOLD);
                    }
                    tvSum.setText(sumString);
                    if (sum != 0) {
                        tvSum.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(ExpenditureStatisticsActivity.this);
                                Expense expense = dbExpenseAdapter.getExpenseByCategoryByDay(date.getTimeInMillis(), category.getId());
                                Intent intent = new Intent(ExpenditureStatisticsActivity.this, AddNewExpensesActivity.class);
                                intent.putExtra(AddNewExpensesActivity.ID_WHAT_TO_DO_WITH_EXPENSE, AddNewExpensesActivity.CHANGE_EXPENSE);
                                intent.putExtra(AddNewExpensesActivity.EXPENSE_ID, expense);
                                startActivityForResult(intent, 1);
                                return true;
                            }
                        });
                    }
                    tvSumsList.add(tvSum);
                }

                // SET PARAMS
                tvDate.setLayoutParams(lParams);
                for (TextView tvCategory : tvSumsList) {
                    tvCategory.setLayoutParams(lParams);
                }
                tvSumPerDay.setLayoutParams(lParams);

                // SET GRAVITY CENTER
                for (TextView tvCategory : tvSumsList) {
                    tvCategory.setGravity(Gravity.CENTER);
                }
                tvSumPerDay.setGravity(Gravity.CENTER);

                // SET BACKGROUND COLOR
                tvDate.setBackgroundColor(Color.WHITE);
                for (TextView tvCategory : tvSumsList) {
                    tvCategory.setBackgroundColor(Color.WHITE);
                }
                tvSumPerDay.setBackgroundColor(Color.WHITE);

                // SET TEXTVIEW TEXT
                String dateString = DateFormat.format(getString(R.string.date_format), new Date(date.getTimeInMillis())).toString();
                tvDate.setText(dateString);
                if (sumsByDayMap.containsKey(i)){
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    String sumString = format.format(sumsByDayMap.get(i));
                    tvSumPerDay.setText(sumString);
                }
                tvSumPerDay.setTypeface(tvSumPerDay.getTypeface(), Typeface.BOLD);

                // SET PADDING
                tvDate.setPadding(20, 20, 20, 20);
                for (TextView tvCategory : tvSumsList) {
                    tvCategory.setPadding(20, 20, 20, 20);
                }
                tvSumPerDay.setPadding(20, 20, 20, 20);

                // ADD TEXTVIEW TO TABLEROW
                tableRow.addView(tvDate);
                for (TextView tvSum : tvSumsList) {
                    tableRow.addView(tvSum);
                }
                tableRow.addView(tvSumPerDay);

                // ADD TABLEROW TO TABLELAYOUT
                tableLayout.addView(tableRow);
            }
        }

        setTableFooter(sumsByCategoryMap);
    }

    public void setExpensesByDay(ArrayList<ArrayMap<Category, Double>> sumsArrayList) {
        this.sumsArrayList = sumsArrayList;
        ArrayMap<Category, Double> sumsMap = sumsArrayList.get(0);

        tableLayout.removeAllViews();
        setTableHeaderForDay();

        for(ArrayMap.Entry<Category, Double> entry : sumsMap.entrySet()) {
            final Category category = entry.getKey();
            Double tmpSum = entry.getValue();

            if (sumsMap.size()>0) {

                TableRow tableRow = new TableRow(this);

                // CREATE PARAM FOR MARGINING
                TableRow.LayoutParams lParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                lParams.topMargin = 3;
                lParams.rightMargin = 3;

                // CREATE TEXTVIEW
                TextView tvCategory = new TextView(this);
                TextView tvSum = new TextView(this);

                tvSum.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(ExpenditureStatisticsActivity.this);
                        Expense expense = dbExpenseAdapter.getExpenseByCategoryByDay(currentTime, category.getId());
                        Intent intent = new Intent(ExpenditureStatisticsActivity.this, AddNewExpensesActivity.class);
                        intent.putExtra(AddNewExpensesActivity.ID_WHAT_TO_DO_WITH_EXPENSE, AddNewExpensesActivity.CHANGE_EXPENSE);
                        intent.putExtra(AddNewExpensesActivity.EXPENSE_ID, expense);
                        startActivityForResult(intent, 1);
                        return true;
                    }
                });

                // SET PARAMS
                tvSum.setLayoutParams(lParams);
                tvCategory.setLayoutParams(lParams);

                // SET GRAVITY CENTER
                tvCategory.setGravity(Gravity.CENTER);
                tvSum.setGravity(Gravity.CENTER);

                // SET BACKGROUND COLOR
                tvCategory.setBackgroundColor(Color.WHITE);
                tvSum.setBackgroundColor(Color.WHITE);

                // SET TEXTVIEW TEXT
                NumberFormat format = NumberFormat.getCurrencyInstance();
                String sumString = format.format(tmpSum);
                tvSum.setText(sumString);

                tvCategory.setText(category.getName());

                // SET PADDING
                tvSum.setPadding(20, 20, 20, 20);
                tvCategory.setPadding(20, 20, 20, 20);

                // ADD TEXTVIEW TO TABLEROW
                tableRow.addView(tvCategory);
                tableRow.addView(tvSum);

                // ADD TABLEROW TO TABLELAYOUT
                tableLayout.addView(tableRow);
            }
        }
    }

    private void setTableHeaderForMonth(ArrayList<Category> categories){
        TableRow tableHeader = new TableRow(this);
        ArrayList<TextView> tvCategoryList = new ArrayList<>(categories.size());

        // CREATE PARAM FOR MARGINING
        TableRow.LayoutParams lParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        lParams.topMargin = 3;
        lParams.rightMargin = 3;

        // CREATE TEXTVIEW
        TextView tvDateHeader = new TextView(this);
        for (Category category: categories){
            TextView tvCategoryHeader = new TextView(this);
            tvCategoryHeader.setText(category.getName());
            tvCategoryList.add(tvCategoryHeader);
        }
        TextView tvDaySumHeader = new TextView(this);

        // SET GRAVITY CENTER
        tvDateHeader.setGravity(Gravity.CENTER);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setGravity(Gravity.CENTER);
        }
        tvDaySumHeader.setGravity(Gravity.CENTER);


        // SET BOLD TYPEFACE
        tvDateHeader.setTypeface(tvDateHeader.getTypeface(), Typeface.BOLD);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setTypeface(tvCategory.getTypeface(), Typeface.BOLD);
        }
        tvDaySumHeader.setTypeface(tvDaySumHeader.getTypeface(), Typeface.BOLD);

        // SET PARAMS
        tvDateHeader.setLayoutParams(lParams);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setLayoutParams(lParams);
        }
        tvDaySumHeader.setLayoutParams(lParams);


        // SET BACKGROUND COLOR
        tvDateHeader.setBackgroundColor(Color.WHITE);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setBackgroundColor(Color.WHITE);
        }
        tvDaySumHeader.setBackgroundColor(Color.WHITE);


        // SET PADDING
        tvDateHeader.setPadding(20, 20, 20, 20);
        for (TextView tvCategory: tvCategoryList) {
            tvCategory.setPadding(20, 20, 20, 20);
        }
        tvDaySumHeader.setPadding(20, 20, 20, 20);


        // SET TEXTVIEW TEXT
        tvDateHeader.setText(getString(R.string.textView_data));
        tvDaySumHeader.setText(getString(R.string.sum_per_day));

        // ADD TEXTVIEW TO TABLEROW
        tableHeader.addView(tvDateHeader);
        for (TextView tvCategory: tvCategoryList) {
            tableHeader.addView(tvCategory);
        }
        tableHeader.addView(tvDaySumHeader);


        // ADD TABLEROW TO TABLELAYOUT
        tableLayout.addView(tableHeader);
    }

    private void setTableHeaderForDay(){
        TableRow tableHeader = new TableRow(this);

        // CREATE PARAM FOR MARGINING
        TableRow.LayoutParams lParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        lParams.topMargin = 3;
        lParams.rightMargin = 3;

        // CREATE TEXTVIEW
        TextView tvCategoryHeader = new TextView(this);
        TextView tvSumHeader = new TextView(this);

        // SET GRAVITY CENTER
        tvSumHeader.setGravity(Gravity.CENTER);
        tvCategoryHeader.setGravity(Gravity.CENTER);

        // SET BOLD TYPEFACE
        tvCategoryHeader.setTypeface(tvCategoryHeader.getTypeface(), Typeface.BOLD);
        tvSumHeader.setTypeface(tvSumHeader.getTypeface(), Typeface.BOLD);

        // SET PARAMS
        tvCategoryHeader.setLayoutParams(lParams);
        tvSumHeader.setLayoutParams(lParams);

        // SET BACKGROUND COLOR
        tvCategoryHeader.setBackgroundColor(Color.WHITE);
        tvSumHeader.setBackgroundColor(Color.WHITE);

        // SET PADDING
        tvCategoryHeader.setPadding(20, 20, 20, 20);
        tvSumHeader.setPadding(20, 20, 20, 20);

        // SET TEXTVIEW TEXT
        tvCategoryHeader.setText(getString(R.string.textView_category));
        tvSumHeader.setText(getString(R.string.textView_sum));

        // ADD TEXTVIEW TO TABLEROW
        tableHeader.addView(tvCategoryHeader);
        tableHeader.addView(tvSumHeader);

        // ADD TABLEROW TO TABLELAYOUT
        tableLayout.addView(tableHeader);
    }

    private void setTableFooter(ArrayMap<Category, Double> sumMap){
        TableRow tableHeader = new TableRow(this);
        ArrayList<TextView> tvCategoryList = new ArrayList<>(sumMap.size());

        // CREATE PARAM FOR MARGINING
        TableRow.LayoutParams lParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        lParams.topMargin = 3;
        lParams.rightMargin = 3;

        // CREATE TEXTVIEW
//        TextView tvNumberFooter = new TextView(this);
        TextView tvDateFooter = new TextView(this);

        for (Category category: categories){
            Double sum = 0.0;
            if (sumMap.containsKey(category)){
                sum = sumMap.get(category);
            }
            TextView tvCategoryFooter = new TextView(this);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            String sumString = format.format(sum);
            if (sum != 0) {
                tvCategoryFooter.setTypeface(tvCategoryFooter.getTypeface(), Typeface.BOLD);
            }
            tvCategoryFooter.setText(sumString);
            tvCategoryList.add(tvCategoryFooter);
        }

        // SET GRAVITY CENTER
//        tvNumberFooter.setGravity(Gravity.CENTER);
        tvDateFooter.setGravity(Gravity.CENTER);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setGravity(Gravity.CENTER);
        }

        // SET BOLD TYPEFACE
//        tvNumberFooter.setTypeface(tvNumberFooter.getTypeface(), Typeface.BOLD);
        tvDateFooter.setTypeface(tvDateFooter.getTypeface(), Typeface.BOLD);

        // SET PARAMS
//        tvNumberFooter.setLayoutParams(lParams);
        tvDateFooter.setLayoutParams(lParams);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setLayoutParams(lParams);
        }

        // SET BACKGROUND COLOR
//        tvNumberFooter.setBackgroundColor(Color.WHITE);
        tvDateFooter.setBackgroundColor(Color.WHITE);
        for (TextView tvCategory: tvCategoryList){
            tvCategory.setBackgroundColor(Color.WHITE);
        }

        // SET PADDING
//        tvNumberFooter.setPadding(20, 20, 20, 20);
        tvDateFooter.setPadding(20, 20, 20, 20);
        for (TextView tvCategory: tvCategoryList) {
            tvCategory.setPadding(20, 20, 20, 20);
        }

        // ADD TEXTVIEW TO TABLEROW
//        tableHeader.addView(tvNumberFooter);
        tableHeader.addView(tvDateFooter);
        for (TextView tvCategory: tvCategoryList) {
            tableHeader.addView(tvCategory);
        }

        // ADD TABLEROW TO TABLELAYOUT
        tableLayout.addView(tableHeader);
    }


    private boolean setSum(long dateInMills){
        DBExpenseAdapter adapter = new DBExpenseAdapter(this);
        double sum;
        if (mode == Constants.MONTH_STATISTICS){
            sum = adapter.getSumExpensesByMonth(dateInMills);
            tvSumTitle.setText(R.string.sum_per_month);
        }else {
            sum = adapter.getSumExpensesByDay(dateInMills);
            tvSumTitle.setText(R.string.sum_per_day);
        }
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String sumString = format.format(sum);
        tvTotalSum.setText(sumString);
        tvTotalSum.setTypeface(tvTotalSum.getTypeface(), Typeface.BOLD);
        return sum != 0;
    }

    private void setMonth(){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(currentTime);
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        tvMonth.setText(month);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.day_chooser_activity_expenditure_statics){
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTime);
            new DatePickerDialog(ExpenditureStatisticsActivity.this, d,
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH))
                    .show();
        }else {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTime);
            int month = date.get(Calendar.MONTH);

            switch (view.getId()) {
                case R.id.month_switcher_arrow_left_activity_expenditure_statics:
                    month = month > 0 ? month - 1 : 11;
                    break;
                case R.id.month_switcher_arrow_right_activity_expenditure_statics:
                    month = month < 11 ? month + 1 : 0;
                    break;
            }
            date.set(Calendar.MONTH, month);
            currentTime = date.getTimeInMillis();
            setMonth();
            new LoadExpensesAsyncTask(this, mode, currentTime).execute();
        }

    }

//    @Override
//    public boolean onLongClick(View v) {
//        Expense expense = expenses.get(v.getId());
//        Intent intent = new Intent(this, AddNewExpensesActivity.class);
//        intent.putExtra(AddNewExpensesActivity.ID_WHAT_TO_DO_WITH_EXPENSE, AddNewExpensesActivity.CHANGE_EXPENSE);
//        intent.putExtra(AddNewExpensesActivity.EXPENSE_ID, expense);
//        startActivityForResult(intent, v.getId());
//        return true;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        Log.e("TAG", "onActivityResult");
        new LoadExpensesAsyncTask(ExpenditureStatisticsActivity.this, mode, currentTime).execute();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTime);

            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentTime = date.getTimeInMillis();
            setDate();
            new LoadExpensesAsyncTask(ExpenditureStatisticsActivity.this, mode, currentTime).execute();
        }
    };

    private void setDate() {
        tvDay.setText(DateUtils.formatDateTime(this, currentTime,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void showDialogRemoveExpense(final Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set title and icon
        builder.setTitle(getString(R.string.app_name));

        builder.setMessage(R.string.remove_expense)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                    DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(ExpenditureStatisticsActivity.this);
                    if (dbExpenseAdapter.delete(expense) != -1) {
                        Toast.makeText(ExpenditureStatisticsActivity.this, getString(R.string.expense_deleted), Toast.LENGTH_SHORT).show();
                    }
                    new LoadExpensesAsyncTask(ExpenditureStatisticsActivity.this, mode, currentTime).execute();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }
}
