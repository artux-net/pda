package net.artux.pda.Views.Chat;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.artux.pda.Models.Dialog;
import net.artux.pda.R;
import net.artux.pda.activities.MainActivity;
import net.artux.pda.app.App;

import java.util.ArrayList;
import java.util.List;

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Dialog> mDialogList = new ArrayList<>();

    private MainActivity mContext;
    private int groupId = 0;

    DialogsAdapter(MainActivity context, int groupId){
        // TODO
        mContext = context;
        this.groupId = groupId;
    }

    public void setDialogs(List<Dialog> dialogs) {
        if (dialogs!=null) mDialogList.addAll(dialogs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (groupId!=0) {
            switch (position) {
                case 0:
                    return 0;
                case 1:
                    return 0;
                case 2:
                    return 0;
            }
        } else {
            switch (position) {
                case 0:
                    return 0;
                case 1:
                    return 0;
            }
        }

        return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==0){
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

        switch (holder.getItemViewType()){
            case 0:

                GeneralChatViewHolder generalChatViewHolder = (GeneralChatViewHolder) holder;
                generalChatViewHolder.bind(position);

                break;
            case 2:

                if(groupId!=0){
                    ViewHolder viewHolder = (ViewHolder) holder;
                    viewHolder.bind(mDialogList.get(position-3));
                } else {
                    ViewHolder viewHolder = (ViewHolder) holder;
                    viewHolder.bind(mDialogList.get(position-2));
                }

                break;
        }


    }

    @Override
    public int getItemCount() {

        int addItems = 2;
        if (groupId!=0)  addItems +=1;
        if(mDialogList!=null) return mDialogList.size()+addItems;
        else return 2;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements DialogsClickListener{

        ImageView avatarView;
        TextView titleView;
        TextView lastMessage;
        View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.avatarDialog);
            titleView = itemView.findViewById(R.id.titleDialog);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            mainView = itemView;
        }

        public void bind(final Dialog dialog){
            avatarView.setImageDrawable(mContext.getResources().
                    getDrawable(App.avatars[dialog.getToPdaAvatarId()]));

            lastMessage.setText(dialog.getLastMessage());
            titleView.setText(dialog.getToPdaLogin() + " PDA #" + dialog.getToPdaId());
            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder.this.onClick(dialog);
                }
            });
        }

        @Override
        public void onClick(Dialog dialog) {
            FragmentTransaction fragmentTransaction = mContext.getFragmentManager().beginTransaction();

            ChatFragment chatFragment = new ChatFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("chatMode", 1);
            bundle.putInt("toPdaId", dialog.getToPdaId());

            chatFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.containerView, chatFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    class GeneralChatViewHolder extends RecyclerView.ViewHolder{

        TextView titleView;
        View mainView;

        public GeneralChatViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            titleView = mainView.findViewById(R.id.titleDialog);
        }

        public void bind(int pos){
            switch (pos){
                case 0:
                    titleView.setText(mContext.getResources().getText(R.string.general_chat_channel));
                    mainView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentTransaction fragmentTransaction = mContext.getFragmentManager().beginTransaction();

                            ChatFragment chatFragment = new ChatFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("chatMode", 0);
                            chatFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.containerView, chatFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 1:
                    titleView.setText(mContext.getResources().getString(R.string.group_chat));
                    mainView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentTransaction fragmentTransaction = mContext.getFragmentManager().beginTransaction();

                            ChatFragment chatFragment = new ChatFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("chatMode", 2);
                            bundle.putInt("group", App.getDataManager().getMember().getGroup());
                            chatFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.containerView, chatFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 2:
                    titleView.setText(mContext.getResources().getText(R.string.write_with_id));
                    mainView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Input PdaID..");

                            final EditText input = new EditText(mContext);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setView(input);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    FragmentTransaction fragmentTransaction = mContext.getFragmentManager().beginTransaction();

                                    ChatFragment chatFragment = new ChatFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("chatMode", 1);

                                    if (Integer.parseInt(input.getText().toString())!=0){
                                        bundle.putInt("toPdaId", Integer.parseInt(input.getText().toString()));
                                        chatFragment.setArguments(bundle);

                                        fragmentTransaction.replace(R.id.containerView, chatFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });
                    break;

            }
        }
    }

}
