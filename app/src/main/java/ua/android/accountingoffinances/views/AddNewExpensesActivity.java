package ua.android.accountingoffinances.views;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import ua.android.accountingoffinances.R;
import ua.android.accountingoffinances.database.DBCategoryAdapter;
import ua.android.accountingoffinances.database.DBExpenseAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.model.Expense;
import ua.android.accountingoffinances.util.Constants;

/** Class responsible for work  add_new_expenses_activity, and describes the logic in it.*/

public class AddNewExpensesActivity extends AppCompatActivity implements  View.OnClickListener {

    public static final String ID_WHAT_TO_DO_WITH_EXPENSE = "what to do with expenses";
    public  static final String EXPENSE_ID = "expensesID";
    public  static final int CHANGE_EXPENSE = 100;
    public  static final int ADD_NEW_EXPENSE = 101;

    private int whatToDo;

    Expense expense = new Expense();

    RelativeLayout relativeLayout;
    public TextView textViewCategory;

    SharedPreferences sPref;
    SharedPreferences.Editor ed;

    TextView textViewData;

    EditText  editTextAddSum;
    Calendar dateAndTime = Calendar.getInstance();

    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_expenses);

        textViewCategory = findViewById(R.id.textViewCategory);
        final Intent intent = new Intent(this, AddNewCategoryActivity.class);
        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent,1);
            }
        });
        editTextAddSum = findViewById(R.id.editTextAddSum);
        editTextAddSum.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    editTextAddSum.removeTextChangedListener(this);
                    String text = s.toString();
                    int index = text.indexOf(".");
                    int selection = start+1<=text.length()?start+1:start;

                    if (index != -1 && (index > 7 || text.length()>index+3) || index == -1 && text.length() > 7){
                        text = current;
                        selection = start;

                    }

                    if (text.length() < current.length()){

                        if (start==0){

                            selection=0;
                        }else

                        selection = start;
                    }

                    Log.d("Log",start + " ");

                    current = text;
                    editTextAddSum.setText(text);
                    editTextAddSum.setSelection(selection);

                    editTextAddSum.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        textViewData = findViewById(R.id.textViewDate);

        textViewData.setOnClickListener(this);

        setInitialDateTime(dateAndTime.getTimeInMillis());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbar_title);
        }
        loadCategory();
        checkAddNewOrChange();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            category = data.getParcelableExtra(Constants.ID_ACTIVITY_RESULT_CATEGORY);
            if(category != null && category.getName() != null){
                textViewCategory.setText(category.getName());
            }
            String stringExtra = data.getStringExtra(Constants.ID_ACTIVITY_RESULT_THERE_ARE_NO_CATEGORIES);
            if (stringExtra!=null){
                textViewCategory.setText("");
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setInitialDateTime(long myLong) {
        textViewData.setText(DateUtils.formatDateTime(this,
                myLong,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    DatePickerDialog.OnDateSetListener d =new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime(dateAndTime.getTimeInMillis());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_btn_ADD) {
                if (textViewCategory.getText().toString().length() >= 1) {
                    if (editTextAddSum.getText().length() > 0) {
                        double amount;
                        String text = editTextAddSum.getText().toString();
                        if (text.equals(".")){
                            amount=0;
                        }else {
                            amount = Double.parseDouble(editTextAddSum.getText().toString());
                        }

                        if (amount!=0) {

                            if (whatToDo == ADD_NEW_EXPENSE) {

                                DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(this);
                                dbExpenseAdapter.insert(new Expense(-1, amount, dateAndTime.getTimeInMillis(), category));


                                saveCategoryInSharPref(category.getId(), Constants.ID_SAVE_CATEGORY);

                                Toast.makeText(this, R.string.toast_expense_save, Toast.LENGTH_SHORT).show();

                                finish();

                            } else {

                                expense.setCategory(category);
                                expense.setAmount(Double.parseDouble(editTextAddSum.getText().toString()));
                                expense.setDate(dateAndTime.getTimeInMillis());

                                DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(this);
                                dbExpenseAdapter.update(expense);

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                        }else {
                            Toast.makeText(this, R.string.toast_sum_is_empty, Toast.LENGTH_SHORT).show();
                            editTextAddSum.setText("");
                        }

                    } else {
                        Toast.makeText(this, R.string.toast_sum_is_empty, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(this, R.string.toast_select_a_category, Toast.LENGTH_SHORT).show();

            }

        return super.onOptionsItemSelected(item);
    }

    public void saveCategoryInSharPref(int myValues, String key){
        sPref = getPreferences(MODE_PRIVATE);
        ed  = sPref.edit();
        ed.putInt(key, myValues);
        ed.apply();
    }

    private void loadCategory(){
        sPref = getPreferences(MODE_PRIVATE);
        int savedID = sPref.getInt(Constants.ID_SAVE_CATEGORY, 1);
        DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(this);
        category =  dbCategoryAdapter.getCategory(savedID);
        textViewCategory.setText(category.getName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textViewDate:
                new DatePickerDialog(AddNewExpensesActivity.this, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
        }
    }

    private void checkAddNewOrChange(){
        whatToDo =  getIntent().getIntExtra(ID_WHAT_TO_DO_WITH_EXPENSE, ADD_NEW_EXPENSE);
        expense =  getIntent().getParcelableExtra(EXPENSE_ID);

        if (whatToDo==CHANGE_EXPENSE){
            getSupportActionBar().setTitle(R.string.toolbar_title_change);
            textViewCategory.setText(expense.getCategory().getName());
            dateAndTime.setTimeInMillis(expense.getDate());
            category = expense.getCategory();
            editTextAddSum.setText(String.valueOf(expense.getAmount()));
            setInitialDateTime(expense.getDate());
        }
    }

}
