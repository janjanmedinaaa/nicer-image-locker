package com.medina.juanantonio.nicer

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.DefaultLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils.patternToString
import com.medina.juanantonio.nicer.databinding.ActivityLockScreenBinding
import com.medina.juanantonio.nicer.managers.PasswordLockManager
import com.medina.juanantonio.nicer.managers.PasswordLockStatus
import com.medina.juanantonio.nicer.managers.SharedPrefsManager

class LockScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLockScreenBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var passwordLockManager: PasswordLockManager

    private var prevPasswordInput: String? = null
    private var passwordLockStatus = PasswordLockStatus.UNSECURED
    private var currentAttempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_SECURE, FLAG_SECURE)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_lock_screen
        )

        sharedPrefsManager = SharedPrefsManager(this)
        passwordLockManager = PasswordLockManager(sharedPrefsManager)

        binding.patternLockView.run {
            addPatternLockListener(
                object : DefaultLockViewListener() {
                    override fun onComplete(
                        pattern: MutableList<PatternLockView.Dot>?
                    ) {
                        validatePattern(patternToString(this@run, pattern))
                        clearPattern()
                    }
                }
            )
        }

        setupLockScreen()
    }

    private fun setupLockScreen() {
        passwordLockStatus = passwordLockManager.checkStatus()
        binding.textPasswordTitle.text =
            when (passwordLockStatus) {
                PasswordLockStatus.SECURED -> {
                    getString(R.string.enter_password)
                }
                PasswordLockStatus.UNSECURED -> {
                    getString(R.string.set_password)
                }
            }
    }

    private fun validatePattern(pattern: String) {
        currentAttempts++

        when (passwordLockStatus) {
            PasswordLockStatus.SECURED -> {
                if (passwordLockManager.validatePatternLock(pattern)) {
                    intent = Intent(this@LockScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.password_invalid),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            PasswordLockStatus.UNSECURED -> {
                if (currentAttempts == 1) {
                    binding.textPasswordTitle.text = getString(R.string.confirm_password)
                } else if (currentAttempts == 2) {
                    if (prevPasswordInput == pattern) {
                        passwordLockManager.savePatternLock(pattern)
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.password_not_match),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    setupLockScreen()
                    currentAttempts = 0
                    prevPasswordInput = null
                }
            }
        }

        prevPasswordInput = pattern
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
