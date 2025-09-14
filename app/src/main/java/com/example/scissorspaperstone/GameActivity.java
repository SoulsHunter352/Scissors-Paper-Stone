package com.example.scissorspaperstone;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private boolean isGameCompleted;
    private GameSimulator.Result result;
    private TextView playerText;
    private TextView compText;
    private TextView statusText;
    private ImageView playerChoice;
    private ImageView compChoice;
    private Animation playerAnim;
    private Animation compAnim;
    private Animation statusAnim;
    private Animation playerPointsAnim;
    private Animation compPointsAnim;
    private final Map<GameSimulator.Choice, Drawable> choicePicture = new HashMap<>();
    private ImageButton[] choiceButtons;

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

        playerPoints = 0;
        compPoints = 0;
        isGameCompleted = false;  // По умолчанию отмечаем игру как незавершенную

        Bundle args = getIntent().getExtras();
        if(args != null){
            if(args.getBoolean("is_continue")){
                playerPoints = prefs.getInt("player_points", 0);
                compPoints = prefs.getInt("comp_points", 0);
                isGameCompleted = prefs.getBoolean("is_game_completed", false);
            }
        }

        playerText = findViewById(R.id.player_points);
        compText = findViewById(R.id.computer_points);
        statusText = findViewById(R.id.round_status);
        playerChoice = findViewById(R.id.player_choice);
        compChoice = findViewById(R.id.comp_choice);
        prepareGame();

        playerAnim = AnimationUtils.loadAnimation(this, R.anim.player_anim);
        playerAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                statusText.setVisibility(VISIBLE);
                statusText.startAnimation(statusAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                setChoicesEnabled(false);
            }
        });
        compAnim = AnimationUtils.loadAnimation(this, R.anim.comp_animation);
        statusAnim = AnimationUtils.loadAnimation(this, R.anim.status_anim);
        statusAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                setChoicesEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });
        playerPointsAnim = AnimationUtils.loadAnimation(this, R.anim.points_anim);
        compPointsAnim = AnimationUtils.loadAnimation(this, R.anim.points_anim);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this::clickBackButton);
        ImageButton stoneButton = findViewById(R.id.stone);
        stoneButton.setOnClickListener(v -> simulateRound(GameSimulator.Choice.STONE));
        ImageButton paperButton = findViewById(R.id.paper);
        paperButton.setOnClickListener(v -> simulateRound(GameSimulator.Choice.PAPER));
        ImageButton scissorsButton = findViewById(R.id.scissors);
        scissorsButton.setOnClickListener(v -> simulateRound(GameSimulator.Choice.SCISSORS));

        choiceButtons = new ImageButton[]{stoneButton, paperButton, scissorsButton};

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isGameCompleted){
            SharedPreferences.Editor ed = prefs.edit();
            ed.putInt("player_points", playerPoints);
            ed.putInt("comp_points", compPoints);
            ed.putBoolean("is_game_completed", isGameCompleted);
            ed.apply();
        }
    }

    public void prepareGame(){
        playerText.setText(String.valueOf(playerPoints));
        compText.setText(String.valueOf(compPoints));
        playerChoice.setImageDrawable(null);
        compChoice.setImageDrawable(null);
        statusText.setVisibility(INVISIBLE);
        //statusText.setText("");
    }

    public void clickBackButton(View view){
        finish();
    }
    public void setPlayerChoice(GameSimulator.Choice choice){
        playerChoice.setImageDrawable(choicePicture.get(choice));
    }

    public void setCompChoice(GameSimulator.Choice choice){
        compChoice.setImageDrawable(choicePicture.get(choice));
    }

    public void startAnimation(){
        playerChoice.startAnimation(playerAnim);
        compChoice.startAnimation(compAnim);

        //statusText.startAnimation(statusAnim);
    }

    public void setChoicesEnabled(boolean enabled){
        int alpha = enabled ? 255 : 128;
        for(var button: choiceButtons){
            button.setEnabled(enabled);
            button.setImageAlpha(alpha);
        }
    }

    public void simulateRound(GameSimulator.Choice playerChoice){
        //setChoicesEnabled(false);
        statusText.setVisibility(INVISIBLE);
        setPlayerChoice(playerChoice);
        GameSimulator.Choice compChoice = GameSimulator.getCompChoice();
        setCompChoice(compChoice);
        setRoundStatus(GameSimulator.simulateRound(playerChoice, compChoice));
        //setChoicesEnabled(true);
    }

    public void setRoundStatus(GameSimulator.Result roundStatus){
        if (roundStatus == GameSimulator.Result.PLAYER_WIN){
            playerPoints++;
            statusText.setText("Победа игрока в раунде");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.win));
            String text = String.valueOf(playerPoints);
            //playerText.setText(String.valueOf(playerPoints));
            playerText.postDelayed(() -> {
                playerText.setText(text);
                playerText.startAnimation(playerPointsAnim);
            }, 350);
            //playerText.startAnimation(playerPointsAnim);
        }
        else if(roundStatus == GameSimulator.Result.DRAW){
            statusText.setText("Ничья");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
        else if(roundStatus == GameSimulator.Result.COMP_WIN){
            compPoints++;
            statusText.setText("Победа компьютера в раунде");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.lost));
            String text = String.valueOf(compPoints);
            compText.postDelayed(() -> {
                compText.setText(text);
                compText.startAnimation(compPointsAnim);
            }, 350);
            //compText.setText(String.valueOf(compPoints));
            //compText.startAnimation(compPointsAnim);
        }
        startAnimation();
        checkWin();
    }
    public void checkWin(){
        if (playerPoints != winPoints && compPoints != winPoints)
            return;
        String gameResult = playerPoints == winPoints ? "Победа!" : "Поражение!";
        playerPoints = 0;
        compPoints = 0;
        isGameCompleted = true;
        SharedPreferences.Editor ed = prefs.edit();
        ed.clear();
        ed.apply();
        ResultDialogFragment dialog = ResultDialogFragment.newInstance(gameResult);
        dialog.show(getSupportFragmentManager(), "result_dialog");
    }

    public void onDialogPositiveClick(DialogFragment dialog){
        prepareGame();
        isGameCompleted = false;  // Если начинаем заново игру, то считаем её как начавшуюся
        dialog.dismiss();
    }

    public void onDialogNegativeClick(DialogFragment dialog){
        finish();
    }
}