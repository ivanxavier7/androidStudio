package com.ptda.imiser.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ptda.imiser.R;
import com.ptda.imiser.model.Transaction;

import java.util.List;

public class AdapterTransaction extends RecyclerView.Adapter<AdapterTransaction.MyViewHolder> {

    List<Transaction> transactionList;
    Context context;

    public AdapterTransaction(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_movimentacao, parent, false);
        return new MyViewHolder(itemList);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.title.setText(transaction.getDescription());
        holder.value.setText(String.valueOf(transaction.getValue()));
        holder.category.setText(transaction.getCategory());
        holder.value.setTextColor(context.getResources().getColor(R.color.colorAccent));

        if (transaction.getType().equals("levantamento")) {
            holder.value.setTextColor(context.getResources().getColor(R.color.colorError));
            holder.value.setText("-" + transaction.getValue());
        }
    }


    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView value;
        private TextView category;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textAdapterTitle);
            value = itemView.findViewById(R.id.textAdapterValue);
            category = itemView.findViewById(R.id.textAdapterCategory);
        }

    }

}
