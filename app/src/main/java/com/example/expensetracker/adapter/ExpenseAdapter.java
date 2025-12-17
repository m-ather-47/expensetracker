package com.example.expensetracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items = new ArrayList<>();
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(Expense expense);
        void onDelete(Expense expense);
    }

    public void setOnItemActionListener(OnItemActionListener l) { this.listener = l; }

    public void setItems(List<ListItem> list) {
        List<ListItem> newList = list != null ? list : new ArrayList<>();
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new ListItemDiffCallback(this.items, newList));
        this.items.clear();
        this.items.addAll(newList);
        diff.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem li = items.get(position);
        if (li.type == ListItem.TYPE_HEADER) {
            HeaderViewHolder hv = (HeaderViewHolder) holder;
            hv.tvHeader.setText(li.header);
        } else {
            Expense e = li.expense;
            ExpenseViewHolder ev = (ExpenseViewHolder) holder;
            ev.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", e.amount));
            ev.tvCategory.setText(e.category == null ? "" : e.category);
            ev.tvDescription.setText(e.description == null ? "" : e.description);

            // Row click -> edit
            ev.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(e);
            });

            ev.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(e);
            });
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeaderDate);
        }
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvCategory, tvDescription;
        ImageButton btnDelete;
        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // DiffUtil callback for ListItem
    private static class ListItemDiffCallback extends DiffUtil.Callback {
        private final List<ListItem> oldList;
        private final List<ListItem> newList;

        ListItemDiffCallback(List<ListItem> oldList, List<ListItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            ListItem o = oldList.get(oldItemPosition);
            ListItem n = newList.get(newItemPosition);
            if (o.type != n.type) return false;
            if (o.type == ListItem.TYPE_HEADER) return o.header.equals(n.header);
            // expense: compare ids
            return o.expense != null && n.expense != null && o.expense.id == n.expense.id;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ListItem o = oldList.get(oldItemPosition);
            ListItem n = newList.get(newItemPosition);
            if (o.type != n.type) return false;
            if (o.type == ListItem.TYPE_HEADER) return o.header.equals(n.header);
            if (o.expense == null || n.expense == null) return false;
            // compare relevant fields
            return o.expense.amount == n.expense.amount
                    && ((o.expense.description == null && n.expense.description == null) || (o.expense.description != null && o.expense.description.equals(n.expense.description)))
                    && ((o.expense.category == null && n.expense.category == null) || (o.expense.category != null && o.expense.category.equals(n.expense.category)))
                    && o.expense.date == n.expense.date;
        }
    }
}
