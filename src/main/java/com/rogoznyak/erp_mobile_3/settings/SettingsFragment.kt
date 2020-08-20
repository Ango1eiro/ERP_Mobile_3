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
import com.rogoznyak.erp_mobile_3.network.UpdateStatus


class SettingsFragment : PreferenceFragmentCompat() {

    lateinit var viewModel: SettingsFragmentViewModel

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
                checkConnectionPreference?.setSummary("Waiting for status")
                viewModel.firstTodo.observe(this, Observer {
                    checkConnectionPreference?.setSummary(it.title)
                })
            true
        }

        // Finding checkConnection preference and assigning it click listener
        val updateCataloguesPreference: Preference? = findPreference("updateCatalogues")
        updateCataloguesPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                updateCataloguesPreference?.setSummary("Starting download")
                viewModel.updateStatus.observe(this, Observer {
                    when (it)
                    {
                        UpdateStatus.DONE -> {
                            updateCataloguesPreference?.setSummary("Done")
                        }
                        UpdateStatus.INPROGRESS -> updateCataloguesPreference?.setSummary("In progress")
                        UpdateStatus.ERROR -> updateCataloguesPreference?.setSummary("Error")
                    }
                })
                true
            }



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
