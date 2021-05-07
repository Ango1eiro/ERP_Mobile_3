package com.rogoznyak.erp_mobile_3.tasks.task

import android.R.attr.maxLength
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.Utils.MaskWatcher
import com.rogoznyak.erp_mobile_3.database.DatabaseTask
import com.rogoznyak.erp_mobile_3.database.DatabaseWorksheet
import com.rogoznyak.erp_mobile_3.databinding.TaskFragmentBinding
import com.rogoznyak.erp_mobile_3.databinding.WorksheetFragmentBinding
import com.rogoznyak.erp_mobile_3.domain.Task
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.network.NetworkTask
import com.rogoznyak.erp_mobile_3.network.NetworkWorksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.search.SearchType
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random


class TaskFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var binding: TaskFragmentBinding

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onResume() {
        super.onResume()
//        binding.textFieldDate.clearFocus()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialising dataBinding and viewModel
        binding = TaskFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        binding.viewModel = viewModel

        var taskDataObserved = false

        // Retrievng args, if any exist
        val arguments = TaskFragmentArgs.fromBundle(requireArguments())
        if (arguments.guidTask.equals(0L)) {
            // Nothing happens
        } else {
            viewModel.setTaskData(arguments.guidTask)
        }

        viewModel.taskData.observe(viewLifecycleOwner,
            Observer<Task> { task ->
                if (!taskDataObserved) {
                    binding.textFieldDate.editText?.setText(task.date)
                    binding.textFieldUser.editText?.setText(task.user.name)
                    binding.textFieldDescription.editText?.setText(task.description)
                    binding.textFieldUser.editText?.setText(task.user.name)
                    viewModel.setCounterpartByGuid(task.counterpart.guid)
                    viewModel.setUserByGuid(task.user.guid)
                    taskDataObserved = true
                }

            })

        // Initialising navigation and start to listen key
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(viewLifecycleOwner,
            Observer {result -> viewModel.setCounterpartByGuid(result)
            })
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("user_guid")?.observe(viewLifecycleOwner,
            Observer {result -> viewModel.setUserByGuid(result)
            })

        //
        viewModel.navigateToSearch.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {

                    val action = TaskFragmentDirections.actionTaskFragmentToSearchFragment(SearchType.COUNTERPART)
                    navController.navigate(action)
                    viewModel.onNavigatedToSearch()
                }
            })

        viewModel.navigateToSearchU.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {

                    val action = TaskFragmentDirections.actionTaskFragmentToSearchForUserFragment()
                    navController.navigate(action)
                    viewModel.onNavigatedToSearchU()
                }
            })

        viewModel.taskSent.observe(viewLifecycleOwner,
            Observer<Boolean> { isSent ->
                if (isSent == true) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.fab.visibility = View.GONE
                    uiScope.launch(Dispatchers.IO) {
                        try {
                            val todo = TodoRepository().sendTask(
                                NetworkTask(date = binding.textFieldDate.editText?.text.toString(),
                                    counterpart = viewModel.counterpart.value!!.guid,
                                    user = viewModel.user.value!!.guid,
                                    description = binding.textFieldDescription.editText?.text.toString()))
                            viewModel.setTaskStatus(todo.title)

                        } catch (t: Throwable) {
                            viewModel.setTaskStatus(t.toString())
                            t.printStackTrace()
                        } finally {
                            viewModel.taskSent()
                        }


                    }

                }
            })

        viewModel.taskStatus.observe(viewLifecycleOwner,
            Observer<String> { text ->
                Snackbar.make(binding.root,text,Snackbar.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
                binding.fab.visibility = View.VISIBLE

            })

        viewModel.taskSave.observe(viewLifecycleOwner,
            Observer<Boolean> { save ->
                if (save) {
                    uiScope.launch(Dispatchers.IO) {
                        var guid = 0L
                        if (viewModel.taskData.value != null) {
                            guid = viewModel.taskData.value!!.guid
                        }

                        TodoRepository().saveTask(
                            DatabaseTask(guid,
                                viewModel.counterpart.value!!.guid,
                                viewModel.user.value!!.guid,
                                binding.textFieldDescription.editText?.text.toString(),
                                binding.textFieldDate.editText?.text.toString()))
                    }
                }


            })

        viewModel.counterpart.observe(viewLifecycleOwner, Observer { counterpart -> binding.textFieldCounterpart.editText?.setText(counterpart.name) })

        viewModel.user.observe(viewLifecycleOwner, Observer { user -> binding.textFieldUser.editText?.setText(user.name) })

        binding.textFieldDate.editText?.setText(viewModel.date)

//        binding.editTextUser.addTextChangedListener(MaskWatcher("##:##"))
//        binding.editTextDuration.filters = arrayOf<InputFilter>(LengthFilter(5))





        return binding.root
    }

}