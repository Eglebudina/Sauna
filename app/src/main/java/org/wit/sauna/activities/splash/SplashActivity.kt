package org.wit.sauna.activities.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import androidx.core.view.ViewCompat.animate
import com.github.florent37.viewanimator.ViewAnimator
import org.wit.sauna.R
import org.wit.sauna.activities.signIn.SignInActivity
import yanzhikai.textpath.SyncTextPathView
import yanzhikai.textpath.painter.ArrowPainter

class SplashActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val welcomeTxt: SyncTextPathView = findViewById(R.id.welcome_view)
        val logoCont: LinearLayout = findViewById(R.id.logo_container)
        val mIntent: Intent = Intent(this, SignInActivity::class.java)
        welcomeTxt.setPathPainter(ArrowPainter())
        welcomeTxt.setFillColor(true)
        welcomeTxt.startAnimation(0f, 1f)
        ViewAnimator.animate(logoCont)
            .slit()
            .duration(2000)
            .start()

        val handler = Handler()
        handler.postDelayed({
            startActivity(mIntent)
            finish()
        }, 3000)
    }
}