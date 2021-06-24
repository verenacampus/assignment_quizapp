package at.campus02.mob.quizapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class GameFragment : Fragment() {

    // Zugriff aufs ViewModel
    private val viewModel: QuizViewModel by activityViewModels()

    // ----------------------------------------------------------------------------------
    // Zugriff auf die Views aus dem Layout (Buttons und Labels)
    // ----------------------------------------------------------------------------------
    private val questionText: TextView?
        get() = activity?.findViewById(R.id.questionText)

    private val button1Label: TextView?
        get() = activity?.findViewById(R.id.button1Label)
    private val button2Label: TextView?
        get() = activity?.findViewById(R.id.button2Label)
    private val button3Label: TextView?
        get() = activity?.findViewById(R.id.button3Label)
    private val button4Label: TextView?
        get() = activity?.findViewById(R.id.button4Label)

    private val button1: View?
        get() = activity?.findViewById(R.id.button1Layout)
    private val button2: View?
        get() = activity?.findViewById(R.id.button2Layout)
    private val button3: View?
        get() = activity?.findViewById(R.id.button3Layout)
    private val button4: View?
        get() = activity?.findViewById(R.id.button4Layout)

    private val continueButton: View?
        get() = activity?.findViewById(R.id.continueButtonLayout)
    private val continueButtonLabel: TextView?
        get() = activity?.findViewById(R.id.continueButtonLabel)

    private val progressBar: ProgressBar?
        get() = activity?.findViewById(R.id.progressBar)

    private val progressIndicators: List<View?>
        get() = listOf(
            activity?.findViewById(R.id.p1),
            activity?.findViewById(R.id.p2),
            activity?.findViewById(R.id.p3),
            activity?.findViewById(R.id.p4),
            activity?.findViewById(R.id.p5),
            activity?.findViewById(R.id.p6),
            activity?.findViewById(R.id.p7),
            activity?.findViewById(R.id.p8),
            activity?.findViewById(R.id.p9),
            activity?.findViewById(R.id.p10),
        )

    private val userLabel: TextView?
        get() = activity?.findViewById(R.id.userLabel)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onStart() {
        super.onStart()


        // ----------------------------------------------------------------------------------
        // UI anpassen aufgrund von ViewModel-Änderungen (LiveData)
        // ----------------------------------------------------------------------------------

        viewModel.userLabel.observe(this, {label ->
            userLabel?.text = label
        })

        viewModel.question.observe(this, { question ->
            questionText?.text = question?.text
            button1Label?.text = question?.answerA
            button2Label?.text = question?.answerB
            button3Label?.text = question?.answerC
            button4Label?.text = question?.answerD
        })

        viewModel.finished.observe(this, { finished ->
            if (finished == true) {
                continueButtonLabel?.text = viewModel.score
            } else {
                continueButtonLabel?.text = getString(R.string.continue_button_label)
            }
        })

        viewModel.guessingCountdown.observe(this, { progress ->
            progressBar?.progress = progress
            progressBar?.visibility = if(progress > 0) View.VISIBLE else View.GONE
        })

        viewModel.buttonMarkers.observe(this, { buttonMarkers ->
            button1?.setBackgroundResource(buttonMarkers[Choice.A] ?: R.drawable.button_background)
            button2?.setBackgroundResource(buttonMarkers[Choice.B] ?: R.drawable.button_background)
            button3?.setBackgroundResource(buttonMarkers[Choice.C] ?: R.drawable.button_background)
            button4?.setBackgroundResource(buttonMarkers[Choice.D] ?: R.drawable.button_background)
        })

        viewModel.progressMarkers.observe(this, {
            it.forEachIndexed { index, backgroundResId ->
                progressIndicators.get(index)?.setBackgroundResource(backgroundResId)
            }
        })

        progressIndicators.forEachIndexed{ index, backgroundResId ->
            progressIndicators.get(index)?.setOnClickListener{
                viewModel.selectQuestion(index)
            }

        }

        viewModel.error.observe(this, { errorMessage ->

            if (errorMessage == null)
                return@observe

            Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.error.value = null
        })

        // ----------------------------------------------------------------------------------
        // Aktionen (vom User getriggert)
        // ----------------------------------------------------------------------------------

        continueButton?.setOnClickListener {
            viewModel.next()
        }

        button1?.setOnClickListener {
            viewModel.choose(Choice.A)
        }
        button2?.setOnClickListener {
            viewModel.choose(Choice.B)
        }
        button3?.setOnClickListener {
            viewModel.choose(Choice.C)
        }
        button4?.setOnClickListener {
            viewModel.choose(Choice.D)
        }
    }

}
