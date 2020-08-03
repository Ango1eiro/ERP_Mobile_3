package com.rogoznyak.erp_mobile_3.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.settings.SettingsViewModel
import com.rogoznyak.erp_mobile_3.databinding.HomeFragmentBinding
import com.rogoznyak.erp_mobile_3.databinding.SettingsFragmentBinding


class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
////        val view = inflater.inflate(R.layout.home_fragment, container, false)
////        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
//
//
//        val binding = SettingsFragmentBinding.inflate(inflater)
//        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
//        binding.viewModel = viewModel
//
//        viewModel.navigateToSearch.observe(viewLifecycleOwner,
//            Observer<Boolean> { shouldNavigate ->
//                if (shouldNavigate == true) {
//                    val navController = binding.root.findNavController()
////                    navController.navigate(R.id.action_homeFragment_to_gdgListFragment)
//                    viewModel.onNavigatedToSearch()
//                }
//            })
//
//        return binding.root
//    }
}
