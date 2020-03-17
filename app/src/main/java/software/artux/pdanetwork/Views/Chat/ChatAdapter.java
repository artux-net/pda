package software.artux.pdanetwork.Views.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import software.artux.pdanetwork.Models.LimitedArrayList;
import software.artux.pdanetwork.Models.UserMessage;
import com.devilsoftware.pdanetwork.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static software.artux.pdanetwork.app.App.avatars;

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
        TextView titleView;
        TextView messageView;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.avatar);
            titleView = itemView.findViewById(R.id.title);
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

            titleView.setText(userMessage.senderLogin
                    + " [PDA #" + userMessage.pdaId+"] "
                    + mContext.getResources().getStringArray(R.array.groups)[userMessage.groupId]
                    + " - " + outputFormat.format(localTime));
            messageView.setText(userMessage.message);
            avatarView.setImageDrawable(
                    mContext.getResources().getDrawable(avatars[userMessage.avatarId]));
        }
    }

}
