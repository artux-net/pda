package net.artux.pda.ui.fragments.chat.adapters;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.UserMessage;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

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
    public void addMessage(UserMessage userMessage) {
        messages.add(userMessage);
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
                nicknameView.setText(userModel.getLogin());
                itemView.setOnLongClickListener(view -> {
                    listener.onLongClick(userMessage);
                    return false;
                });
                itemView.setOnClickListener(view -> listener.onClick(userMessage));
                if (userModel.getPdaId() < 0 || userModel.getGang() == null) {
                    infoView.setText(" [PDA ###]"
                            + " - " + formatter.format(instant));
                } else {
                    infoView.setText(" [PDA #" + userModel.getPdaId() + "] "
                            + infoView.getContext().getResources().getStringArray(R.array.groups)[userModel.getGang().getId()] // группировка
                            + " - " + formatter.format(instant));
                }
                ProfileHelper.setAvatar(avatarView, userModel.getAvatar());
            } else {
                nicknameView.setText("System ");
                infoView.setText(formatter.format(instant));
                messageView.setMovementMethod(LinkMovementMethod.getInstance());
            }
            messageView.setText(Html.fromHtml(userMessage.getContent(), Html.FROM_HTML_MODE_LEGACY));
        }
    }

    public interface MessageClickListener {

        void onClick(UserMessage message);

        void onLongClick(UserMessage message);

    }

}
