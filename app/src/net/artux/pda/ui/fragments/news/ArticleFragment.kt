package net.artux.pda.ui.fragments.news

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import net.artux.pda.R
import net.artux.pda.databinding.FragmentInputBinding
import net.artux.pda.databinding.FragmentListBinding
import net.artux.pda.databinding.FragmentOpenNewsBinding
import net.artux.pda.model.news.ArticleModel
import net.artux.pda.model.news.CommentModel
import net.artux.pda.repositories.CommentsRepository
import net.artux.pda.ui.fragments.chat.ChatFragment
import net.artux.pda.ui.fragments.profile.UserProfileFragment
import net.artux.pda.ui.fragments.web.WebFragment
import net.artux.pda.ui.viewmodels.CommentViewModel
import net.artux.pda.ui.viewmodels.NewsViewModel
import net.artux.pda.utils.URLHelper
import net.artux.pda.utils.serializable
import java.util.UUID

class ArticleFragment : WebFragment(), View.OnClickListener, CommentsAdapter.OnClickListener {

    private val newsViewModel: NewsViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()
    private lateinit var binding: FragmentOpenNewsBinding
    private lateinit var fragmentInputBinding: FragmentInputBinding
    private lateinit var commentsBinding: FragmentListBinding
    private lateinit var id: UUID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenNewsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = UUID.fromString(requireArguments().serializable("id"))

        commentsBinding = FragmentListBinding.bind(binding.root.findViewById(R.id.listLayout))
        fragmentInputBinding =
            FragmentInputBinding.bind(binding.root.findViewById(R.id.inputLayout))
        val commentsView: RecyclerView = commentsBinding.list
        val adapter = CommentsAdapter(this)
        commentsView.adapter = adapter

        binding.likeButton.setOnClickListener(this)
        binding.shareButton.setOnClickListener(this)

        commentViewModel.comments.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                commentsBinding.list.visibility = View.VISIBLE
                commentsBinding.viewMessage.visibility = View.GONE
                adapter.setComments(it)
            } else {
                commentsBinding.list.visibility = View.GONE
                commentsBinding.viewMessage.visibility = View.VISIBLE
            }
        }
        commentViewModel.updateComments(CommentsRepository.CommentType.ARTICLE, id, 1)

        fragmentInputBinding.sendButton.setOnClickListener {
            commentViewModel.leaveComment(
                CommentsRepository.CommentType.ARTICLE,
                id,
                fragmentInputBinding.inputMessage.text.toString().trim()
            )
            fragmentInputBinding.inputMessage.setText("")
        }
        commentViewModel.status.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.description, Toast.LENGTH_SHORT).show()
        }
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
            bundle.putString("url", URLHelper.getApiUrl(articleModel.url?.substring(1)))
            webFragment.arguments = bundle
            return webFragment
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.likeButton) {
            newsViewModel.likeArticle(id)
        } else if (v?.id == R.id.shareButton) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent(intent))
        }
    }

    override fun onClick(comment: CommentModel) {
        //TODO("Not yet implemented")
    }

    override fun onLongClick(comment: CommentModel) {
        val builder = AlertDialog.Builder(requireContext(), R.style.PDADialogStyle)
        builder.setTitle(getString(R.string.any_select_action))
        builder.setItems(resources.getStringArray(R.array.comment_actions))
        { dialogInterface: DialogInterface?, i: Int ->
            when (i) {
                1 -> {
                    //reply
                    navigationPresenter.addFragment(ChatFragment.with(comment.author), true)
                }

                //profile
                2 -> navigationPresenter.addFragment(
                    UserProfileFragment.of(comment.author.id),
                    true
                )

                //like
                3 -> {
                    commentViewModel.likeComment(comment.id)
                }

                else -> {
                    val login = "@" + comment.author.login + ", "
                    var input: String = fragmentInputBinding.inputMessage.text.toString()
                    if (!input.startsWith("@")) {
                        fragmentInputBinding.inputMessage.text?.insert(0, login)
                        return@setItems
                    }
                    var textToReplace = input
                    var j = 0
                    while (j < input.length) {
                        if (Character.isSpaceChar(input[j])) {
                            textToReplace = input.substring(0, j)
                            break
                        }
                        j++
                    }
                    input = input.replaceFirst(
                        textToReplace.toRegex(),
                        "@" + comment.author.login + ", "
                    )
                    fragmentInputBinding.inputMessage.setText(input)
                    navigationPresenter.addFragment(ChatFragment.with(comment.author), true)
                    navigationPresenter.addFragment(
                        UserProfileFragment.of(comment.author.id),
                        true
                    )
                }
            }
        }
        builder.create().show()
    }

    override fun onLinkClick(url: String?) {
        navigationPresenter.addFragment(of("Переход по ссылке", url), true)
    }
}