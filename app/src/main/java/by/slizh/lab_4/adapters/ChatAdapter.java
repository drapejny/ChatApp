package by.slizh.lab_4.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import by.slizh.lab_4.R;
import by.slizh.lab_4.activity.ImageActivity;
import by.slizh.lab_4.databinding.ItemContainerReceivedMsgBinding;
import by.slizh.lab_4.databinding.ItemContainerSentMsgBinding;
import by.slizh.lab_4.entity.ChatMessage;
import by.slizh.lab_4.utils.Constants;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final Bitmap senderProfileImage;
    private final Bitmap receiverProfileImage;
    private final String senderId;
    private final Context context;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap senderProfileImage, Bitmap receiverProfileImage, String senderId, Context context) {
        this.chatMessages = chatMessages;
        this.senderProfileImage = senderProfileImage;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
        this.context = context;
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
                    ),
                    context
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ),
                    context
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
        private final Context context;

        SentMessageViewHolder(ItemContainerSentMsgBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        void setData(ChatMessage chatMessage, Bitmap senderProfileImage) {
            if (chatMessage.isWithImage()) {
                binding.messageImage.setVisibility(View.VISIBLE);
                binding.messageImage.setImageResource(R.drawable.image_gallery);
            } else {
                binding.messageImage.setVisibility(View.GONE);
            }
            if (chatMessage.isWithFile()) {
                binding.fileLayout.setVisibility(View.VISIBLE);
            } else {
                binding.fileLayout.setVisibility(View.GONE);
            }
            if (chatMessage.getImageName() != null) {
                StorageReference imageReference = FirebaseStorage.getInstance()
                        .getReference().child("uploads/" + chatMessage.getMessageId()
                                + "/image/" + chatMessage.getImageName());
                imageReference.getBytes(1024 * 1024 * 1024)
                        .addOnSuccessListener(bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            binding.messageImage.setImageBitmap(bitmap);
                            binding.messageImage.setOnClickListener(view -> {
                                Intent intent = new Intent(context, ImageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constants.KEY_IMAGE, bytes);
                                intent.putExtra(Constants.KEY_IMAGE_NAME, chatMessage.getImageName());
                                context.startActivity(intent);
                            });
                        })
                        .addOnFailureListener(failure -> System.out.println("FAILURE"));

            }
            if (chatMessage.getFileName() != null) {
                binding.attachedFileName.setText(chatMessage.getFileName());
            }
            binding.messageText.setText(chatMessage.getMessage());
            binding.msgTimeText.setText(chatMessage.getDateTime());
            binding.imageMsgProfile.setImageBitmap(senderProfileImage);

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMsgBinding binding;
        private final Context context;

        ReceivedMessageViewHolder(ItemContainerReceivedMsgBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            if (chatMessage.isWithImage()) {
                binding.messageImage.setVisibility(View.VISIBLE);
                binding.messageImage.setImageResource(R.drawable.image_gallery);
            } else {
                binding.messageImage.setVisibility(View.GONE);
            }
            if (chatMessage.isWithFile()) {
                binding.fileLayout.setVisibility(View.VISIBLE);
            } else {
                binding.fileLayout.setVisibility(View.GONE);
            }
            if (chatMessage.getImageName() != null) {
                binding.messageImage.setVisibility(View.VISIBLE);
                StorageReference imageReference = FirebaseStorage.getInstance()
                        .getReference().child("uploads/" + chatMessage.getMessageId()
                                + "/image/" + chatMessage.getImageName());
                imageReference.getBytes(1024 * 1024 * 1024)
                        .addOnSuccessListener(bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            binding.messageImage.setImageBitmap(bitmap);
                            binding.messageImage.setOnClickListener(view -> {
                                Intent intent = new Intent(context, ImageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constants.KEY_IMAGE, bytes);
                                intent.putExtra(Constants.KEY_IMAGE_NAME, chatMessage.getImageName());
                                context.startActivity(intent);
                            });
                        })
                        .addOnFailureListener(failure -> System.out.println("FAILURE"));

            }
            if (chatMessage.getFileName() != null) {
                binding.attachedFileName.setText(chatMessage.getFileName());
            }
            binding.messageText.setText(chatMessage.getMessage());
            binding.msgTimeText.setText(chatMessage.getDateTime());
            binding.imageMsgProfile.setImageBitmap(receiverProfileImage);
        }
    }
}
