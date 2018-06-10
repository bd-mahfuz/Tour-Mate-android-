package com.example.mahfuz.tourmate.utility;

import android.text.style.TtsSpan;

import com.example.mahfuz.tourmate.model.Expense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TourUtility {

    public static String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    public static int daysDifference(Date currentDate, String departureDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dd = dateFormat.parse(departureDate);
            long dDate = dd.getTime();
            long cDate = (currentDate.getTime())/1000;

            return (int) ((dDate - cDate)/86400);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String milliToDate(long millisecond) {
        Date date =  new Date(millisecond);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return dateFormat.format(date);
    }

    public static double sumOfExpenses(List<Expense> expenses) {
        double sum = 0;
        for(Expense e : expenses) {
            sum = sum + e.getExpenseAmount();
        }
        return sum;
    }

    public static int getProgressPercentage(double totalBudget, double totalExpense) {
        int result = (int) ((totalExpense/totalBudget)*100);
        return result;
    }


}
