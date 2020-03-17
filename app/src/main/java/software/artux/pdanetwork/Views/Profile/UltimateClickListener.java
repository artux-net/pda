package software.artux.pdanetwork.Views.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devilsoftware.pdanetwork.R;

import software.artux.pdanetwork.Models.profile.Item;
import software.artux.pdanetwork.Views.Addirional.AdditionalFragment;
import software.artux.pdanetwork.Views.Encyclopedia.EncyclopediaFragment;
import software.artux.pdanetwork.activities.MainActivity;
import software.artux.pdanetwork.activities.MainActivityController;

public class UltimateClickListener implements View.OnClickListener {

    private Item item;
    private Context context;
    private MainActivity mainActivity;

    UltimateClickListener(Item item, Context context, MainActivity mainActivity){
        this.item = item;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);

        LinearLayout linearLayout = view.findViewById(R.id.desc);
        TextView textView = new TextView(context);
        textView.setLayoutParams(params);
        textView.setText("Цена:" + item.price);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(params);
        textView.setText("Цена:" + item.price);
        linearLayout.addView(textView);

        Button button = view.findViewById(R.id.d);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EncyclopediaFragment encyclopediaFragment = new EncyclopediaFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.library_id);
                bundle.putInt("type", item.type);

                encyclopediaFragment.setArguments(bundle);
                AdditionalFragment additionalFragment = new AdditionalFragment();
                additionalFragment.setArguments(bundle);

                mainActivity.setTitle("Энциклопедия");
                mainActivity.setupMainFragment(encyclopediaFragment);
                mainActivity.setAdditionalTitle("Разделы");
                mainActivity.setupAdditionalFragment(additionalFragment);

            }
        });

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(400, 300);
    }
}
