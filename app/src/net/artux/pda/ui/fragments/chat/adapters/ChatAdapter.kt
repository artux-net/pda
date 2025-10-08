package net.artux.pda.ui.fragments.chat.adapters

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
import net.artux.pda.model.chat.ChatUpdate
import net.artux.pda.model.chat.UserMessage
import net.artux.pda.utils.init
import java.util.function.Consumer
import java.util.stream.Collectors

open class ChatAdapter(private val listener: MessageClickListener) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var messages: MutableList<UserMessage>

    init {
        messages = mutableListOf()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(messages: MutableList<UserMessage>) {
        clearItems()
        this.messages = messages
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItems() {
        messages.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(update: ChatUpdate) {
        if (update.getUpdatesByType(UserMessage.Type.OLD).isNotEmpty()) {
            messages = update.getUpdatesByType(UserMessage.Type.OLD).toMutableList()
        }
        for ((id) in update.getUpdatesByType(UserMessage.Type.DELETE))
            messages.removeIf { (id1): UserMessage -> id == id1 }
        for ((id, _, content) in update.getUpdatesByType(UserMessage.Type.UPDATE))
            messages.forEach(
                Consumer { userMessage: UserMessage ->
                    if (id == userMessage.id) {
                        userMessage.content = content
                    }
                })
        messages.addAll(update.getUpdatesByType(UserMessage.Type.NEW))
        messages.addAll(update.events!!.stream().map { UserMessage.event(it) }
            .collect(Collectors.toList()))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemChatBinding = ItemChatBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(userMessage: UserMessage?) {
            if (userMessage == null) return

            init(binding, userMessage.author, userMessage.timestamp)
            setTextViewHTML(binding.message, userMessage.content)
            itemView.setOnClickListener { listener.onClick(userMessage) }
            itemView.setOnLongClickListener { listener.onLongClick(userMessage); true }
        }
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
                    listener.onLinkClick(span.url)
                }
            }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    interface MessageClickListener {
        fun onClick(message: UserMessage?)
        fun onLongClick(message: UserMessage?)
        fun onLinkClick(url: String?)
    }
}