package com.example.scissorspaperstone;

import java.util.Random;

public class GameSimulator {
    public static final byte STONE = 0;
    public static final byte PAPER = 1;
    public static final byte SCISSORS = 2;
    private static final byte[] choices = {STONE, PAPER, SCISSORS};
    public static final byte PLAYER_WIN = 0;
    public static final byte COMP_WIN = 1;
    public static final byte DRAW = 2;
    private final Random random;

    {
        random = new Random();
    }

    public byte simulateRound(byte playerChoice){
        byte compChoice = choices[random.nextInt(choices.length)];
        if(compChoice == playerChoice)
            return DRAW;
        else if((playerChoice == STONE && compChoice == SCISSORS) ||
                (playerChoice == PAPER && compChoice == STONE) ||
                (playerChoice == SCISSORS && compChoice == PAPER)){
            return PLAYER_WIN;
        }
        else
            return COMP_WIN;
    }
}
