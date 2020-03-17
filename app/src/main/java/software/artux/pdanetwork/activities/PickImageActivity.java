package software.artux.pdanetwork.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import software.artux.pdanetwork.PhotoItemRecyclerViewAdapter;
import com.devilsoftware.pdanetwork.R;

import software.artux.pdanetwork.app.App;

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
