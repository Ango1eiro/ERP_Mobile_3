package com.rogoznyak.erp_mobile_3.settings

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rogoznyak.erp_mobile_3.R


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


        val checkConnectionPreference: Preference? = findPreference("checkConnection")
        checkConnectionPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                checkConnectionPreference?.setSummary("Waiting for status")
                viewModel.firstTodo.observe(this, Observer {
                    checkConnectionPreference?.setSummary(it.title)
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
