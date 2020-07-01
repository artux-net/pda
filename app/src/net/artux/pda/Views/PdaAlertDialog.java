package net.artux.pda.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import net.artux.pda.R;

public class PdaAlertDialog extends AlertDialog.Builder {

    private View view;

    public PdaAlertDialog(Context context) {
        super(context);
        set(context);
    }

    public PdaAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        set(context);
    }

    private void set(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);

        view.setMinimumWidth((int) (context.getResources().getDisplayMetrics().widthPixels * 0.6f));
        view.setMinimumHeight((int) (context.getResources().getDisplayMetrics().heightPixels * 0.6f));
        setView(view);
    }

    public void addView(View v){
        LinearLayout layout = view.findViewById(R.id.dialog_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.addView(v, params);
        setView(view);
    }

    public void addButton(String text, View.OnClickListener onClickListener){
        Button button = new Button(getContext());
        button.setText(text);
        button.setBackgroundResource(R.drawable.border_background);
        button.setOnClickListener(onClickListener);
        LinearLayout layout = view.findViewById(R.id.dialog_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.addView(button, params);
    }
}

