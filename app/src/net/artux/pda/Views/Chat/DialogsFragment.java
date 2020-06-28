package net.artux.pda.Views.Chat;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.Models.Dialog;
import net.artux.pda.R;
import net.artux.pda.activities.MainActivity;
import net.artux.pda.app.App;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogsFragment extends Fragment {

    private View mainView;
    private RecyclerView mRecyclerView;
    private Gson mGson = new Gson();
    private DialogsAdapter dialogsAdapter;

    private Handler updateDialogHandler = new Handler();
    private int delay = 10000; // 10 seconds
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mainView==null){
            mainView = inflater.inflate(R.layout.fragment_messages, container, false);

            mRecyclerView = mainView.findViewById(R.id.dialogs);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());

            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);

            dialogsAdapter = new DialogsAdapter((MainActivity) getActivity());
            Type listType = new TypeToken<List<Dialog>>(){}.getType();
            List<Dialog> dialogs = mGson.fromJson(App.getDataManager().getDialogsJson(), listType);
            if(dialogs!=null){
                dialogsAdapter.setDialogs(dialogs);
            }

            mRecyclerView.setAdapter(dialogsAdapter);

            updateDialog();
        }

        updateDialogHandler.postDelayed(new Runnable(){
            public void run(){
                updateDialog();

                updateDialogHandler.postDelayed(this, delay);
            }
        }, delay);

        return mainView;
    }

    void updateDialog(){
        App.getRetrofitService().getPdaAPI().getDialogs().enqueue(new Callback<List<Dialog>>() {
            @Override
            public void onResponse(Call<List<Dialog>> call, Response<List<Dialog>> response) {
                List<Dialog> dialogs = response.body();
                dialogsAdapter.setDialogs(dialogs);
            }

            @Override
            public void onFailure(Call<List<Dialog>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
