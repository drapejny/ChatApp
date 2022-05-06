package by.slizh.lab_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class SignInActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button signInButton;
    TextView signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //// TODO: 06.05.2022 убрать
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        signUpTextView = findViewById(R.id.signUpTextView);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }
}