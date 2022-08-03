package com.example.quizquiz

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.quizquiz.database.Quiz
import com.google.android.material.navigation.NavigationView
import com.example.quizquiz.database.QuizDatabase
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {

    fun initQuizDataFromXMLFile() {
        AsyncTask.execute { // 이 안에서 하면 메인 스레드가 아니라 셋 스레드에서 실행됨(ANR 방지)
            val stream = assets.open("quizzes.xml")

            val doBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

            val doc = doBuilder.parse(stream)

            val quizzesFromXMLDoc = doc.getElementsByTagName("quiz")
            val quizList = mutableListOf<Quiz>()
            for(idx in 0 until quizzesFromXMLDoc.length) {
                val e = quizzesFromXMLDoc.item(idx) as Element

                val type = e.getAttribute("type")
                val question = e.getElementsByTagName("question").item(0).textContent
                val answer = e.getElementsByTagName("answer").item(0).textContent
                val category = e.getElementsByTagName("category").item(0).textContent

                when(type) {
                    "ox" -> {
                        quizList.add(
                            Quiz(type = type,
                            question = question,
                            answer = answer,
                            category = category)
                        )
                    }
                    "multiple_choice" -> {
                        var choices = e.getElementsByTagName("choice")
                        var choiceList = mutableListOf<String>()
                        for(idx in 0 until choices.length) {
                            choiceList.add(choices.item(idx).textContent)
                        }
                        quizList.add(
                            Quiz(
                                type = type,
                                question = question,
                                answer = answer,
                                category = category,
                                guesses = choiceList))
                    }
                }

            }
            for(quiz in quizList) {
                db.quizDAO().insert(quiz)
            }
        }
    }

    lateinit var db : QuizDatabase
    lateinit var drawerToggle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = QuizDatabase.getInstance(this)

        Thread(Runnable {
            for(quiz in db.quizDAO().getAll()) {
                Log.d("mytag", quiz.toString())
            }
        }).start()




        val sp : SharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        if(sp.getBoolean("initialized", true)) {
            initQuizDataFromXMLFile()
            val editor = sp.edit()  // 데이터를 집어넣는 애
            editor.putBoolean("initialized", false) // 다음부턴 실행이 되지 않는다.
            editor.commit()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.drawer_nav_view)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame, QuizFragment())
            .commit()

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.quiz_solve -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizFragment())    // 프래그먼트 교체 위해 replace를 사 용
                        .commit()
                }
                R.id.quiz_manage -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizListFragment())
                        .commit()
                }
                R.id.quiz_make -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizCreateFragment())
                        .commit()
                }
            }

            drawerLayout.closeDrawers() // 메뉴 누른 후 자동으로 닫힘~

            true
        }

        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        ) {}

        // isDrawerIndicatorEnabled 속성을 true로 설정해 액션바의 왼쪽 상단에 위치한 햄버거 아이콘을 통해 내비게이션 드로어를 표시하고 숨길 수 있도록 합니다.
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        // setDisplayHomeAsUpEnabled 메서드를 호출해서 햄버거 아이콘을 표시하고 해당 아이콘을 클릭해 내비게이션 드로어를 열고 닫을 수 있도록 설
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {    // 만들어진 후에 실행
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()    // 햄버거 애니메이션이 생김
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }



}