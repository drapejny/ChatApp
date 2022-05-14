package by.slizh.lab_4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import by.slizh.lab_4.databinding.ItemContainerDiaologBinding;
import by.slizh.lab_4.entity.Dialog;
import by.slizh.lab_4.entity.User;
import by.slizh.lab_4.utils.Base64Coder;

public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.DialogViewHolder> {

    private final List<Dialog> dialogs;
    private final String userId;

    public DialogsAdapter(List<Dialog> dialogs, String userId) {
        this.dialogs = dialogs;
        this.userId = userId;
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogViewHolder(
                ItemContainerDiaologBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder holder, int position) {
        holder.setData(dialogs.get(position));
    }

    @Override
    public int getItemCount() {
        return dialogs.size();
    }

    class DialogViewHolder extends RecyclerView.ViewHolder {

        ItemContainerDiaologBinding binding;

        public DialogViewHolder(ItemContainerDiaologBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Dialog dialog) {
            binding.imageProfile.setImageBitmap(Base64Coder.decode(dialog.getUserImage()));
            binding.nameTextView.setText(dialog.getUserName());
            binding.msgText.setText(dialog.getLastMessage());
            if (dialog.getMessageCount() == 0 || dialog.getSenderId().equals(userId)) {
                binding.msgCountText.setVisibility(View.INVISIBLE);
            } else {
                binding.msgCountText.setText(Long.toString(dialog.getMessageCount()));
                binding.msgCountText.setVisibility(View.VISIBLE);
            }
            if (dialog.isOnline()) {
                binding.onlineIndicator.setVisibility(View.VISIBLE);
            } else {
                binding.onlineIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }
}
