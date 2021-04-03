package vn.example.btl_orderfood.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import vn.example.btl_orderfood.Interface.ItemClickListener;
import vn.example.btl_orderfood.R;
import vn.example.btl_orderfood.comon.Common;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    private ItemClickListener itemClickListener;


    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        itemView.setOnClickListener(this);

    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);
    }



}

