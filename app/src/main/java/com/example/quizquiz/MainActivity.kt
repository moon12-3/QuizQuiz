package com.example.quizquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.room.Entity

class MainActivity : AppCompatActivity() {
    lateinit var drawerToggle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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