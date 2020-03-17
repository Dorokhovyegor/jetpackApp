package com.dorokhov.jetpackapp.ui.main.account

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_update_account.*

class UpdateAccountFragment : BaseAccountFragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun setAccountFields(accountProperties: AccountProperties) {
        input_email?.setText(accountProperties.email)
        input_username?.setText(accountProperties.username)
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            println("$TAG: UpdateAccountFragment: DataState ${dataState}")

        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            viewState?.let {
                it.accountProperties?.let {
                    println("$TAG: UpdateAccountFragment: ViewState ${it}")
                    setAccountFields(it)
                }
            }
        })
    }

    private fun saveChanges() {
        viewModel.setStateEvent(AccountStateEvent.UpdatedAccountPropertiesEvent(
            input_email.text.toString(),
            input_username.text.toString()
        ))
    }

    // создает меню
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu, menu)

    }

    // позволяет кликать по менюшкам
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}