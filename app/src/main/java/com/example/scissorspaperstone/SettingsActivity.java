package com.example.scissorspaperstone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private SharedPreferences gamePrefs;
    private int currentWinPoints;
    private TextView winPointsText;  // Текст, отображающий кол-во очков

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        mPrefs = getSharedPreferences("game_settings", MODE_PRIVATE);
        gamePrefs = getSharedPreferences("game_info", MODE_PRIVATE);
        currentWinPoints = mPrefs.getInt("win_points", 1);

        SeekBar seekBar = findViewById(R.id.win_points_seekbar);
        seekBar.setProgress(currentWinPoints - 1);

        winPointsText = findViewById(R.id.win_points_value);
        updateWinPointsText(currentWinPoints);

        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(this::clickBackButton);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateWinPointsText(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateWinPoints(seekBar.getProgress());
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPrefs.getInt("win_points", 1) == currentWinPoints)
            return;
        // Перезаписываем настройки игры
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("win_points", currentWinPoints);
        ed.apply();
        // Удаляем данные о текущей игре
        ed = gamePrefs.edit();
        ed.clear();
        ed.apply();
    }

    public void updateWinPoints(int value){
        currentWinPoints = value + 1;
    }

    public void updateWinPointsText(int value){
        winPointsText.setText(String.valueOf(value));
    }

    public void clickBackButton(View view){
        finish();
    }
}
