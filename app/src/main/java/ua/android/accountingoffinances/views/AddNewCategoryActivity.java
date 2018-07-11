package ua.android.accountingoffinances.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ua.android.accountingoffinances.R;
import ua.android.accountingoffinances.database.DBCategoryAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.util.Constants;
import ua.android.accountingoffinances.util.LoadCategoriesAsyncTask;
import ua.android.accountingoffinances.views.adapter.RecyclerAdapter;

/** Class responsible for work  add_new_category_activity, and describes the logic in it.*/

public class AddNewCategoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    public RecyclerView my_recycler_view;
    public LinearLayoutManager vllm;
    public RecyclerAdapter adapter;
    ImageView imageButtonAddCategory;
    TextView editTextAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);

        toolbar = findViewById(R.id.toolbar_activity_add_new_category);
        setSupportActionBar(toolbar);


        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        editTextAddCategory = findViewById(R.id.editTextAddCategory);

        imageButtonAddCategory = findViewById(R.id.imageButtonAddCategory);
        imageButtonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextAddCategory.getText().toString().length()<=1){
                    Toast.makeText(AddNewCategoryActivity.this, R.string.toast_category_not_added, Toast.LENGTH_SHORT).show();
                }else {
                    DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(AddNewCategoryActivity.this);
                    long id = dbCategoryAdapter.insert(new Category(-1, editTextAddCategory.getText().toString()));

                    Intent intent = new Intent();
                    intent.putExtra(Constants.ID_ACTIVITY_RESULT_CATEGORY, new Category((int) id, editTextAddCategory.getText().toString()));
                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        });

        my_recycler_view = findViewById(R.id.my_recycler_view);
        vllm = new LinearLayoutManager(this);
        my_recycler_view.setLayoutManager(vllm);

        loadCategories();
    }
    @Override
    public boolean onSupportNavigateUp() {
        ArrayList<Category> listCategories;
        DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(this);
        listCategories = dbCategoryAdapter.getCategories();

        if (listCategories.size()==0){
            Intent intent = new Intent();
            intent.putExtra(Constants.ID_ACTIVITY_RESULT_THERE_ARE_NO_CATEGORIES,
                    Constants.ID_ACTIVITY_RESULT_THERE_ARE_NO_CATEGORIES);
            setResult(RESULT_OK, intent);
        }

        onBackPressed();

        return super.onSupportNavigateUp();
    }

    void  loadCategories(){
        new LoadCategoriesAsyncTask(this).execute();
    }

    public  void setAdapter(ArrayList<Category> categories){
       adapter = new RecyclerAdapter(categories, this);
       my_recycler_view.setAdapter(adapter);
    }

   public void updateCategory(Category category, int id, String categoryNewName){
       DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(this);
       dbCategoryAdapter.update(category);

       adapter.categoryNameList.get(id).setName(categoryNewName);

       adapter.notifyDataSetChanged();
   }

}
