package com.rogoznyak.erp_mobile_3.worksheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.databinding.WorksheetsFragmentBinding


class WorksheetsFragment : Fragment() {


    private lateinit var viewModel: WorksheetsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = WorksheetsFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(WorksheetsViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.navigateToNewWorksheet.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(R.id.action_worksheetsFragment_to_worksheetFragment)
                    viewModel.onNavigatedToWorksheet()
                }
            })

        return binding.root
    }
}
