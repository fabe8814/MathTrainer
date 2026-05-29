package com.example.mathtrainer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // UI элементы
    private TextView textViewQuestion;
    private TextView textViewHint;
    private TextView textViewResult;
    private TextView textViewLevel;
    private TextView textViewMistakes;
    private TextView textViewScore;
    private EditText editTextAnswer;
    private Button buttonCheck;
    private Button buttonYes;
    private Button buttonNo;
    private LinearLayout layoutYesNo;
    private ProgressBar progressBarTime;

    // Игровые переменные
    private int currentLevel = 1;
    private int mistakes = 0;
    private int correctAnswers = 0;
    private int currentAnswer = 0;
    private String currentQuestionText = "";
    private boolean isTrueFalseQuestion = false;
    private boolean expectedTrueFalseAnswer = false;

    // Таймер
    private CountDownTimer countDownTimer;
    private long timeLeftForQuestion = 30000;
    private boolean isTimerRunning = false;

    // Анимации
    private Animation shakeAnimation;
    private Animation bounceAnimation;

    // Генератор случайных чисел
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация UI
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewHint = findViewById(R.id.textViewHint);
        textViewResult = findViewById(R.id.textViewResult);
        textViewLevel = findViewById(R.id.textViewLevel);
        textViewMistakes = findViewById(R.id.textViewMistakes);
        textViewScore = findViewById(R.id.textViewScore);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonCheck = findViewById(R.id.buttonCheck);
        buttonYes = findViewById(R.id.buttonYes);
        buttonNo = findViewById(R.id.buttonNo);
        layoutYesNo = findViewById(R.id.layoutYesNo);
        progressBarTime = findViewById(R.id.progressBarTime);

        // Загрузка анимаций
        shakeAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        bounceAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        // Обработчики кнопок
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYesNoAnswer(true);
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYesNoAnswer(false);
            }
        });

        // Начинаем игру
        startNewGame();
    }

    private void startNewGame() {
        currentLevel = 1;
        mistakes = 0;
        correctAnswers = 0;
        updateUI();
        generateNewQuestion();
    }

    private void updateUI() {
        textViewLevel.setText(String.valueOf(currentLevel));
        textViewMistakes.setText(mistakes + " / 3");
        textViewScore.setText("Правильно: " + correctAnswers);
    }

    private void generateNewQuestion() {
        stopTimer();

        int questionType = determineQuestionType();

        switch (questionType) {
            case 0:
                generateSimpleArithmetic();
                break;
            case 1:
                generateMultiplication();
                break;
            case 2:
                generateTwoOperations();
                break;
            case 3:
                generateTrueFalse();
                break;
            default:
                generateSimpleArithmetic();
                break;
        }

        setupTimer();

        if (editTextAnswer.getVisibility() == View.VISIBLE) {
            editTextAnswer.setText("");
        }
        textViewResult.setText("");
    }

    private int determineQuestionType() {
        if (currentLevel <= 2) {
            return random.nextInt(2);
        } else if (currentLevel <= 4) {
            return random.nextInt(3);
        } else if (currentLevel <= 6) {
            return random.nextInt(4);
        } else {
            return random.nextInt(4);
        }
    }

    private void generateSimpleArithmetic() {
        int maxNumber = Math.min(10 + currentLevel * 2, 100);
        int a = random.nextInt(maxNumber) + 1;
        int b = random.nextInt(maxNumber) + 1;
        boolean isAddition = random.nextBoolean();

        if (isAddition) {
            currentAnswer = a + b;
            currentQuestionText = a + " + " + b + " = ?";
        } else {
            // Разрешаем отрицательные ответы
            currentAnswer = a - b;
            currentQuestionText = a + " - " + b + " = ?";
        }

        isTrueFalseQuestion = false;
        textViewQuestion.setText(currentQuestionText);
        textViewHint.setText("Введите ответ:");

        editTextAnswer.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.VISIBLE);
        layoutYesNo.setVisibility(View.GONE);
        editTextAnswer.requestFocus();
    }

    private void generateMultiplication() {
        int maxMultiplier = Math.min(5 + currentLevel, 12);
        int a = random.nextInt(maxMultiplier) + 1;
        int b = random.nextInt(maxMultiplier) + 1;
        currentAnswer = a * b;
        currentQuestionText = a + " × " + b + " = ?";

        isTrueFalseQuestion = false;
        textViewQuestion.setText(currentQuestionText);
        textViewHint.setText("Введите ответ:");

        editTextAnswer.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.VISIBLE);
        layoutYesNo.setVisibility(View.GONE);
        editTextAnswer.requestFocus();
    }

    private void generateTwoOperations() {
        int maxNumber = Math.min(10 + currentLevel, 50);
        int a = random.nextInt(maxNumber) + 1;
        int b = random.nextInt(maxNumber) + 1;
        int c = random.nextInt(maxNumber) + 1;

        int operation1 = random.nextInt(2);
        int operation2 = random.nextInt(2);

        int result1;

        if (operation1 == 0) {
            result1 = a + b;
        } else {
            result1 = a - b;
        }

        if (operation2 == 0) {
            currentAnswer = result1 + c;
        } else {
            currentAnswer = result1 - c;
        }

        String op1 = operation1 == 0 ? " + " : " - ";
        String op2 = operation2 == 0 ? " + " : " - ";
        currentQuestionText = a + op1 + b + op2 + c + " = ?";

        isTrueFalseQuestion = false;
        textViewQuestion.setText(currentQuestionText);
        textViewHint.setText("Введите ответ:");

        editTextAnswer.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.VISIBLE);
        layoutYesNo.setVisibility(View.GONE);
        editTextAnswer.requestFocus();
    }

    private void generateTrueFalse() {
        int maxNumber = Math.min(10 + currentLevel, 50);
        int a = random.nextInt(maxNumber) + 1;
        int b = random.nextInt(maxNumber) + 1;
        int c = random.nextInt(maxNumber * 2) + 1;

        String[] operators = {"<", ">", "="};
        String operator = operators[random.nextInt(3)];

        int leftResult;
        int operationType = random.nextInt(2);

        if (operationType == 0) {
            leftResult = a + b;
            currentQuestionText = a + " + " + b;
        } else {
            leftResult = a - b;
            currentQuestionText = a + " - " + b;
        }

        currentQuestionText = currentQuestionText + " " + operator + " " + c + " ?";

        switch (operator) {
            case "<":
                expectedTrueFalseAnswer = leftResult < c;
                break;
            case ">":
                expectedTrueFalseAnswer = leftResult > c;
                break;
            case "=":
                expectedTrueFalseAnswer = leftResult == c;
                break;
        }

        isTrueFalseQuestion = true;
        textViewQuestion.setText(currentQuestionText);
        textViewHint.setText("Верно ли утверждение?");

        editTextAnswer.setVisibility(View.GONE);
        buttonCheck.setVisibility(View.GONE);
        layoutYesNo.setVisibility(View.VISIBLE);
    }

    private void setupTimer() {
        long timeForLevel = Math.max(5000, 30000 - (currentLevel - 1) * 1500);
        timeLeftForQuestion = timeForLevel;
        progressBarTime.setMax(100);
        progressBarTime.setProgress(100);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftForQuestion, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftForQuestion = millisUntilFinished;
                int progress = (int) ((millisUntilFinished * 100) / timeForLevel);
                progressBarTime.setProgress(progress);

                if (progress < 30) {
                    progressBarTime.setProgressTintList(
                            android.content.res.ColorStateList.valueOf(0xFFFF0000)
                    );
                } else if (progress < 60) {
                    progressBarTime.setProgressTintList(
                            android.content.res.ColorStateList.valueOf(0xFFFFA500)
                    );
                } else {
                    progressBarTime.setProgressTintList(
                            android.content.res.ColorStateList.valueOf(0xFF4CAF50)
                    );
                }
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                handleWrongAnswer("Время вышло!");
            }
        };

        countDownTimer.start();
        isTimerRunning = true;
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    private void checkAnswer() {
        if (isTrueFalseQuestion) return;

        String answerText = editTextAnswer.getText().toString().trim();
        if (answerText.isEmpty()) {
            textViewResult.setText("Введите ответ!");
            textViewResult.setTextColor(0xFFFFA500);
            return;
        }

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(answerText);
        } catch (NumberFormatException e) {
            textViewResult.setText("Введите число!");
            return;
        }

        if (userAnswer == currentAnswer) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer("Правильный ответ: " + currentAnswer);
        }
    }

    private void checkYesNoAnswer(boolean userAnswer) {
        if (!isTrueFalseQuestion) return;

        if (userAnswer == expectedTrueFalseAnswer) {
            handleCorrectAnswer();
        } else {
            String correctText = expectedTrueFalseAnswer ? "ДА" : "НЕТ";
            handleWrongAnswer("Правильный ответ: " + correctText);
        }
    }

    private void handleCorrectAnswer() {
        stopTimer();
        correctAnswers++;
        textViewResult.setText("✅ Правильно!");
        textViewResult.setTextColor(0xFF4CAF50);

        textViewQuestion.startAnimation(bounceAnimation);

        if (correctAnswers % 5 == 0) {
            currentLevel++;
            Toast.makeText(this, "🎉 УРОВЕНЬ " + currentLevel + "! 🎉", Toast.LENGTH_LONG).show();
        }

        updateUI();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                generateNewQuestion();
            }
        }, 1000);
    }

    private void handleWrongAnswer(String message) {
        stopTimer();
        mistakes++;
        textViewResult.setText("❌ Неправильно!\n" + message);
        textViewResult.setTextColor(0xFFF44336);
        textViewQuestion.startAnimation(shakeAnimation);

        updateUI();

        if (mistakes >= 3) {
            endGame();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                generateNewQuestion();
            }
        }, 2000);
    }

    private void endGame() {
        stopTimer();

        new AlertDialog.Builder(this)
                .setTitle("💀 ИГРА ОКОНЧЕНА 💀")
                .setMessage("Вы допустили 3 ошибки!\n\n" +
                        "Ваш результат:\n" +
                        "• Уровень: " + currentLevel + "\n" +
                        "• Правильных ответов: " + correctAnswers)
                .setPositiveButton("Новая игра", (dialog, which) -> {
                    startNewGame();
                })
                .setNegativeButton("Выход", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}