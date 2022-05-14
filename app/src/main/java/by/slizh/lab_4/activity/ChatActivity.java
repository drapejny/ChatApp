package by.slizh.lab_4.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import by.slizh.lab_4.R;
import by.slizh.lab_4.adapters.ChatAdapter;
import by.slizh.lab_4.databinding.ActivityChatBinding;
import by.slizh.lab_4.entity.ChatMessage;
import by.slizh.lab_4.entity.User;
import by.slizh.lab_4.utils.Constants;
import by.slizh.lab_4.utils.PreferenceManager;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private String dialogDocument;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("On create");
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        setReceiverDetails();
        init();
        listenMessages();
        System.out.println(chatMessages.size());

    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());

        binding.sendImage.setOnClickListener(view -> sendMessage());
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(preferenceManager.getString(Constants.KEY_IMAGE)),
                getBitmapFromEncodedString(receiverUser.getImage()),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.msgRecycler.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        //// TODO: 12.05.2022 добавить фотки и файлы в сообщение
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.getId());
        message.put(Constants.KEY_MESSAGE, binding.msgEditText.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        message.put(Constants.KEY_IS_VIEWED, false);
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        updateDialog(message);
        binding.msgEditText.setText(null);
    }

    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.getId())
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.getId())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage(); //// TODO: 12.05.2022 изменить на конструктор
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, Comparator.comparing(ChatMessage::getDateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.msgRecycler.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.msgRecycler.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    });

    private void updateDialog(HashMap<String, Object> message) {
        String senderId = preferenceManager.getString(Constants.KEY_USER_ID);
        String receiverId = receiverUser.getId();

        // Fucking update dialog
        database.collection(Constants.KEY_COLLECTION_DIALOGS)
                .whereEqualTo(Constants.KEY_FIRST_USER_ID, senderId)
                .whereEqualTo(Constants.KEY_SECOND_USER_ID, receiverId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            System.out.println("Empty");
                            database.collection(Constants.KEY_COLLECTION_DIALOGS)
                                    .whereEqualTo(Constants.KEY_FIRST_USER_ID, receiverId)
                                    .whereEqualTo(Constants.KEY_SECOND_USER_ID, senderId)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.getResult().isEmpty()) {
                                            System.out.println("Empty");
                                            HashMap<String, Object> dialog = new HashMap<>();
                                            dialog.put(Constants.KEY_FIRST_USER_ID, senderId);
                                            dialog.put(Constants.KEY_SECOND_USER_ID, receiverId);
                                            dialog.put(Constants.KEY_FIRST_USER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
                                            dialog.put(Constants.KEY_SECOND_USER_IMAGE, receiverUser.getImage());
                                            dialog.put(Constants.KEY_FIRST_USER_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME
                                                    + " " + preferenceManager.getString(Constants.KEY_LAST_NAME)));
                                            dialog.put(Constants.KEY_SECOND_USER_NAME, receiverUser.getFirstName()
                                                    + " " + receiverUser.getLastName());
                                            dialog.put(Constants.KEY_LAST_MESSAGE, message.get(Constants.KEY_MESSAGE));
                                            dialog.put(Constants.KEY_SENDER_ID, senderId);
                                            dialog.put(Constants.KEY_TIMESTAMP, message.get(Constants.KEY_TIMESTAMP));
                                            dialog.put(Constants.KEY_MESSAGE_COUNT, 1);
                                            database.collection(Constants.KEY_COLLECTION_DIALOGS).add(dialog);
                                        } else {
                                            System.out.println("Not empty");
                                            HashMap<String, Object> changes = new HashMap<>();
                                            changes.put(Constants.KEY_LAST_MESSAGE, message.get(Constants.KEY_MESSAGE));
                                            changes.put(Constants.KEY_SENDER_ID, senderId);
                                            changes.put(Constants.KEY_TIMESTAMP, message.get(Constants.KEY_TIMESTAMP));
                                            DocumentSnapshot document = task1.getResult().getDocuments().get(0);
                                            if (document.get(Constants.KEY_SENDER_ID).equals(senderId)) {
                                                changes.put(Constants.KEY_MESSAGE_COUNT, FieldValue.increment(1));
                                            } else {
                                                changes.put(Constants.KEY_MESSAGE_COUNT, 1);
                                            }
                                            database.collection(Constants.KEY_COLLECTION_DIALOGS)
                                                    .document(document.getId())
                                                    .update(changes);
                                        }
                                    });
                        } else {
                            System.out.println("Not empty");
                            HashMap<String, Object> changes = new HashMap<>();
                            changes.put(Constants.KEY_LAST_MESSAGE, message.get(Constants.KEY_MESSAGE));
                            changes.put(Constants.KEY_SENDER_ID, senderId);
                            changes.put(Constants.KEY_TIMESTAMP, message.get(Constants.KEY_TIMESTAMP));
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            if (document.get(Constants.KEY_SENDER_ID).equals(senderId)) {
                                changes.put(Constants.KEY_MESSAGE_COUNT, FieldValue.increment(1));
                            } else {
                                changes.put(Constants.KEY_MESSAGE_COUNT, 1);
                            }
                            database.collection(Constants.KEY_COLLECTION_DIALOGS)
                                    .document(document.getId())
                                    .update(changes);
                        }
                    }
                });
    }


    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private void setReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.userNameText.setText(receiverUser.getFirstName() + " " + receiverUser.getLastName());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


    @Override
    protected void onPause() {
        System.out.println("On pause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        System.out.println("On restart");
        super.onRestart();
    }
}