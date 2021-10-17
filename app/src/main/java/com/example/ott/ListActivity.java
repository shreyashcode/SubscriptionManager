package com.example.ott;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ott.CardRecyclerViewAdapter;
import com.example.ott.R;
import com.example.ott.Subscription;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListActivity extends AppCompatActivity implements ItemClickListener{


    private RecyclerView recyclerView;
    private CardRecyclerViewAdapter cardRecyclerViewAdapter;
    private ArrayList<Subscription> subscriptionArrayList;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        subscriptionArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        db = FirebaseFirestore.getInstance();
        TextView textView = findViewById(R.id.heading);
        textView.setText("HellO, " + Common.USER_NAME + "!");

        recyclerView = findViewById(R.id.recView);
        Log.d("DEBUG: ", Common.USER_NAME + Common.USER_PHONE + ",,,,,,,,,,,,");
        db.collection("USER")
                .document(Common.USER_NAME+Common.USER_PHONE)
                .collection("SUBS")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DEBUG: ", "ENTER!");
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String name_ = document.getString("name");
                            Log.d("DEBUG: NAME: ", name_);
                            long rnew = (long) document.get("renewDate");
                            int cost = Integer.parseInt(document.get("cost").toString());
                            String fav = document.getString("favouriteShow");
                            subscriptionArrayList.add(new Subscription(name_, cost, fav, rnew));
                        }
                        cardRecyclerViewAdapter = new CardRecyclerViewAdapter(subscriptionArrayList, this, this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(cardRecyclerViewAdapter);

                        cardRecyclerViewAdapter.notifyDataSetChanged();
                        for(Subscription k: subscriptionArrayList){
                            Log.d("DEBUG: ", k.getName() + " CALL");
                        }
                    }
                });


//        Subscription s1 = new Subscription("hee",100,"20", System.currentTimeMillis());
//        Subscription s2 = new Subscription("wwwhello", 41,"24", System.currentTimeMillis());
//        Subscription s3 = new Subscription("1234o", 234,"2444", System.currentTimeMillis());
//        Subscription s4 = new Subscription("5678ello", 34,"123", System.currentTimeMillis());
////
//        subscriptionArrayList.add(s1);
//        subscriptionArrayList.add(s2);
//        subscriptionArrayList.add(s3);
//        subscriptionArrayList.add(s4);
//        subscriptionArrayList.add(s1);
//        subscriptionArrayList.add(s2);
//        subscriptionArrayList.add(s3);
//        subscriptionArrayList.add(s4);
//        subscriptionArrayList.add(s1);
//        subscriptionArrayList.add(s2);
//        subscriptionArrayList.add(s3);
//        subscriptionArrayList.add(s4);
//        subscriptionArrayList.add(s1);
//        subscriptionArrayList.add(s2);
//        subscriptionArrayList.add(s3);
//        subscriptionArrayList.add(s4);
//
//
//                        cardRecyclerViewAdapter = new CardRecyclerViewAdapter(subscriptionArrayList, this);
//                        recyclerView.setAdapter(cardRecyclerViewAdapter);
//                        recyclerView.setLayoutManager(linearLayoutManager);
//                        cardRecyclerViewAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                Toast.makeText(getBaseContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(getBaseContext(), "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                // TODO Implement Delete
//                int position = viewHolder.getAdapterPosition();
//                arrayList.remove(position);
//                adapter.notifyDataSetChanged();
            }
        };

        CardView cardView = findViewById(R.id.add);
        cardView.setOnClickListener(v->{
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("OTT_NAME", "NULL");
            startActivity(intent);
        });

    }

    @Override
    public void itemClicked(int position) {
        Log.d("DEBUG: ", "DB: " + subscriptionArrayList.get(position).getName());
        Toast.makeText(this, "DB: " + subscriptionArrayList.get(position).getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("OTT_NAME", subscriptionArrayList.get(position).getName());
        startActivity(intent);
    }



}