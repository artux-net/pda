package net.artux.pda.ui.fragments.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.ResponsePage;
import net.artux.pda.model.news.Article;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.util.GsonProvider;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NewsFragment extends BaseFragment {

    NewsAdapter adapter;
    FragmentListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getResources().getString(R.string.news));
            navigationPresenter.setLoadingState(true);
        }

        adapter = new NewsAdapter(navigationPresenter);
        binding.list.setAdapter(adapter);

        Type listType = new TypeToken<List<Article>>(){}.getType();
        List<Article> news = GsonProvider.getInstance().fromJson(App.getDataManager().getString("news"), listType);
        if(news!=null && !news.isEmpty()){
            binding.list.setVisibility(View.VISIBLE);
            binding.viewMessage.setVisibility(View.GONE);
            adapter.setNews(news);
        }

        ((App)getActivity().getApplication()).getOldApi().getFeed().enqueue(new Callback<ResponsePage<Article>>() {
            @Override
            public void onResponse(Call<ResponsePage<Article>> call, Response<ResponsePage<Article>> response) {
                ResponsePage<Article> page = response.body();
                if (page!=null){
                    List<Article> list = page.getData();
                    if (binding!=null && !list.isEmpty()) {
                        binding.list.setVisibility(View.VISIBLE);
                        binding.viewMessage.setVisibility(View.GONE);
                        adapter.setNews(list);
                        App.getDataManager().setString("news", GsonProvider.getInstance().toJson(list));
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponsePage<Article>> call, Throwable t) {
                Timber.tag("News").e(t);
            }
        });
    }

    @Override
    public void onDestroyView() {
        binding.list.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }
}
