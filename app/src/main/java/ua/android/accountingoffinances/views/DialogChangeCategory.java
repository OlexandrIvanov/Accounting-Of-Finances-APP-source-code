package ua.android.accountingoffinances.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.app.Dialog;

import ua.android.accountingoffinances.R;
import ua.android.accountingoffinances.model.Category;

/** Class responsible for creating a dialog in {@link AddNewCategoryActivity}.*/

@SuppressLint("ValidFragment")
public class DialogChangeCategory extends DialogFragment {

    AddNewCategoryActivity addNewCategoryActivity;
    Category category;
    int id;

    @SuppressLint("ValidFragment")
    public DialogChangeCategory(Category category, int id) {
        this.category = category;
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText editText = new EditText(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_titles)
                .setView(editText)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                category = new Category(category.getId(), editText.getText().toString());
                addNewCategoryActivity.updateCategory(category, id, editText.getText().toString());
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        addNewCategoryActivity=(AddNewCategoryActivity) activity;
    }
}
