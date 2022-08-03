package com.example.quizquiz

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.quizquiz.database.Quiz
import com.example.quizquiz.database.QuizDatabase
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class QuizCreateFragment : Fragment() {


    lateinit var db : QuizDatabase
    lateinit var quiz : Quiz

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.quiz_create_fragment, container, false)
        val answerView = view.findViewById<TextView>(R.id.answer)
        val nAnswerView = view.findViewById<LinearLayout>(R.id.n_answer)
        val makeBtn = view.findViewById<Button>(R.id.plus_quiz)
        var type = ""
        db = QuizDatabase.getInstance(requireContext())

        nAnswerView.visibility = View.GONE

        view.findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId) {
                R.id.ox -> {
                    type = "ox"
                    nAnswerView.visibility = View.GONE
                    answerView.visibility = View.VISIBLE
                }
                R.id.n_select -> {
                    type = "multiple_choice"
                    answerView.visibility = View.GONE
                    nAnswerView.visibility = View.VISIBLE
                }
            }
        }

        makeBtn.setOnClickListener {
            val question = view.findViewById<TextView>(R.id.question).text.toString()
            val answer = answerView.text.toString()
            val category = view.findViewById<TextView>(R.id.category).text.toString()

            if(question.isNullOrEmpty() || answer.isNullOrEmpty() || category.isNullOrEmpty() || type.isNullOrEmpty())
                Toast.makeText(requireContext(), "비어있는 칸이 있습니다.", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(requireContext(), "추가했습니다.", Toast.LENGTH_SHORT).show()
                if(type == "ox") {
                     quiz = Quiz(
                        type = type,
                        question = question,
                        answer = answer,
                        category = category
                    )
                }
                else if(type == "multiple_choice") {
                    val answerList = mutableListOf<String>()
                    answerList.add(view.findViewById<TextView>(R.id.answer1).text.toString())
                    answerList.add(view.findViewById<TextView>(R.id.answer2).text.toString())
                    answerList.add(view.findViewById<TextView>(R.id.answer3).text.toString())
                    answerList.add(view.findViewById<TextView>(R.id.answer4).text.toString())
                    quiz = Quiz(
                        type = type,
                        question = question,
                        answer = answer,
                        category = category,
                        guesses = answerList
                    )
                }
            }
            Thread {
                db.quizDAO().insert(quiz)
            }
        }

        return view
    }
}