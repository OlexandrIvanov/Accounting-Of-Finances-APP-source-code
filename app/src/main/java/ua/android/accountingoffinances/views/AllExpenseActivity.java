package ua.android.accountingoffinances.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;

import ua.android.accountingoffinances.R;
import ua.android.accountingoffinances.database.DBExpenseAdapter;
import ua.android.accountingoffinances.util.Constants;

/** Class responsible for work  all_expense_activity, and describes the logic in it.*/

public class AllExpenseActivity extends AppCompatActivity implements View.OnClickListener {



    CardView cardViewNewExpenses, cardViewMonthlyExpenses, cardViewExpensesPerDay;
    TextView textView_expense_per_month, textViewDayExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expense);

        Toolbar toolbar= findViewById(R.id.activity_all_expense_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        textView_expense_per_month = findViewById(R.id.textView_expense_per_month);
        textView_expense_per_month.setFocusable(false);
        textViewDayExpenses = findViewById(R.id.textViewDayExpenses);

        cardViewNewExpenses = findViewById(R.id.cardViewNewExpenses);
        cardViewMonthlyExpenses = findViewById(R.id.cardViewMonthlyExpenses);
        cardViewExpensesPerDay = findViewById(R.id.cardViewExpensesPerDay);

        cardViewNewExpenses.setOnClickListener(this);
        cardViewMonthlyExpenses.setOnClickListener(this);
        cardViewExpensesPerDay.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSum();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, ExpenditureStatisticsActivity.class);
        switch (view.getId()){
            case R.id.cardViewNewExpenses:
                Intent intentNewExpensesActivity = new Intent(this, AddNewExpensesActivity.class);
                startActivity(intentNewExpensesActivity);
                break;
            case R.id.cardViewMonthlyExpenses:
                intent.putExtra(Constants.ID_PUT_EXTRA, Constants.MONTH_STATISTICS);
                startActivity(intent);
                break;
            case R.id.cardViewExpensesPerDay:
                intent.putExtra(Constants.ID_PUT_EXTRA, Constants.DAY_STATISTICS);
                startActivity(intent);
                break;
        }
    }

    private void getSum(){
        DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(this);
        long time = Calendar.getInstance().getTimeInMillis();
        double sumPerDay =  dbExpenseAdapter.getSumExpensesByDay(time);
        double sumPerMonth =  dbExpenseAdapter.getSumExpensesByMonth(time);

        NumberFormat numberFormat= NumberFormat.getCurrencyInstance();
        String price = numberFormat.format(sumPerDay);
        String priceForMonth = numberFormat.format(sumPerMonth);

        textViewDayExpenses.setText(price);
        textView_expense_per_month.setText(priceForMonth);
    }
}
