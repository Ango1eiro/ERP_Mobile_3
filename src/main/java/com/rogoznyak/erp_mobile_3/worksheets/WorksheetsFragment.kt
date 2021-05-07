package com.rogoznyak.erp_mobile_3.worksheets

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.database.getDatabase
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseCounterpartToCounterpart
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseWorksheetFullDataToWorksheet
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseWorksheetToWorksheet
import com.rogoznyak.erp_mobile_3.databinding.WorksheetsFragmentBinding
import com.rogoznyak.erp_mobile_3.search.SearchFragmentViewModel
import com.rogoznyak.erp_mobile_3.search.SearchFragmentViewModelFactory
import com.rogoznyak.erp_mobile_3.search.SleepNightAdapter
import com.rogoznyak.erp_mobile_3.search.SleepNightListener


class WorksheetsFragment : Fragment() {

    private lateinit var viewModel: WorksheetsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lateinit var syncAnimation: AnimationDrawable

        val binding = WorksheetsFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val dataSource = getDatabase(application).AppDao
        val viewModelFactory = WorksheetsViewModelFactory(dataSource, application)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(WorksheetsViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.navigateToNewWorksheet.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(R.id.action_worksheetsFragment_to_worksheetFragment)
                    viewModel.onNavigatedToWorksheet()
                }
            })

        val manager = LinearLayoutManager(activity)
        binding.itemList.layoutManager = manager

        val adapter = WorksheetsAdapter(WorksheetsListener { worksheet ->
            worksheet?.let {
                binding.root.findNavController().navigate(
                    WorksheetsFragmentDirections
                        .actionWorksheetsFragmentToWorksheetFragment(worksheet.guid))
                viewModel.doneNavigatingToExistingWorksheet()
        }})
        binding.itemList.adapter = adapter

        viewModel.worksheetsList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it.transformFromDatabaseWorksheetFullDataToWorksheet())
            }
        })

        binding.syncImageView.setBackgroundResource(android.R.drawable.ic_popup_sync)
        syncAnimation = binding.syncImageView.background as AnimationDrawable

        viewModel.syncIsOn.observe(viewLifecycleOwner, Observer<Boolean> { syncIsOn ->
            if (syncIsOn) {
                syncAnimation.start()
            } else {
                syncAnimation.stop()
                Snackbar.make(binding.root,viewModel.getMsg(), Snackbar.LENGTH_LONG).show()
            }

        })

        return binding.root
    }
}
