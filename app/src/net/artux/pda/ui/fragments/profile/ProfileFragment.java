package net.artux.pda.ui.fragments.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.PdaAlertDialog;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.ChatFragment;
import net.artux.pda.ui.fragments.profile.adapters.GroupsAdapter;
import net.artux.pdalib.Profile;
import net.artux.pdalib.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private Profile profile;
    RecyclerView recyclerView;
    GroupsAdapter groupsAdapter = new GroupsAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getString(R.string.profile));
            navigationPresenter.setLoadingState(true);
        }

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupsAdapter);
        int pda = 0;
        if(App.getDataManager().getMember()!=null) {
            setProfile(new Profile(App.getDataManager().getMember()), view);

            pda = App.getDataManager().getMember().getPdaId();
        }
        if (getArguments()!=null)
            pda = getArguments().getInt("pdaId", App.getDataManager().getMember().getPdaId());
        update(pda, view);

    }
    
    void update(int pdaId, View view){
        App.getRetrofitService().getPdaAPI().getProfile(pdaId).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Profile profile = response.body();
                if (navigationPresenter!=null)
                    navigationPresenter.setLoadingState(false);
                if (profile != null)
                    setProfile(profile, view);
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable throwable) {
                navigationPresenter.setLoadingState(false);
                Timber.e(throwable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    public void setProfile(Profile profile, View view1) {
        this.profile = profile;
        ImageView avatar = view1.findViewById(R.id.profile_avatar);
        avatar.setImageDrawable(ProfileHelper.getAvatar(profile, getContext()));
        ((TextView)view1.findViewById(R.id.profile_login)).setText(profile.getLogin());
        ((TextView)view1.findViewById(R.id.profile_group)).setText(getString(R.string.group_p, ProfileHelper.getGroup(profile, getContext())));
        ((TextView)view1.findViewById(R.id.profile_location)).setText(getString(R.string.location_p, profile.getLocation()));
        ((TextView)view1.findViewById(R.id.profile_time)).setText(getString(R.string.in_zone_time_p, ProfileHelper.getDays(profile)));
        ((TextView)view1.findViewById(R.id.profile_rang)).setText(getString(R.string.rang_p, ProfileHelper.getRang(profile,view1.getContext())));
        ((TextView)view1.findViewById(R.id.profile_rating)).setText(getString(R.string.rating_p, String.valueOf(profile.getXp())));

        Button friends = view1.findViewById(R.id.profile_friends);
        friends.setText(view1.getContext().getString(R.string.friends, String.valueOf(profile.getFriends())));
        friends.setOnClickListener(this);
        Button requests = view1.findViewById(R.id.profile_requests);
        requests.setText(view1.getContext().getString(R.string.subscribers, String.valueOf(profile.getSubs())));
        requests.setOnClickListener(this);

        groupsAdapter.setRelations(profile.getRelations());
        recyclerView.setVisibility(View.VISIBLE);
        view1.findViewById(R.id.viewMessage).setVisibility(View.GONE);

        Button friendButton = view1.findViewById(R.id.profile_friend);
        Button subsButton = view1.findViewById(R.id.requests);
        Button messageButton = view1.findViewById(R.id.write_message);
        messageButton.setOnClickListener(this);
        if (App.getDataManager().getMember().getPdaId()!=profile.getPdaId()) {
            switch (profile.getFriendStatus()) {
                case 0:
                    friendButton.setText(R.string.add_friend);
                    friendButton.setOnClickListener(view -> {
                        PdaAlertDialog pdaAlertDialog = new PdaAlertDialog(getContext(), (ViewGroup) view1, R.style.AlertDialogStyle);
                        pdaAlertDialog.setTitle(R.string.add_friend_q);
                        pdaAlertDialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getRetrofitService().getPdaAPI().requestFriend(profile.getPdaId()).enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        Status status = response.body();
                                        if (status!=null)
                                            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_SHORT).show();
                                        update(profile.getPdaId(), view1);
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable throwable) {

                                    }
                                });
                            }
                        });
                        pdaAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        pdaAlertDialog.show();
                    });

                    break;
                case 1:
                    friendButton.setText(getString(R.string.is_friend, profile.getName()));
                    friendButton.setOnClickListener(view -> {
                        AlertDialog.Builder pdaAlertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                        pdaAlertDialog.setTitle(R.string.remove_friend_q);
                        pdaAlertDialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getRetrofitService().getPdaAPI().requestFriend(profile.getPdaId()).enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        Status status = response.body();
                                        if (status!=null)
                                            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_SHORT).show();
                                        update(profile.getPdaId(), view1);
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable throwable) {

                                    }
                                });
                            }
                        });
                        pdaAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        pdaAlertDialog.show();
                    });

                    break;
                case 2:
                    friendButton.setText(getString(R.string.is_sub, profile.getName()));
                    friendButton.setOnClickListener(view -> {
                        AlertDialog.Builder pdaAlertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                        pdaAlertDialog.setTitle(R.string.add_friend_q);
                        pdaAlertDialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getRetrofitService().getPdaAPI().requestFriend(profile.getPdaId()).enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        Status status = response.body();
                                        if (status!=null)
                                            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_SHORT).show();
                                        update(profile.getPdaId(), view1);
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable throwable) {

                                    }
                                });
                            }
                        });
                        pdaAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        pdaAlertDialog.show();
                    });
                    break;
                case 3:
                    friendButton.setText(getString(R.string.requested));
                    friendButton.setOnClickListener(view -> {
                        AlertDialog.Builder pdaAlertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
                        pdaAlertDialog.setTitle(R.string.cancel_friend_q);
                        pdaAlertDialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getRetrofitService().getPdaAPI().requestFriend(profile.getPdaId()).enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        Status status = response.body();
                                        if (status!=null)
                                            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_SHORT).show();
                                        update(profile.getPdaId(), view1);
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable throwable) {

                                    }
                                });
                            }
                        });
                        pdaAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        pdaAlertDialog.show();
                    });
                    break;
            }
            friendButton.setVisibility(View.VISIBLE);
            messageButton.setVisibility(View.VISIBLE);
            subsButton.setVisibility(View.GONE);
        }else{
            friendButton.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
            subsButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        FriendsFragment friendsFragment = new FriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pdaId", profile.getPdaId());

        switch (view.getId()){
            case R.id.profile_friends:
                bundle.putInt("type", 0);
                friendsFragment.setArguments(bundle);
                navigationPresenter.addFragment(friendsFragment, true);
                break;
            case R.id.profile_requests:
                bundle.putInt("type", 1);
                friendsFragment.setArguments(bundle);
                navigationPresenter.addFragment(friendsFragment, true);
                break;
            case R.id.write_message:
                bundle.putInt("to", profile.getPdaId());
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                navigationPresenter.addFragment(chatFragment, true);
                break;
        }
    }

}
