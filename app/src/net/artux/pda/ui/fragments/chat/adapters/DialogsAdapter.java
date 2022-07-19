package net.artux.pda.ui.fragments.chat.adapters;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.models.Checker;
import net.artux.pda.models.UserMessage;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation;
import net.artux.pda.ui.fragments.chat.ChatFragment;
import net.artux.pda.ui.fragments.chat.Dialog;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.util.ArrayList;
import java.util.List;

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Dialog> mDialogList = new ArrayList<>();

    private final MainActivity mContext;
    private final FragmentNavigation.Presenter presenter;

    public DialogsAdapter(MainActivity context, FragmentNavigation.Presenter presenter) {
        mContext = context;
        this.presenter = presenter;
    }

    public void setDialogs(List<Dialog> dialogs) {
        mDialogList = dialogs;
        notifyDataSetChanged();
    }

    public void updateDialog(UserMessage message) {
        for (Dialog d :
                mDialogList) {
            if (d.id == message.getId())
                d.lastMessage = message.getLogin() + ": " + message.getContent();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_general_chat, parent, false);
            return new GeneralChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dialog, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                GeneralChatViewHolder generalChatViewHolder = (GeneralChatViewHolder) holder;
                generalChatViewHolder.bind();
                break;
            case 1:
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.bind(mDialogList.get(position - 1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mDialogList != null) return mDialogList.size() + 1;
        else return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements DialogsClickListener {

        ImageView avatarView;
        TextView titleView;
        TextView lastMessage;
        View mainView;

        ViewHolder(View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.avatarDialog);
            titleView = itemView.findViewById(R.id.titleDialog);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            mainView = itemView;
        }

        void bind(Dialog dialog) {
            ProfileHelper.setAvatar(avatarView, dialog.avatar);
            lastMessage.setText(dialog.lastMessage);
            titleView.setText(dialog.title);
            mainView.setOnClickListener(view -> ViewHolder.this.onClick(dialog));
        }

        @Override
        public void onClick(Dialog dialog) {
            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", dialog.type);
            bundle.putInt("c", dialog.id);

            chatFragment.setArguments(bundle);
            presenter.addFragment(chatFragment, true);
        }
    }

    class GeneralChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView generalView;
        TextView groupView;
        TextView createView;
        View mainView;

        GeneralChatViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            generalView = mainView.findViewById(R.id.chat_general);
            groupView = mainView.findViewById(R.id.chat_group);
            createView = mainView.findViewById(R.id.chat_create);
        }

        void bind() {
            generalView.setOnClickListener(this);
            groupView.setOnClickListener(this);
            createView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.chat_general:
                    ChatFragment chatFragment = new ChatFragment();
                    presenter.addFragment(chatFragment, true);
                    break;
                case R.id.chat_group:
                    chatFragment = new ChatFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("group", true);
                    chatFragment.setArguments(bundle);
                    presenter.addFragment(chatFragment, true);
                    break;
                case R.id.chat_create:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Input PdaID.. | Create a conversation");

                    final EditText input = new EditText(mContext);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        ChatFragment chatFragment1 = new ChatFragment();
                        Bundle bundle1 = new Bundle();
                        if (Checker.isInteger(input.getText().toString())) {
                            bundle1.putInt("to", Integer.parseInt(input.getText().toString()));
                            chatFragment1.setArguments(bundle1);
                            presenter.addFragment(chatFragment1, true);
                        }
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    builder.show();
                    break;
            }
        }
    }
}
