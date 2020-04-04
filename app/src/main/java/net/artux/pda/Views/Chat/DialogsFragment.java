package net.artux.pda.Views.Chat;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.artux.pda.Models.Dialog;
import net.artux.pda.R;
import net.artux.pda.activities.MainActivity;
import net.artux.pda.app.App;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogsFragment extends Fragment {

    View mainView;
    RecyclerView mRecyclerView;
    Gson mGson = new Gson();

    Handler updateDialogHandler = new Handler();
    int delay = 10000; // 10 seconds
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (mainView==null){
            mainView = inflater.inflate(R.layout.fragment_messages, container, false);

            mRecyclerView = mainView.findViewById(R.id.dialogs);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());

            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);

            DialogsAdapter dialogsAdapter = new DialogsAdapter((MainActivity) getActivity(), App.getDataManager().getMember().getGroup());
            Type listType = new TypeToken<List<Dialog>>(){}.getType();
            List<Dialog> dialogs = mGson.fromJson(App.getDataManager().getDialogsJson(), listType);
            if(dialogs!=null){
                dialogsAdapter.setDialogs(dialogs);
                dialogsAdapter.notifyDataSetChanged();
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

                App.getDataManager().setDialogsJson(mGson.toJson(dialogs));
                DialogsAdapter dialogsAdapter = new DialogsAdapter((MainActivity) getActivity(), App.getDataManager().getMember().getGroup());
                dialogsAdapter.setDialogs(dialogs);
                dialogsAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(dialogsAdapter);

            }

            @Override
            public void onFailure(Call<List<Dialog>> call, Throwable t) {

            }
        });
    }
}
