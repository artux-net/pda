package net.artux.pda.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.PhotoItemRecyclerViewAdapter;

import net.artux.pda.R;
import net.artux.pda.app.App;

public class PickImageActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_image);

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        updateList();
    }

    void updateList(){
        recyclerView.setAdapter(new PhotoItemRecyclerViewAdapter(this, App.avatars));
    }

}
