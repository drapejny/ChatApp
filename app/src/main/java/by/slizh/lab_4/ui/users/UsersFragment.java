package by.slizh.lab_4.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import by.slizh.lab_4.activity.UserProfileActivity;
import by.slizh.lab_4.adapters.UsersAdapter;
import by.slizh.lab_4.databinding.FragmentUsersBinding;
import by.slizh.lab_4.entity.User;
import by.slizh.lab_4.listener.UserListener;
import by.slizh.lab_4.utils.Constants;
import by.slizh.lab_4.utils.PreferenceManager;

public class UsersFragment extends Fragment implements UserListener {

    private FragmentUsersBinding binding;
    private PreferenceManager preferenceManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUsersBinding.inflate(inflater, container, false);
        preferenceManager = new PreferenceManager(getContext());
        getUsers();
        setListeners();

        View root = binding.getRoot();
        return root;
    }

    private void setListeners(){
        // TODO: 08.05.2022 добавить листенеры
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User(
                                    queryDocumentSnapshot.getString(Constants.KEY_EMAIL),
                                    queryDocumentSnapshot.getString(Constants.KEY_PHONE),
                                    queryDocumentSnapshot.getString(Constants.KEY_FIRST_NAME),
                                    queryDocumentSnapshot.getString(Constants.KEY_LAST_NAME),
                                    queryDocumentSnapshot.getString(Constants.KEY_BIRTHDAY),
                                    queryDocumentSnapshot.getString(Constants.KEY_IMAGE),
                                    queryDocumentSnapshot.getLong(Constants.KEY_AVAILABILITY),
                                    queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                            users.add(user);
                        }
                        System.out.println(1);
                        if (users.size() > 0) {
                            System.out.println(2);
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                        }
                    }
                });
    }

    @Override
    public void onUserClicked(User user){
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        //// // TODO: 08.05.2022 финиш у фрагмета нужен не нужен хз как поступить
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}