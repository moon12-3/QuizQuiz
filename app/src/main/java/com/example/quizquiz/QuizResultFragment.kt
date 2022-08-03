package com.example.quizquiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.lang.Exception

class QuizResultFragment : Fragment() {
    interface QuizResultListener {
        fun onRetry()
    }

    lateinit var listener : QuizResultListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        
        if(parentFragment is QuizResultListener) {
            listener = parentFragment as QuizResultListener
        }
        else {
            throw Exception("QuizResultListener 미구현")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.quiz_result_fragment, container, false)
        val correctCount = arguments?.getInt("correct")
        val fullCount = arguments?.getInt("full")

        val correctRatio = view.findViewById<TextView>(R.id.correct_ratio)
        correctRatio.text = "$correctCount / $fullCount"

        view.findViewById<Button>(R.id.retry).setOnClickListener {
            listener.onRetry()
        }

        return view
    }

    companion object {
        fun newInstance(correctCount: Int, fullCount: Int): QuizResultFragment {
            val args = Bundle()
            args.putInt("correct", correctCount)
            args.putInt("full", fullCount)
            val fragment = QuizResultFragment()
            fragment.arguments = args
            return fragment
        }
    }
}