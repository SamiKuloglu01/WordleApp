package com.nexustech.wordle

object FourLetterWordList {
    private val fourLetterWords =
        "Area,Army,Baby,Back"

    // Returns a list of four letter words
    fun getAllFourLetterWords(): List<String> {
        return fourLetterWords.split(",")
    }

    // Returns a random four letter word from the list in all caps
    fun getRandomFourLetterWord(): String {
        val allWords = getAllFourLetterWords()
        val randomNumber = (0 until allWords.size).random()
        return allWords[randomNumber].uppercase()
    }
}



