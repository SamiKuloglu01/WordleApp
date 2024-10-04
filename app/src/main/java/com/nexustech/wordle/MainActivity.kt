package com.nexustech.wordle

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var guessHistoryText: TextView
    private lateinit var resultTxtView: TextView
    private lateinit var inputEditText: EditText
    private lateinit var guessButton: Button
    private lateinit var resetButton: Button
    private lateinit var wordEnteredText: TextView
    private var targetWord = ""
    private var guessCount = 0
    private val maxGuesses = 3
    private lateinit var sharedPreferences: SharedPreferences
    private val STREAK_PREFS_KEY = "streak_prefs"
    private val WIN_STREAK_KEY = "win_streak"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intializesharedprefs()
        initializeViews()
        setUpListeners()
        startNewGame()
    }

    private fun intializesharedprefs() {
        sharedPreferences = getSharedPreferences(STREAK_PREFS_KEY, MODE_PRIVATE)
    }

    private fun initializeViews() {
        guessHistoryText = findViewById(R.id.guessHistoryText)
        resultTxtView = findViewById(R.id.ResultTxtView)
        inputEditText = findViewById(R.id.editTextGuess)
        guessButton = findViewById(R.id.guessBtn)
        resetButton = findViewById(R.id.resetBtn)
        wordEnteredText = findViewById(R.id.WordEnterTxtView)
        resetButton.visibility = View.GONE
    }

    private fun setUpListeners() {
        guessButton.setOnClickListener {
            handleGuess()
        }
        resetButton.setOnClickListener {
            startNewGame()
        }
        val streakButton: Button = findViewById(R.id.StreakBtn)
        streakButton.setOnClickListener {
            showStreakDialog()
        }
    }

    private fun showStreakDialog() {
        val winStreak = sharedPreferences.getInt(WIN_STREAK_KEY, 0)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Win Streak")
        dialogBuilder.setMessage("Your current win streak is: $winStreak")
        dialogBuilder.setPositiveButton("OK", null)
        dialogBuilder.show()
    }

    private fun startNewGame() {
        targetWord = FourLetterWordList.getRandomFourLetterWord()
        guessCount = 0

        resetUIForNewGame()
    }

    private fun resetUIForNewGame() {
        guessHistoryText.text = ""
        resetButton.visibility = View.GONE
        guessButton.isEnabled = true
        guessButton.visibility = View.VISIBLE
        inputEditText.text.clear()
        resultTxtView.text = ""
        wordEnteredText.text = ""
    }

    private fun handleGuess() {
        val userGuess = getUserInput()
        if (!isValidGuess(userGuess)) {
            showInvalidGuessToast()
            return
        }
        processGuess(userGuess)
    }

    private fun getUserInput(): String {
        return inputEditText.text.toString().uppercase()
    }

    private fun isValidGuess(guess: String): Boolean {
        return guess.length == 4
    }

    private fun showInvalidGuessToast() {
        Toast.makeText(this, "Please enter a 4-letter word.", Toast.LENGTH_SHORT).show()
    }

    private fun processGuess(userGuess: String) {
        wordEnteredText.text = userGuess
        guessCount++
        val result = checkGuess(userGuess)
        updateGuessHistory(userGuess, result)
        if (isGameWon(result)) {
            handleGameWin()
        } else if (isGameOver()) {
            handleGameOver()
        }
        inputEditText.text.clear()
    }

    private fun checkGuess(guess: String): String {
        var result = ""
        for (i in 0..3) {
            if (guess[i] == targetWord[i]) {
                result += "O"  // Correct letter and correct position
            } else if (guess[i] in targetWord) {
                result += "+"  // Correct letter, wrong position
            } else {
                result += "X"  // Letter not in the target word
            }
        }
        return result
    }

    private fun updateGuessHistory(guess: String, result: String) {
        val spannableString = SpannableStringBuilder()
        spannableString.append("Guess #$guessCount: $guess\n")
        spannableString.append("Guess #$guessCount Check: ")

        for (i in guess.indices) {
            val start = spannableString.length
            spannableString.append(guess[i].toString())

            when (result[i]) {
                'O' -> spannableString.setSpan(
                    ForegroundColorSpan(Color.GREEN),
                    start, start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                '+' -> spannableString.setSpan(
                    ForegroundColorSpan(Color.RED),
                    start, start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        spannableString.append("\n\n")
        guessHistoryText.append(spannableString)
    }


    private fun isGameWon(result: String): Boolean {
        return result == "OOOO"
    }

    private fun handleGameWin() {
        resultTxtView.text = "Congratulations! You've guessed the word!"
        resultTxtView.visibility = View.VISIBLE
        guessButton.visibility = View.GONE

        val currentStreak = sharedPreferences.getInt(WIN_STREAK_KEY, 0) + 1
        sharedPreferences.edit().putInt(WIN_STREAK_KEY, currentStreak).apply()

        showResetButton()
    }


    private fun isGameOver(): Boolean {
        return guessCount >= maxGuesses
    }

    @SuppressLint("SetTextI18n")
    private fun handleGameOver() {
        resultTxtView.text = "Game Over! The word was: $targetWord"
        resultTxtView.visibility = View.VISIBLE
        guessButton.visibility = View.GONE

        // Reset the streak in SharedPreferences
        sharedPreferences.edit().putInt(WIN_STREAK_KEY, 0).apply()

        showResetButton()
    }


    private fun showResetButton() {
        resetButton.visibility = View.VISIBLE
        guessButton.isEnabled = false
    }
}
