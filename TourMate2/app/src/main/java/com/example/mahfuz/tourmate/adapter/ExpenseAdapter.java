package com.example.mahfuz.tourmate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.model.Expense;
import com.example.mahfuz.tourmate.utility.TourUtility;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;



    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_alert_custom_layout,
                parent, false);

        ExpenseAdapter.ExpenseViewHolder eventViewHolder = new ExpenseAdapter.ExpenseViewHolder(view);

        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
            holder.commentTv.setText(expenses.get(position).getComment());
            holder.amountTv.setText(expenses.get(position).getExpenseAmount()+" Tk");
            holder.dateTv.setText(TourUtility.milliToDate(expenses.get(position).getExpenseDate()));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder{

        private TextView commentTv, amountTv, dateTv, timeTv;

        public ExpenseViewHolder(View itemView) {
            super(itemView);

            commentTv = itemView.findViewById(R.id.commentTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            dateTv = itemView.findViewById(R.id.expenseDateTv);

        }
    }
}
