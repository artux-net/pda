package net.artux.pda.ui.fragments.chat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.ui.PdaAlertDialog;
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation;
import net.artux.pda.ui.fragments.chat.ChatFragment;
import net.artux.pda.ui.fragments.profile.ProfileFragment;
import net.artux.pdalib.LimitedArrayList;
import net.artux.pdalib.UserMessage;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import static net.artux.pda.app.App.avatars;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final LimitedArrayList<UserMessage> mMessageList = new LimitedArrayList<>();

    private final Context mContext;
    private final FragmentNavigation.Presenter presenter;

    public ChatAdapter(Context context, FragmentNavigation.Presenter presenter){
        mContext = context;
        this.presenter = presenter;
    }

    public void setItems(LimitedArrayList<UserMessage> messages) {
        mMessageList.addAll(messages);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mMessageList.clear();
        notifyDataSetChanged();
    }

    public void addMessage(UserMessage userMessage){
        mMessageList.add(userMessage);
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
        holder.bind(mMessageList.get(position), presenter);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
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
        void bind(UserMessage userMessage, FragmentNavigation.Presenter presenter){
            itemView.setOnLongClickListener(view -> {
                PdaAlertDialog builder = new PdaAlertDialog(mContext, (LinearLayout) itemView);
                builder.addButton("Перейти к диалогу", view12 -> {
                    ChatFragment chatFragment1 = new ChatFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("to", userMessage.pdaId);
                    chatFragment1.setArguments(bundle1);
                    presenter.addFragment(chatFragment1, true);
                });
                builder.addButton("Посмотреть профиль", view1 -> {
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pdaId",userMessage.pdaId);
                    profileFragment.setArguments(bundle);
                    if (presenter!=null)
                        presenter.addFragment(profileFragment, true);
                });
                builder.show();

                return false;
            });
            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pdaId",userMessage.pdaId);
                    profileFragment.setArguments(bundle);
                    if (presenter!=null)
                        presenter.addFragment(profileFragment, true);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());
            Instant instant = new Instant(userMessage.time);
            DateTime time = instant.toDateTime().toDateTime(DateTimeZone.getDefault());


            nicknameView.setText(userMessage.senderLogin);
            infoView.setText(" [PDA #" + userMessage.pdaId+"] "
                    + mContext.getResources().getStringArray(R.array.groups)[userMessage.groupId]
                    + " - " + outputFormat.format(time.toDate()));
            messageView.setText(Html.fromHtml(userMessage.message));
            avatarView.setImageDrawable(ContextCompat.getDrawable(mContext, avatars[userMessage.avatarId]));
        }
    }

}
