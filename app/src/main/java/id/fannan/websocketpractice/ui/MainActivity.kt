package id.fannan.websocketpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.fannan.websocketpractice.R
import id.fannan.websocketpractice.ui.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}