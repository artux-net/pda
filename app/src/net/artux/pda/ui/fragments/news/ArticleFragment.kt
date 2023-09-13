package net.artux.pda.ui.fragments.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import net.artux.pda.R
import net.artux.pda.model.news.ArticleModel
import net.artux.pda.ui.fragments.web.WebFragment
import net.artux.pda.ui.viewmodels.NewsViewModel
import java.util.UUID

class ArticleFragment : WebFragment(), View.OnClickListener {

    private val newsViewModel: NewsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_open_news, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val likeButton: Button = view.findViewById(R.id.likeButton)
        val commentsView: RecyclerView = view.findViewById(R.id.list)
        likeButton.setOnClickListener(this)
        newsViewModel.status.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.description, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        content.destroy()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun of(articleModel: ArticleModel): ArticleFragment {
            val webFragment = ArticleFragment()
            val bundle = Bundle()
            bundle.putSerializable("id", articleModel.id)
            bundle.putString("title", articleModel.title)
            bundle.putString("url", articleModel.url)
            webFragment.arguments = bundle
            return webFragment
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.likeButton) {
            requireArguments().getSerializable("id", String::class.java)
                ?.let { newsViewModel.likeArticle(UUID.fromString(it)) }
        }
    }
}