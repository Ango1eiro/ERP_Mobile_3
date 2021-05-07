package com.rogoznyak.erp_mobile_3.tasks

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.database.getDatabase
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseTaskFullDataToTask
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseWorksheetFullDataToWorksheet
import com.rogoznyak.erp_mobile_3.databinding.HomeFragmentBinding
import com.rogoznyak.erp_mobile_3.databinding.TasksFragmentBinding
import com.rogoznyak.erp_mobile_3.worksheets.WorksheetsAdapter
import com.rogoznyak.erp_mobile_3.worksheets.WorksheetsFragmentDirections
import com.rogoznyak.erp_mobile_3.worksheets.WorksheetsListener
import com.rogoznyak.erp_mobile_3.worksheets.WorksheetsViewModelFactory


class TasksFragment : Fragment() {

    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lateinit var syncAnimation: AnimationDrawable

        val binding = TasksFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val dataSource = getDatabase(application).AppDao
        val viewModelFactory = TasksViewModelFactory(dataSource, application)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(TasksViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.navigateToNewTask.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(R.id.action_tasksFragment_to_taskFragment)
                    viewModel.onNavigatedToTask()
                }
            })

        val manager = LinearLayoutManager(activity)
        binding.itemList.layoutManager = manager

        val adapter = TasksAdapter(TasksListener { task ->
            task?.let {
                binding.root.findNavController().navigate(
                    TasksFragmentDirections
                        .actionTasksFragmentToTaskFragment(task.guid))
                viewModel.doneNavigatingToExistingTask()
            }})
        binding.itemList.adapter = adapter

        viewModel.tasksList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it.transformFromDatabaseTaskFullDataToTask())
            }
        })


        binding.syncImageView.setBackgroundResource(android.R.drawable.ic_popup_sync)
        syncAnimation = binding.syncImageView.background as AnimationDrawable

        viewModel.syncIsOn.observe(viewLifecycleOwner, Observer<Boolean> { syncIsOn ->
            if (syncIsOn) {
                syncAnimation.start()
            } else {
                syncAnimation.stop()
            }

        })
        return binding.root
    }
}
