package at.campus02.mob.quizapp

enum class Choice {
    A, B, C, D, NONE
}

data class Question(
    val id: Int?,
    val text: String,
    val answerA: String,
    val answerB: String,
    val answerC: String,
    val answerD: String,
    val correctChoice: Choice,
    var choice: Choice? = null
) {
    val isAnswered: Boolean get() = choice != null

    val isCorrect: Boolean get() = choice == correctChoice

    fun choose(userChoice: Choice) {
        choice = userChoice
    }
}

const val username = "Verena"

// ein einzelnes Quiz-Game (kümmert sich um den Durchlauf durch die Fragen, Sammeln der Antworten, ...)
data class Game(private val id: Int?, val questions: List<Question>, var finished: Boolean) {

    // Index, auf welcher Frage stehen wir gerade
    private var index = 0

    val current: Question? get() = questions.getOrNull(index)

    val count: Int get() = questions.size
    val correctCount: Int get() = questions.count { it.isCorrect }

    fun next() {
        // index startet bei 0, deshalb noch "+ 1"
        if ((index + 1) < questions.size)
          index++
    }

    fun gotoIndex(indexToGoTo: Int) {
        if (index !in 0..questions.size)
            return
        index = indexToGoTo
    }

    suspend fun answer(choice: Choice) {
        if (current?.isAnswered == true)
            return

        if (id == null || current == null)
            return

        val response = api.answer("$username", id, current!!.copy(choice = choice)).await()
        if (response.isSuccessful) {
            val gameFromServer = response.body() ?: throw IllegalStateException("Answering question did not return a valid game!")
            current?.choose(choice)
            finished = gameFromServer.finished
        } else {
            throw IllegalStateException("Could not answer question on server! Http Code " + response.code())
        }

    }
}

// für das Starten eines neuen Spiels
object QuizRepository {
    suspend fun startGame(): Game {

        val response = api.startGameFor("$username").await()

        if (response.isSuccessful) {
            return response.body() ?: throw IllegalStateException("Could not fetch game from server!")
        } else {
            throw IllegalStateException("Could not fetch game from server! Http Code " + response.code())
        }
    }
}

