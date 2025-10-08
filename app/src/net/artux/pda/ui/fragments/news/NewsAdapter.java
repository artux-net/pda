package net.artux.pda.ui.fragments.news;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.databinding.ItemNewsBinding;
import net.artux.pda.model.news.ArticleModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private List<ArticleModel> mArticleModels;
    private final OnClickListener clickListener;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofPattern("dd/MM")
            .withZone(ZoneId.systemDefault());

    public NewsAdapter(OnClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    public void setNews(List<ArticleModel> articleModels) {
        mArticleModels = articleModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNewsBinding binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new NewsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        holder.bind(holder.binding.getRoot(), mArticleModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticleModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemNewsBinding binding;

        public ViewHolder(ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(View mainView, final ArticleModel articleModel) {
            binding.title.setText(Html.fromHtml(articleModel.getTitle()));
            binding.content.setText(Html.fromHtml(articleModel.getDescription()));
            binding.likes.setText(Integer.toString(articleModel.getLikes()));
            binding.comments.setText(Integer.toString(articleModel.getComments()));
            binding.date.setText(dateTimeFormatter.format(articleModel.getPublished()));
            mainView.setOnClickListener(v -> clickListener.onClick(articleModel));
            Glide
                    .with(binding.image.getContext())
                    .load(articleModel.getImage())
                    .into(binding.image);
        }

    }

    interface OnClickListener {
        void onClick(ArticleModel articleModel);
    }

}
