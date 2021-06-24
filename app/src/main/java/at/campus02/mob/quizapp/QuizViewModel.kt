package at.campus02.mob.quizapp

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    // <editor-fold desc="Properties für View">
    // ----------------------------------------------------------------------------------
    // Properties für die Anzeige des Views
    // ----------------------------------------------------------------------------------

    // Property für die aktuelle Frage des Quiz -> für die Anzeige im GameFragment
    // solange das "Spiel" nicht gestartet ist, gibt es keine aktuelle Frage -> initialisieren mit null
    var question: MutableLiveData<Question?> = MutableLiveData(null)

    val finished: MutableLiveData<Boolean> = MutableLiveData(false)
    val score: String get() = "Score: ${game?.correctCount} / ${game?.count} correct"

    var error: MutableLiveData<String?> = MutableLiveData(null)

    var buttonMarkers: MutableLiveData<Map<Choice, Int>> = MutableLiveData(mapOf(
            Choice.A to R.drawable.button_background,
            Choice.B to R.drawable.button_background,
            Choice.C to R.drawable.button_background,
            Choice.D to R.drawable.button_background
    ))

    var progressMarkers: MutableLiveData<List<Int>> = MutableLiveData(listOf(
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered,
        R.drawable.progress_unanswered
    ))

    var guessingCountdown: MutableLiveData<Int> = MutableLiveData(0)

    // Header 2: Definieren einer neuen beobachtbaren Variable userLabel (für die Anzeige im Header des View)
    var userLabel: MutableLiveData<String?> = MutableLiveData("Name")

    private var game: Game? = null
    //Header 1: Definieren einer privaten Variable user: User? analog zu game
    private var user: User? = null

    // </editor-fold>

    // <editor-fold desc="Aktionen für View">
    // ----------------------------------------------------------------------------------
    // Aktionen (vom User getriggert)
    // ----------------------------------------------------------------------------------

    // Zum Starten eines neuen Spiels -> setzt index auf 0 und die aktuelle Frage auf die erste in der Liste
    fun start() {
        error.value = null
        finished.value = false
        if (user == null)
            fetchUser()
        runInThread(
            execute = {
                QuizRepository.startGame()
            },
            then = { gameFromServer ->
                game = gameFromServer
                question.value = game?.current
                finished.value = game?.finished
                updateButtonMarkers()
                updateProgressMarkers()
                startGuessingCountdown()
            },
            catch = { exception ->
                error.value = exception.message
                println(exception.message)
            }
        )
    }



    // Zum Durchblättern der Fragen -> erhöht den Index und aktualisiert die aktive Frage
    fun next() {
        if (question.value?.isAnswered == false) return
        game?.next()
        question.value = game?.current
        updateButtonMarkers()
        updateProgressMarkers()
        startGuessingCountdown()
    }

    // Zur Wahl der User-Antwort
    fun choose(choice: Choice) {
        guessingCountdown.value = 0
        countDownTimer.cancel()
        runInThread(
            execute = {
                game?.answer(choice)
            },
            then = {
                finished.value = game?.finished
                updateProgressMarkers()
                updateButtonMarkers()
            },
            catch = { exception ->
                error.value = exception.message
                println(exception.message)
            }
        )
    }

    // </editor-fold>

    // <editor-fold desc="Interne Hilfsfunktionalität">
    // ----------------------------------------------------------------------------------
    // Interne Hilfsfunktionalität
    // ----------------------------------------------------------------------------------

    //Header 3: Definieren einer privaten Hilfsfunktion fetchUser(),
    // die runInThread verwendet, um QuizRepository.getUser() aufzurufen
    private fun fetchUser(){
        runInThread(
            execute = {
                QuizRepository.searchForThisUser()
            },
            //Falls erfolgreich: user den erhaltenen User zuweisen,
            // userLabel soll den Vornamen dieses Users erhalten
            then = {userFromServer ->
                user = userFromServer
                userLabel.value = user?.firstname
            },
            //Falls nicht erfolgreich: Setzen der error Property
            catch = { exception ->
                error.value = exception.message
                println(exception.message)
            }
        )
    }

    private fun startGuessingCountdown() {
        if (question.value?.isAnswered == true)
            return
        guessingCountdown.value = 100
        countDownTimer.start()
    }

    private val countDownTimer = object: CountDownTimer(10_000, 500) {
        override fun onTick(millisUntilFinished: Long) {
            guessingCountdown.value = (millisUntilFinished / 100).toInt()
        }

        override fun onFinish() {
            guessingCountdown.value = 0
            if (question.value?.isAnswered == false)
                choose(Choice.NONE)
        }
    }

    // Hilfsmethode, um suspend-Functions in einem eigenen Thread ausführt und
    // die Antwort im Main-Thread behandelt (auch die Fehlerbehandlung im Main-Thread
    // durchführt).
    //
    // der Typ <T> steht für den Rückgabe-Typ des REST-calls
    private fun <T> runInThread(
        // die suspend-Funktion, wird in eigenem Thread exekutiert
        execute: suspend () -> T,
        // die Funktion zur Verarbeitung des Ergebnisses,
        // wird wieder im Main-Thread ausgeführt
        then: (T) -> Unit,
        // die Funktion für den Fehlerfall
        catch: (Exception) -> Unit
    ) {
        // Starten in einem eigenen Thread
        GlobalScope.launch {
            try {
                // Ausführen der suspend-Funktion
                val result: T = execute()
                // wieder zurück in den Main-Thread
                MainScope().launch {
                    // Verarbeiten des Ergebnisses
                    then(result)
                }
            } catch (e: Exception) {
                // wieder zurück in den Main-Thread
                MainScope().launch {
                    // Fehlerbehandlung
                    catch(e)
                }
            }
        }
    }

    private fun updateProgressMarkers() {
        progressMarkers.value = listOf(
            progressResourceFor(0),
            progressResourceFor(1),
            progressResourceFor(2),
            progressResourceFor(3),
            progressResourceFor(4),
            progressResourceFor(5),
            progressResourceFor(6),
            progressResourceFor(7),
            progressResourceFor(8),
            progressResourceFor(9)
        )
    }

    private fun progressResourceFor(index: Int): Int {
        val displayQuestion = game?.questions?.get(index)
        return when {
            displayQuestion == null -> R.drawable.progress_unanswered
            displayQuestion == question.value && !displayQuestion.isAnswered -> R.drawable.progress_current
            displayQuestion == question.value && displayQuestion.isCorrect -> R.drawable.progress_current_correct
            displayQuestion == question.value && !displayQuestion.isCorrect -> R.drawable.progress_current_incorrect
            displayQuestion.isCorrect -> R.drawable.progress_correct
            displayQuestion.isAnswered && !displayQuestion.isCorrect -> R.drawable.progress_incorrect
            else -> R.drawable.progress_unanswered
        }
    }

    private fun updateButtonMarkers() {
        buttonMarkers.value = mapOf(
            Choice.A to resourceFor(game?.current, Choice.A),
            Choice.B to resourceFor(game?.current, Choice.B),
            Choice.C to resourceFor(game?.current, Choice.C),
            Choice.D to resourceFor(game?.current, Choice.D)
        )
    }

    private fun resourceFor(question: Question?, choice: Choice): Int {
        // entscheiden, welcher Hintergrund verwendet wird....

        // die Kotlin-when Variante:
//        return when {
//            !question.isAnswered -> R.drawable.button_background
//            question.isCorrect && choice == question.choice -> R.drawable.button_background_correct
//            !question.isCorrect && choice == question.choice -> R.drawable.button_background_incorrect
//            !question.isCorrect && choice == question.correctChoice -> R.drawable.button_background_hint
//            else -> R.drawable.button_background
//        }


        // die if - Variante

        // wenn die Frage noch nicht beantwortet wurde -> neutraler Hintergrund
        if (question == null || !question.isAnswered) {
            return R.drawable.button_background
        }
        // wenn die Frage korrekt ist und die Button-Choice der korrekt gegebenen Antwort entspricht -> grün
        if (question.isCorrect && question.choice == choice) {
            return R.drawable.button_background_correct
        }

        // wenn die Frage falsch ist und die Button-Choice der falsch gegebenen Antwort entspricht -> rot
        if (!question.isCorrect && question.choice == choice) {
            return R.drawable.button_background_incorrect
        }

        // wenn die Frage falsch beantwortet wurde, die Button-Choice aber der richtigen Antwort entspricht -> hint
        if (!question.isCorrect && question.correctChoice == choice) {
            return R.drawable.button_background_hint
        }

        return R.drawable.button_background

    }
    // </editor-fold>

}
