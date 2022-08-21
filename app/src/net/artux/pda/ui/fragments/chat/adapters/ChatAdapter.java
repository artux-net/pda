package net.artux.pda.ui.fragments.chat.adapters;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.UserMessage;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<UserMessage> messages;
    private final MessageClickListener listener;

    public ChatAdapter(MessageClickListener listener){
        this.listener = listener;
        messages = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<UserMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearItems() {
        messages.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(UserMessage userMessage){
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

    class ViewHolder extends RecyclerView.ViewHolder{

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

        @SuppressLint("SetTextI18n")
        void bind(UserMessage userMessage){
            itemView.setOnLongClickListener(view -> {
                listener.onLongClick(userMessage);
                return false;
            });
            itemView.setOnClickListener(view -> listener.onClick(userMessage));
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());
            Instant instant = userMessage.getTimestamp();

            nicknameView.setText(userMessage.getLogin());
            messageView.setText(Html.fromHtml(userMessage.getContent()));

            if (userMessage.getPdaId() < 0 || userMessage.getGang().getId() < 0){
                infoView.setText(" [PDA ###]"
                        + " - " + outputFormat.format(instant));
            }else {
                infoView.setText(" [PDA #" + userMessage.getPdaId() + "] "
                        + infoView.getContext().getResources().getStringArray(R.array.groups)[userMessage.getGang().getId()] // группировка
                        + " - " + outputFormat.format(instant));
            }
            ProfileHelper.setAvatar(avatarView, userMessage.getAvatar());
        }
    }

    public interface MessageClickListener{

        void onClick(UserMessage message);
        void onLongClick(UserMessage message);

    }

}
