package com.medina.juanantonio.nicer

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.DefaultLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils.patternToString
import com.medina.juanantonio.nicer.common.Constants.PATTERN.LETTER_B
import com.medina.juanantonio.nicer.databinding.ActivityLockScreenBinding

class LockScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLockScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_SECURE, FLAG_SECURE)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_lock_screen
        )

        binding.patternLockView.run {
            addPatternLockListener(
                object : DefaultLockViewListener() {
                    override fun onComplete(
                        pattern: MutableList<PatternLockView.Dot>?
                    ) {
                        val patternText =
                            patternToString(this@run, pattern)
                        if (patternText == LETTER_B) {
                            intent = Intent(
                                this@LockScreenActivity,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                        }
                        clearPattern()
                    }
                }
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
