package at.campus02.mob.quizapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class StartFragment : Fragment() {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Zugriff via findViewById über die Activity, in der dieses Fragment "wohnt"
        val startNewGameButton = activity?.findViewById<View>(R.id.newGameButtonLayout)

        startNewGameButton?.setOnClickListener {
            viewModel.start()
            val navController = findNavController()
            navController.navigate(R.id.action_startFragment_to_gameFragment)
        }

    }

}