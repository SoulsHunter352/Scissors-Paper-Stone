package com.example.scissorspaperstone;

import java.util.List;
import java.util.Random;

public class GameSimulator {
    public enum Choice{
        STONE,
        PAPER,
        SCISSORS
    }

    public enum Result{
        PLAYER_WIN,
        DRAW,
        COMP_WIN
    }
    private static final List<Choice> CHOICES = List.of(Choice.values());
    private static final Random RANDOM = new Random();

    public static Choice getCompChoice(){
        return CHOICES.get(RANDOM.nextInt(CHOICES.size()));
    }

    public static Result simulateRound(Choice playerChoice, Choice compChoice){
        if(compChoice == playerChoice)
            return Result.DRAW;
        else if((playerChoice == Choice.STONE && compChoice == Choice.SCISSORS) ||
                (playerChoice == Choice.PAPER && compChoice == Choice.STONE) ||
                (playerChoice == Choice.SCISSORS && compChoice == Choice.PAPER)){
            return Result.PLAYER_WIN;
        }
        else
            return Result.COMP_WIN;
    }
}
