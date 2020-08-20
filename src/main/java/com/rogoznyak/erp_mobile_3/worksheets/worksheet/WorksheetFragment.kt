package com.rogoznyak.erp_mobile_3.worksheets.worksheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.databinding.WorksheetFragmentBinding
import com.rogoznyak.erp_mobile_3.databinding.WorksheetsFragmentBinding
import com.rogoznyak.erp_mobile_3.search.SearchType
import com.rogoznyak.erp_mobile_3.worksheets.WorksheetsViewModel

class WorksheetFragment : Fragment() {

    private lateinit var viewModel: WorksheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = WorksheetFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(WorksheetViewModel::class.java)
        binding.viewModel = viewModel

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(viewLifecycleOwner,
            Observer {
                    result ->
                binding.viewModel.counterpartGuid = result
                binding.editTextCounterpart.hint = result
            })

        viewModel.navigateToSearch.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {

                    val action = WorksheetFragmentDirections.actionWorksheetFragmentToSearchFragment(SearchType.COUNTERPART)
                    navController.navigate(action)
                    viewModel.onNavigatedToSearch()
                }
            })

        return binding.root
    }

}