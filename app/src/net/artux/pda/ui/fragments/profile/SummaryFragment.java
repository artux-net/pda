package net.artux.pda.ui.fragments.profile;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.UserMessage;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.SummaryAdditionalFragment;
import net.artux.pda.ui.fragments.chat.adapters.ChatAdapter;
import net.artux.pda.ui.viewmodels.SummaryViewModel;

public class SummaryFragment extends BaseFragment implements ChatAdapter.MessageClickListener {

    private ChatAdapter mChatAdapter;
    {
        defaultAdditionalFragment = SummaryAdditionalFragment.class;
    }
    private SummaryViewModel summaryViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (summaryViewModel==null)
            summaryViewModel = getViewModelFactory(this).create(SummaryViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mChatAdapter = new ChatAdapter(this);
        recyclerView.setAdapter(mChatAdapter);
        reset();

        summaryViewModel.getSummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary!=null){
                navigationPresenter.setTitle("Сводка от " + summary.getTitle());
                mChatAdapter.setItems(summary.getMessages());
                if (summary.getMessages().size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.viewMessage).setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    view.findViewById(R.id.viewMessage).setVisibility(View.VISIBLE);
                }
            }else {
                reset();
            }
        });

    }

    @Override
    public void onClick(UserMessage message) {

    }

    @Override
    public void onLongClick(UserMessage message) {

    }

    void reset(){
        navigationPresenter.setTitle("Выберете сводку в меню справа..");
        mChatAdapter.clearItems();
    }
}
