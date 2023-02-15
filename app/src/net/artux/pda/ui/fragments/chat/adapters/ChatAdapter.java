package net.artux.pda.ui.fragments.chat.adapters;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.chat.ChatUpdate;
import net.artux.pda.model.chat.UserMessage;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<UserMessage> messages;
    private final MessageClickListener listener;

    public ChatAdapter(MessageClickListener listener) {
        this.listener = listener;
        messages = new LinkedList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<UserMessage> messages) {
        clearItems();
        this.messages = messages;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearItems() {
        messages.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(ChatUpdate update) {
        if (update.getUpdatesByType(UserMessage.Type.OLD).size() > 0) {
            messages = update.getUpdatesByType(UserMessage.Type.OLD);
        }

        for (UserMessage msg : update.getUpdatesByType(UserMessage.Type.DELETE))
            messages.removeIf(userMessage -> msg.getId().equals(userMessage.getId()));

        for (UserMessage msg : update.getUpdatesByType(UserMessage.Type.UPDATE))
            messages.forEach(userMessage -> {
                if (msg.getId().equals(userMessage.getId())) {
                    userMessage.setContent(msg.getContent());
                }
            });

        messages.addAll(update.getUpdatesByType(UserMessage.Type.NEW));
        messages.addAll(update.getEvents().stream().map(UserMessage::event).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarView;
        TextView nicknameView;
        TextView infoView;
        TextView messageView;

        ViewHolder(View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.avatar);
            nicknameView = itemView.findViewById(R.id.nickname);
            infoView = itemView.findViewById(R.id.info);
            messageView = itemView.findViewById(R.id.message);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.systemDefault());

        @SuppressLint("SetTextI18n")
        void bind(UserMessage userMessage) {
            if (userMessage == null) return;
            Instant instant = userMessage.getTimestamp();
            UserModel userModel = userMessage.getAuthor();

            if (userModel != null) {
                nicknameView.setText(userModel.getName() + " " + userModel.getNickname());
                itemView.setOnLongClickListener(view -> {
                    listener.onLongClick(userMessage);
                    return false;
                });
                itemView.setOnClickListener(view -> listener.onClick(userMessage));
                if (userModel.getPdaId() == null || userModel.getPdaId() < 0 || userModel.getGang() == null) {
                    infoView.setText(" [PDA ###]"
                            + " - " + formatter.format(instant));
                } else {
                    infoView.setText(" [PDA #" + userModel.getPdaId() + "] "
                            + infoView.getContext().getResources().getStringArray(R.array.groups)[userModel.getGang().getId()] // группировка
                            + " - " + formatter.format(instant));
                }

                if (userModel.getRole() != null && userModel.getRole() != UserModel.Role.USER)
                    infoView.setText(infoView.getText() + " " + userModel.getRole());

                ProfileHelper.setAvatar(avatarView, userModel.getAvatar());
            } else {
                nicknameView.setText("System ");
                infoView.setText(formatter.format(instant));
                messageView.setMovementMethod(LinkMovementMethod.getInstance());
            }
            setTextViewHTML(messageView, userMessage.getContent());
        }
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                listener.onLinkClick(span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public interface MessageClickListener {

        void onClick(UserMessage message);

        void onLongClick(UserMessage message);

        void onLinkClick(String url);

    }

}
