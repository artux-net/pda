package net.artux.pda.ui.fragments.news;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import net.artux.pda.R;
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation;
import net.artux.pdalib.news.Article;

import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends  RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private List<Article> mArticles = new ArrayList<>();

    FragmentNavigation.Presenter presenter;

    NewsAdapter(FragmentNavigation.Presenter presenter ){
        this.presenter = presenter;
    }

    public void setNews(List<Article> articles) {
        mArticles = articles;
        notifyDataSetChanged();
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        holder.bind(holder.mainView,mArticles.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements NewsClickListener {

        View mainView;
        ImageView imageView;
        TextView titleView;
        TextView contentView;
        TextView dateView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            imageView = itemView.findViewById(R.id.image);
            titleView = itemView.findViewById(R.id.title);
            contentView = itemView.findViewById(R.id.content);
            dateView = itemView.findViewById(R.id.date);
        }

        @SuppressLint("SetTextI18n")
        public void bind(View mainView, final Article article){
            titleView.setText(Html.fromHtml(article.title));
            contentView.setText(Html.fromHtml(article.description));

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd.MM", Locale.getDefault());

            dateView.setText(outputFormat.format(new Date(article.published)));
            mainView.setOnClickListener(v -> ViewHolder.this.onClick(article));
            Glide
                    .with(imageView.getContext())
                    .load(article.image)
                    .into(imageView);
        }

        @Override
        public void onClick(Article article) {
            OpenNewsFragment openNewsFragment = new OpenNewsFragment();
            openNewsFragment.setArticle(article);
            if (presenter!=null) {
                presenter.setTitle(article.title);
                //presenter.setLoadingState(true);
                presenter.addFragment(openNewsFragment, false);
            }
        }
    }

}
