package com.dorokhov.jetpackapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.ui.BaseActivity

class AuthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}