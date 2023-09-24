package net.artux.pda.ui.fragments.news

import android.annotation.SuppressLint
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.artux.pda.databinding.ItemChatBinding
import net.artux.pda.databinding.ItemCommentBinding
import net.artux.pda.model.news.CommentModel
import net.artux.pda.utils.init
import java.util.LinkedList

class CommentsAdapter(private val clickListener: OnClickListener) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private var content: MutableList<CommentModel>

    init {
        content = LinkedList()
    }

    fun setComments(comments: List<CommentModel>) {
        content = comments.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder.binding.root, content[position])
    }

    override fun getItemCount(): Int {
        return content.size
    }

    inner class ViewHolder(var binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var chatBinding: ItemChatBinding = ItemChatBinding.bind(binding.root)

        @SuppressLint("SetTextI18n")
        fun bind(mainView: View, commentModel: CommentModel) {
            init(chatBinding, commentModel.author)

            binding.likes.text = commentModel.likes.toString()
            mainView.setOnClickListener { v: View? -> clickListener.onClick(commentModel) }
            setTextViewHTML(chatBinding.message, commentModel.content)
            itemView.setOnClickListener { clickListener.onClick(commentModel) }
            itemView.setOnLongClickListener { clickListener.onLongClick(commentModel); true }
        }

        private fun setTextViewHTML(text: TextView, html: String?) {
            val sequence: CharSequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            val strBuilder = SpannableStringBuilder(sequence)
            val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
            for (span in urls) {
                makeLinkClickable(strBuilder, span)
            }
            text.text = strBuilder
            text.movementMethod = LinkMovementMethod.getInstance()
        }

        protected fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan) {
            val start = strBuilder.getSpanStart(span)
            val end = strBuilder.getSpanEnd(span)
            val flags = strBuilder.getSpanFlags(span)
            val clickable: ClickableSpan =
                object : ClickableSpan() {
                    override fun onClick(view: View) {
                        clickListener.onLinkClick(span.url)
                    }
                }
            strBuilder.setSpan(clickable, start, end, flags)
            strBuilder.removeSpan(span)
        }

    }

    interface OnClickListener {
        fun onClick(comment: CommentModel)
        fun onLongClick(comment: CommentModel)
        fun onLinkClick(url: String?)
    }
}