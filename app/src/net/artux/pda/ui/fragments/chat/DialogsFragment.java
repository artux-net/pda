package net.artux.pda.ui.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.DialogsAdapter;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DialogsFragment extends BaseFragment {

    private final Gson mGson = new Gson();
    private DialogsAdapter dialogsAdapter;
    FragmentListBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null)
            navigationPresenter.setTitle(getResources().getString(R.string.chat));

        dialogsAdapter = new DialogsAdapter((MainActivity) getActivity(), navigationPresenter);
        Type listType = new TypeToken<List<Dialog>>(){}.getType();
        List<Dialog> dialogs = mGson.fromJson(App.getDataManager().getDialogsJson(), listType);
        if(dialogs!=null){
            dialogsAdapter.setDialogs(dialogs);
        }

        binding.list.setAdapter(dialogsAdapter);

        load();
    }

    void load(){
        if (navigationPresenter!=null)
            navigationPresenter.setLoadingState(true);
        App.getRetrofitService().getPdaAPI().getFirstDialogs("f").enqueue(new Callback<List<Dialog>>() {
            @Override
            public void onResponse(Call<List<Dialog>> call, Response<List<Dialog>> response) {
                List<Dialog> dialogs = response.body();
                if (dialogs!=null) {
                    binding.list.setVisibility(View.VISIBLE);
                    binding.viewMessage.setVisibility(View.GONE);
                    Timber.d("Set dialogs");
                    dialogsAdapter.setDialogs(dialogs);
                    if (navigationPresenter!=null)
                        navigationPresenter.setLoadingState(false);
                    updateDialog();
                }else load();

            }

            @Override
            public void onFailure(Call<List<Dialog>> call, Throwable t) {
                Timber.e(t);
                load();
            }
        });
    }

    void updateDialog(){
        App.getRetrofitService().getPdaAPI().getDialogs().enqueue(new Callback<List<Dialog>>() {
            @Override
            public void onResponse(Call<List<Dialog>> call, Response<List<Dialog>> response) {
                List<Dialog> dialogs = response.body();
                if (dialogs!=null) {
                    binding.list.setVisibility(View.VISIBLE);
                    binding.viewMessage.setVisibility(View.GONE);
                    Timber.d("Set dialogs");
                    dialogsAdapter.setDialogs(dialogs);
                }
                updateDialog();
            }

            @Override
            public void onFailure(Call<List<Dialog>> call, Throwable t) {
                Timber.e(t);
                updateDialog();
            }
        });
    }
}
