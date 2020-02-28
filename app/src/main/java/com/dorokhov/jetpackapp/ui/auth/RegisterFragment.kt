package com.dorokhov.jetpackapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.registrationFields?.let { registrationFields ->
                registrationFields.registration_email?.let {
                    input_email.setText(it)
                }

                registrationFields.registration_username?.let {
                    input_username.setText(it)
                }

                registrationFields.registration_password?.let {
                    input_password.setText(it)
                }

                registrationFields.registration_confirm_password?.let {
                    input_password_confirm.setText(it)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                registration_username = input_username.text.toString(),
                registration_email = input_email.text.toString(),
                registration_password = input_password.text.toString(),
                registration_confirm_password = input_password_confirm.text.toString()
            )
        )
    }
}
