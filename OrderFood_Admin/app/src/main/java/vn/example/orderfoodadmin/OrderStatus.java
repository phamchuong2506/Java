package vn.example.orderfoodadmin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.example.orderfoodadmin.Interface.ItemClickListener;
import vn.example.orderfoodadmin.Model.Request;
import vn.example.orderfoodadmin.ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;

    MaterialSprinner sprinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        // Firebase
        db=FirebaseDatabase.getInstance();
        requests =db.getReference("Requests");

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(); //load all orders
    }
    private void loadOrders(){
        adapter =new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests

        )
        {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
            viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
            viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
            viewHolder.txtOrderAddress.setText(model.getAddress());
            viewHolder.txtOrderPhone.setText(model.getPhone());

            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    // just implement it to fix Crash when click to this item
                }
            });
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            }
        };

    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getIntent().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if(item.getIntent().equals(Common.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }
    private void showUpdateDialog(String key,Request item){
        final AlertDialog.Builder alertDialog =new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("update order");
        alertDialog.setMessage("please choose status");
        LayoutInflater inflater =this.getLayoutInflater();
        final  View view = inflater.inflate(R.layout.update_order_layout,null);
        sprinner= (MateralSpinner)view.findViewById(R.id.statusSpinner);
        sprinner.setItem("Placed","on my way","shipped");
        alertDialog.setView(view);

        final String localKey =key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(sprinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);

            }
        });
        alertDialog.setNegativeButton("No")
    }
}