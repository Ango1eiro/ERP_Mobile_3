package com.rogoznyak.erp_mobile_3.settings

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.database.getDatabase
import com.rogoznyak.erp_mobile_3.firebase.MyFirebaseMessagingService
import com.rogoznyak.erp_mobile_3.network.UpdateStatus
import kotlinx.coroutines.*


class SettingsFragment : PreferenceFragmentCompat() {

    lateinit var viewModel: SettingsFragmentViewModel

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    val coroutineExceptionHandler = CoroutineExceptionHandler{CoroutineContext, throwable ->
        throwable.printStackTrace()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Initialisating viewModel
        viewModel = ViewModelProviders.of(this).get(SettingsFragmentViewModel::class.java)

        // Finding password preference
        val passwordPreference: EditTextPreference? = findPreference("password")

        // Setting password summary
        passwordPreference?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text)) {
                    "Not set"
                } else {
                    "*****"
                }
            }

        // Finding checkConnection preference and assigning it click listener
        val checkConnectionPreference: Preference? = findPreference("checkConnection")
        checkConnectionPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                checkConnectionPreference?.setSummary(R.string.waiting_for_status)
                viewModel.setConnectionStatusNeedsUpdate(true)

            true
        }

        viewModel.connectionStatusNeedsUpdate.observe(this, Observer {
            if (it) uiScope.launch() {
                try {
                    checkConnectionPreference?.summary = viewModel.getConnectionStatus()
                } catch (t: Throwable) {
                    t.printStackTrace()
                    checkConnectionPreference?.summary = t.localizedMessage
                } finally {
                    viewModel.setConnectionStatusNeedsUpdate(false)
                }
            }
        })

        // Finding checkConnection preference and assigning it click listener
        val updateCataloguesPreference: Preference? = findPreference("updateCatalogues")
        updateCataloguesPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                updateCataloguesPreference?.summary = ("In progress")
                viewModel.setUpdateStatusNeedsUpdate(true)
                true
            }

        viewModel.updateStatusNeedsUpdate.observe(this, Observer{
            if (it) uiScope.launch {
                try {
                    updateCataloguesPreference?.summary = viewModel.getUpdateStatus().toString()
                } catch (t: Throwable) {
                    updateCataloguesPreference?.summary = t.localizedMessage
                    t.printStackTrace()
                } finally {
                    viewModel.setUpdateStatusNeedsUpdate(false)
                }
            }
        })

        val sendToken: Preference? = findPreference("sendToken")
        sendToken?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
              sendToken?.setSummary(R.string.waiting_for_status)
                viewModel.setSendTokenNeedsUpdate(true)

                true
            }

        viewModel.sendTokenNeedsUpdate.observe(this, Observer {
            if (it) uiScope.launch() {
                try {
                    val myFB = MyFirebaseMessagingService()
                    val token = myFB.getToken()
                    if (token != null)
                        sendToken?.summary = viewModel.sendToken(token).title
                    else
                        sendToken?.summary = "Token is null"
                } catch (t: Throwable) {
                    t.printStackTrace()
                    sendToken?.summary = t.localizedMessage
                } finally {
                    viewModel.setSendTokenNeedsUpdate(false)
                }
            }
        })

    }

    //return the password in asterisks
    private fun setAsterisks(length: Int): String? {
        val sb = StringBuilder()
        for (s in 0 until length) {
            sb.append("*")
        }
        return sb.toString()
    }

}
