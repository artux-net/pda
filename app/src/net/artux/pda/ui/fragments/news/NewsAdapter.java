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

import net.artux.pda.R;
import net.artux.pda.model.news.ArticleModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private List<ArticleModel> mArticleModels;
    private final OnClickListener clickListener;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofPattern("dd.MM")
            .withZone(ZoneId.systemDefault());

    public NewsAdapter(OnClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    public void setNews(List<ArticleModel> articleModels) {
        mArticleModels = articleModels;
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
        holder.bind(holder.mainView, mArticleModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticleModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
        public void bind(View mainView, final ArticleModel articleModel) {
            titleView.setText(Html.fromHtml(articleModel.getTitle()));
            contentView.setText(Html.fromHtml(articleModel.getDescription()));

            dateView.setText(dateTimeFormatter.format(articleModel.getPublished()));
            mainView.setOnClickListener(v -> clickListener.onClick(articleModel));
            Glide
                    .with(imageView.getContext())
                    .load(articleModel.getImage())
                    .into(imageView);
        }

    }

    interface OnClickListener {
        void onClick(ArticleModel articleModel);
    }

}
