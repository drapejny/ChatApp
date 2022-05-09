package by.slizh.lab_4.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import by.slizh.lab_4.R;
import by.slizh.lab_4.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

}