package com.dorokhov.jetpackapp.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.ui.auth.state.AuthStateEvent
import com.dorokhov.jetpackapp.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()

        login_button.setOnClickListener {
            login()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let { loginFields ->
                loginFields.login_email?.let { email ->
                    input_email.setText(email)
                }

                loginFields.login_password?.let { password ->
                    input_password.setText(password)
                }
            }
        })
    }

    fun login() {
        viewModel.setStateEvent(AuthStateEvent.LoginAttemptEvent(
            input_email.text.toString(),
            input_password.text.toString()
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                login_email = input_email.text.toString(),
                login_password = input_password.text.toString()
            )
        )
    }
}
