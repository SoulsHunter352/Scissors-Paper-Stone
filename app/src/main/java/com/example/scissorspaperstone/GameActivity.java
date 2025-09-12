package com.example.scissorspaperstone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final byte STONE = 0;
    private static final byte PAPER = 1;
    private static final byte SCISSORS = 2;
    private static final byte[] choices = {STONE, PAPER, SCISSORS};
    private Random random;

    private SharedPreferences prefs;
    private int winPoints;
    private int playerPoints;
    private int compPoints;
    private TextView playerText;
    private TextView compText;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        random = new Random();
        prefs = getSharedPreferences("game_settings", MODE_PRIVATE);
        winPoints = prefs.getInt("win_points", 1);
        prefs = getSharedPreferences("game_info", MODE_PRIVATE);
        playerPoints = prefs.getInt("player_points", 0);
        compPoints = prefs.getInt("comp_points", 0);

        playerText = findViewById(R.id.player_points);
        compText = findViewById(R.id.computer_points);
        setPointsText();


        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBackButton(v);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void clickBackButton(View view){
        finish();
    }

    public void setPointsText(){
        playerText.setText(String.valueOf(playerPoints));
        compText.setText(String.valueOf(compPoints));
    }

    public void setRoundStatus(String status){
        statusText.setText(status);
    }

    public void simulateRound(byte playerChoice){
        byte compChoice = choices[random.nextInt(choices.length)];
        if(compChoice == playerChoice){
            setRoundStatus("Ничья");
        }
    }
}