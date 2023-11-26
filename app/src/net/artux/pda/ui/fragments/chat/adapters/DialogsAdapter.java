package net.artux.pda.ui.fragments.chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;
import net.artux.pdanetwork.model.ConversationDTO;

import java.util.LinkedList;
import java.util.List;

public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ViewHolder> {

    private List<ConversationDTO> mConversationList;
    private final OnClickListener onClickListener;

    public DialogsAdapter(OnClickListener onClickListener) {
        mConversationList = new LinkedList<>();
        this.onClickListener = onClickListener;
    }

    public void setDialogs(List<ConversationDTO> dialogs) {
        mConversationList = dialogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DialogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(DialogsAdapter.ViewHolder holder, int position) {
        holder.bind(mConversationList.get(position));
    }

    @Override
    public int getItemCount() {
        return mConversationList.size();
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

        void bind(ConversationDTO dialog) {
            ProfileHelper.setAvatar(avatarView, dialog.getIcon());
            lastMessage.setText(dialog.getType().toString());
            titleView.setText(dialog.getTitle());
            mainView.setOnClickListener(view -> onClickListener.onClick(dialog));
            mainView.setOnLongClickListener(view -> onClickListener.onLongClick(dialog));
        }
    }

    public interface OnClickListener {
        void onClick(ConversationDTO model);
        boolean onLongClick(ConversationDTO model);
    }
}
