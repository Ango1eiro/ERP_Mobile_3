package com.rogoznyak.erp_mobile_3.worksheets.worksheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rogoznyak.erp_mobile_3.databinding.WorksheetFragmentBinding
import com.rogoznyak.erp_mobile_3.databinding.WorksheetsFragmentBinding
import com.rogoznyak.erp_mobile_3.worksheets.WorksheetsViewModel

class WorksheetFragment : Fragment() {

    private lateinit var viewModel: WorksheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.home_fragment, container, false)
//        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)


        val binding = WorksheetFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(WorksheetViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.navigateToSearch.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
//                    navController.navigate(R.id.action_homeFragment_to_gdgListFragment)
                    viewModel.onNavigatedToSearch()
                }
            })

        return binding.root
    }
}