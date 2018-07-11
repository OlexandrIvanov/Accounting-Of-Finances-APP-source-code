package ua.android.accountingoffinances.views.adapter;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.android.accountingoffinances.R;
import ua.android.accountingoffinances.database.DBCategoryAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.util.Constants;
import ua.android.accountingoffinances.views.AddNewCategoryActivity;
import ua.android.accountingoffinances.views.DialogChangeCategory;

import static android.app.Activity.RESULT_OK;

/** The class is responsible for filling RecyclerView data. */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    public ArrayList<Category> categoryNameList;

    private AddNewCategoryActivity addNewCategoryActivity;

    public RecyclerAdapter(ArrayList<Category> categoryNameList, AddNewCategoryActivity addNewCategoryActivity) {
        this.categoryNameList = categoryNameList;
        this.addNewCategoryActivity = addNewCategoryActivity;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bind(categoryNameList.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(addNewCategoryActivity, holder.imageView);
                popupMenu.inflate(R.menu.recycler_item_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnu_item_save:
                                DialogFragment dialogFragment;
                                dialogFragment = new DialogChangeCategory(categoryNameList.get(position), position);
                                dialogFragment.show(addNewCategoryActivity.getFragmentManager(),"DialogChangeCategory");
                                break;
                            case R.id.mnu_item_delete:
                                DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(addNewCategoryActivity);
                                dbCategoryAdapter.delete(categoryNameList.get(position));
                                categoryNameList.remove(categoryNameList.get(position));
                                notifyDataSetChanged();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Constants.ID_ACTIVITY_RESULT_CATEGORY, categoryNameList.get(position));

                addNewCategoryActivity.setResult(RESULT_OK, intent);
                addNewCategoryActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryNameList.size();
    }
}

class RecyclerViewHolder extends RecyclerView.ViewHolder {

TextView textView;
ImageView imageView;

RecyclerViewHolder(View itemView) {
    super(itemView);
    textView = itemView.findViewById(R.id.textView_RW_item);
    imageView = itemView.findViewById(R.id.imageButton) ;
}

void bind(Category modelItem) {
    textView.setText(modelItem.getName());
}
}
