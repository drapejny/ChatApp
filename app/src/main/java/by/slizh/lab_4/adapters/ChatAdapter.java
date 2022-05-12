package by.slizh.lab_4.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.slizh.lab_4.databinding.ItemContainerReceivedMsgBinding;
import by.slizh.lab_4.databinding.ItemContainerSentMsgBinding;
import by.slizh.lab_4.entity.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final Bitmap senderProfileImage;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap senderProfileImage, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.senderProfileImage = senderProfileImage;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position), senderProfileImage);
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }

    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMsgBinding binding;

         SentMessageViewHolder(ItemContainerSentMsgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage, Bitmap senderProfileImage) {
            //// TODO: 12.05.2022 добавить картинку и файл, а также решить вопрос с отображаемым временем
            binding.messageText.setText(chatMessage.getMessage());
            binding.msgTimeText.setText(chatMessage.getDateTime());
            binding.imageMsgProfile.setImageBitmap(senderProfileImage);

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMsgBinding binding;

         ReceivedMessageViewHolder(ItemContainerReceivedMsgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.messageText.setText(chatMessage.getMessage());
            binding.msgTimeText.setText(chatMessage.getDateTime());
            binding.imageMsgProfile.setImageBitmap(receiverProfileImage);
        }
    }
}
