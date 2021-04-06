package vn.example.btl_orderfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.example.btl_orderfood.Interface.ItemClickListener;
import vn.example.btl_orderfood.ViewHolder.FoodViewHolder;
import vn.example.btl_orderfood.ui.Food;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId="";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    //Search
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList =new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent() !=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId !=null)
        {
            loadListFood(categoryId);
        }
        //Search
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        loadSuggest();//Tifm trong firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //khi nhập vào ô ,nó sẽ hiển thị ra danh sách cần tìm
                List<String> suggest =new ArrayList<String>();
                for (String search:suggestList)//
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //khi đóng tìm kiếm
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //khi tìm kiếm kết thúc
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food food, int i) {
                viewHolder.food_name.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage()).into(viewHolder.food_image);

                final Food local =food;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this,""+local.getName(),Toast.LENGTH_SHORT).show();
                        //start activity
                        Intent foodDetails = new Intent(FoodList.this,FoodDetail.class);
                        foodDetails.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild(String.valueOf("MenuId".equals(categoryId)))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot:snapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());//thêm tên món ăn
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,FoodViewHolder.class,foodList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food food, int i) {
                viewHolder.food_name.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage()).into(viewHolder.food_image);

                final Food local =food;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this,""+local.getName(),Toast.LENGTH_SHORT).show();
                        //start activity
                        Intent foodDetails = new Intent(FoodList.this,FoodDetail.class);
                        foodDetails.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });
            }
        };
        Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }
}