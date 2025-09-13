package com.example.scissorspaperstone;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("game_info", MODE_PRIVATE);
        continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("is_continue", true);
            openActivity(GameActivity.class, args);
        });

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> openActivity(SettingsActivity.class, null));

        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("is_continue", false);
            openActivity(GameActivity.class, args);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openActivity(Class<?> cls, Bundle args){
        Intent intent = new Intent(this, cls);
        if(args != null){
            intent.putExtras(args);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pref.getBoolean("is_game_completed", true)){
            continueButton.setVisibility(INVISIBLE);
        }
        else{
            continueButton.setVisibility(VISIBLE);
        }
    }
}