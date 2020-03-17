package software.artux.pdanetwork.Views.News;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import software.artux.pdanetwork.activities.MainActivity;
import com.devilsoftware.pdanetwork.R;
import com.prof.rssparser.Article;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NewsAdapter extends  RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private ArrayList<Article> mArticles = new ArrayList<>();

    MainActivity mMainActivity;

    NewsAdapter(MainActivity activity){
        mMainActivity = activity;
    }

    public void setNews(ArrayList<Article> articles) {
        mArticles.addAll(articles);
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
            titleView.setText(Html.fromHtml(article.getTitle()));
            Glide.with(mMainActivity).load(article.getImage()).into(imageView);
            contentView.setText(Html.fromHtml(article.getDescription()).subSequence(0,200) + "...");

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd.MM", Locale.getDefault());
            String localTime = outputFormat.format(article.getPubDate());
            dateView.setText(localTime);
            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder.this.onClick(article);
                }
            });

        }

        @Override
        public void onClick(Article article) {
            OpenNewsFragment openNewsFragment = new OpenNewsFragment();
            openNewsFragment.setArticle(article);
            mMainActivity.setTitle(article.getTitle());
            mMainActivity.setLoadingState(true);

            FragmentTransaction fragmentTransaction = mMainActivity.getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.containerView, openNewsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

}
