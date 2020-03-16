package com.dorokhov.jetpackapp.ui.main.account

import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.repository.main.AccountRepository
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.BaseViewModel
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.main.account.state.AccountStateEvent
import com.dorokhov.jetpackapp.ui.main.account.state.AccountStateEvent.*
import com.dorokhov.jetpackapp.ui.main.account.state.AccountViewState
import com.dorokhov.jetpackapp.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    override fun handleStateEvent(it: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (it) {
            is GetAccountPropertiesEvent -> {
                return sessionManager.cashedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }
            is UpdatedAccountPropertiesEvent -> {
                return AbsentLiveData.create()
            }
            is ChangePasswordEvent -> {
                return AbsentLiveData.create()
            }
            is None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentNewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logOut() {
        sessionManager.logout()
    }
}