package com.example.scissorspaperstone;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity implements ResultDialogFragment.ResultDialogListener {
    private SharedPreferences prefs;
    private int winPoints;
    private int playerPoints;
    private int compPoints;
    private TextView playerText;
    private TextView compText;
    private TextView statusText;
    private ImageView playerChoice;
    private ImageView compChoice;
    private final Map<GameSimulator.Choice, Drawable> choicePicture = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        choicePicture.put(GameSimulator.Choice.STONE, ContextCompat.getDrawable(this, R.drawable.stone));
        choicePicture.put(GameSimulator.Choice.PAPER, ContextCompat.getDrawable(this, R.drawable.paper));
        choicePicture.put(GameSimulator.Choice.SCISSORS, ContextCompat.getDrawable(this, R.drawable.scissors));

        prefs = getSharedPreferences("game_settings", MODE_PRIVATE);
        winPoints = prefs.getInt("win_points", 1);
        prefs = getSharedPreferences("game_info", MODE_PRIVATE);
        playerPoints = prefs.getInt("player_points", 0);
        compPoints = prefs.getInt("comp_points", 0);

        playerText = findViewById(R.id.player_points);
        compText = findViewById(R.id.computer_points);
        statusText = findViewById(R.id.round_status);
        playerText.setText(String.valueOf(playerPoints));
        compText.setText(String.valueOf(compPoints));
        playerChoice = findViewById(R.id.player_choice);
        compChoice = findViewById(R.id.comp_choice);


        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this::clickBackButton);
        ImageButton stoneButton = findViewById(R.id.stone);
        stoneButton.setOnClickListener(v -> {
            simulateRound(GameSimulator.Choice.STONE);
        });
        ImageButton paperButton = findViewById(R.id.paper);
        paperButton.setOnClickListener(v -> {simulateRound(GameSimulator.Choice.PAPER);});
        ImageButton scissorsButton = findViewById(R.id.scissors);
        scissorsButton.setOnClickListener(v -> {simulateRound(GameSimulator.Choice.SCISSORS);});

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    /*
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt("player_points", playerPoints);
        ed.putInt("comp_points", compPoints);
        ed.apply();
    }
     */

    public void clickBackButton(View view){
        finish();
    }
    public void simulateRound(GameSimulator.Choice playerChoice){
        setPlayerChoice(playerChoice);
        GameSimulator.Choice compChoice = GameSimulator.getCompChoice();
        setCompChoice(compChoice);
        setRoundStatus(GameSimulator.simulateRound(playerChoice, compChoice));
    }

    public void setPlayerChoice(GameSimulator.Choice choice){
        playerChoice.setImageDrawable(choicePicture.get(choice));
    }

    public void setCompChoice(GameSimulator.Choice choice){
        compChoice.setImageDrawable(choicePicture.get(choice));
    }
    public void setRoundStatus(GameSimulator.Result roundStatus){
        if (roundStatus == GameSimulator.Result.PLAYER_WIN){
            playerPoints++;
            statusText.setText("Победа игрока в раунде");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.win));
            playerText.setText(String.valueOf(playerPoints));
        }
        else if(roundStatus == GameSimulator.Result.DRAW){
            statusText.setText("Ничья");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

        else if(roundStatus == GameSimulator.Result.COMP_WIN){
            compPoints++;
            statusText.setText("Победа компьютера в раунде");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.lost));
            compText.setText(String.valueOf(compPoints));
        }
        checkWin();
    }
    public void checkWin(){
        if (playerPoints != winPoints && compPoints != winPoints)
            return;
        String gameResult = playerPoints == winPoints ? "Победа!" : "Поражение!";
        ResultDialogFragment dialog = ResultDialogFragment.newInstance(gameResult);
        dialog.show(getSupportFragmentManager(), "result_dialog");
    }

    public void onDialogPositiveClick(DialogFragment dialog){
        playerPoints = 0;
        compPoints = 0;
        playerText.setText(String.valueOf(playerPoints));
        compText.setText(String.valueOf(compPoints));
        dialog.dismiss();
    }

    public void onDialogNegativeClick(DialogFragment dialog){
        SharedPreferences.Editor ed = prefs.edit();
        ed.clear();
        ed.apply();
        finish();
    }
}