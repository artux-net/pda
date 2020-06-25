package net.artux.pda.Views.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.Models.LimitedArrayList;
import net.artux.pda.Models.UserMessage;
import net.artux.pda.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static net.artux.pda.app.App.avatars;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    private LimitedArrayList<UserMessage> mMessageList = new LimitedArrayList<>();

    private Context mContext;

    ChatAdapter(Context context){
        mContext = context;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mMessageList.get(position));
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
        public void bind(UserMessage userMessage){
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date localTime = new Date();
            try {
                localTime = outputFormat.parse(userMessage.time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal;
            TimeZone tz;
            cal = Calendar.getInstance();
            tz = cal.getTimeZone();
            outputFormat.setTimeZone(tz);

            nicknameView.setText(userMessage.senderLogin);
            infoView.setText(" [PDA #" + userMessage.pdaId+"] "
                    + mContext.getResources().getStringArray(R.array.groups)[userMessage.groupId]
                    + " - " + outputFormat.format(localTime));
            messageView.setText(userMessage.message);
            avatarView.setImageDrawable(
                    mContext.getResources().getDrawable(avatars[userMessage.avatarId]));
        }
    }

}
