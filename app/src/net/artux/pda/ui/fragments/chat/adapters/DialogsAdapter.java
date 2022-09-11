package net.artux.pda.ui.fragments.chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.ConversationModel;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.util.LinkedList;
import java.util.List;

public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ViewHolder> {

    private List<ConversationModel> content;
    private final OnClickListener onClickListener;

    public DialogsAdapter(OnClickListener onClickListener) {
        content = new LinkedList<>();
        this.onClickListener = onClickListener;
    }

    public void setDialogs(List<ConversationModel> dialogs) {
        content = dialogs;
        notifyDataSetChanged();
    }

    /*public void updateDialog(UserMessage message) {
        for (ConversationModel d : mDialogList) {
            //todo
            if (d.id == message.getAuthor().getPdaId())
                d.lastMessage = message.getAuthor().getLogin() + ": " + message.getContent();
        }
    }
*/
    @NonNull
    @Override
    public DialogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(DialogsAdapter.ViewHolder holder, int position) {
        holder.bind(content.get(position));
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView avatarView;
        private final TextView titleView;
        private final TextView lastMessage;
        private final View mainView;

        ViewHolder(View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.avatarDialog);
            titleView = itemView.findViewById(R.id.titleDialog);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            mainView = itemView;
        }

        void bind(ConversationModel dialog) {
            ProfileHelper.setAvatar(avatarView, dialog.getAvatar());
            lastMessage.setText(dialog.getLastMessage());
            titleView.setText(dialog.getTitle());
            mainView.setOnClickListener(view -> onClickListener.onClick(dialog));
        }
    }

    public interface OnClickListener {
        void onClick(ConversationModel model);
    }
}
