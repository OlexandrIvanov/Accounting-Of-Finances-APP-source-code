package ua.android.accountingoffinances.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by Acer on 30.03.2018.
 */

public class Expense implements Parcelable {

    private int id;
    private double amount;
    private long date;
    private Category category;

    public Expense(int id, double amount, long date, Category category) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public Expense() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static final Parcelable.Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel source) {
            return new Expense(source);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    private Expense(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeDouble(this.amount);
        dest.writeLong(this.date);
        dest.writeParcelable(this.category, flags);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.amount = source.readDouble();
        this.date = source.readLong();
        this.category = source.readParcelable(Category.class.getClassLoader());
    }

    @Override
    public String toString() {
        String dateString = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date(date)).toString();
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", date='" + dateString + '\'' +
                ", category=" + category +
                '}';
    }
}
