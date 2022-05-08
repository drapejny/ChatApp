package by.slizh.lab_4.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import by.slizh.lab_4.databinding.ActivityUserProfileBinding;
import by.slizh.lab_4.entity.User;
import by.slizh.lab_4.utils.Constants;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUserData();
        setListeners();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setUserData() {
        User user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        System.out.println(user);
        binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
        binding.nameText.setText(user.getFirstName() + " " + user.getLastName());
        binding.emailText.setText(user.getEmail());
        binding.phoneText.setText(user.getPhone());
        binding.birthdayText.setText(user.getBirthday());
    }
}