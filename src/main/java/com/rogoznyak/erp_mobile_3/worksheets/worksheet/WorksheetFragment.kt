package com.rogoznyak.erp_mobile_3.worksheets.worksheet

import android.R.attr.maxLength
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.Utils.MaskWatcher
import com.rogoznyak.erp_mobile_3.database.DatabaseWorksheet
import com.rogoznyak.erp_mobile_3.databinding.WorksheetFragmentBinding
import com.rogoznyak.erp_mobile_3.domain.Worksheet
import com.rogoznyak.erp_mobile_3.network.NetworkWorksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.search.SearchType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


class WorksheetFragment : Fragment() {

    private lateinit var viewModel: WorksheetViewModel
    private lateinit var binding: WorksheetFragmentBinding

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialising daataBinding and viewModel
        binding = WorksheetFragmentBinding.inflate(inflater)

        viewModel = ViewModelProviders.of(this).get(WorksheetViewModel::class.java)
        binding.viewModel = viewModel

        var worksheetDataObserved = false

        // Retrievng args, if any exist
        val arguments = WorksheetFragmentArgs.fromBundle(requireArguments())
        if (arguments.guidWorksheet.equals(0L)) {
            // Nothing happens
        } else {
            viewModel.setWorksheetData(arguments.guidWorksheet)
        }

        viewModel.worksheetData.observe(viewLifecycleOwner,
            Observer<Worksheet> { worksheet ->
                if (!worksheetDataObserved) {
                    binding.textFieldDate.editText?.setText(worksheet.date)
                    binding.textFieldDuration.editText?.setText(worksheet.duration)
                    binding.textFieldDescription.editText?.setText(worksheet.description)
                    viewModel.setCounterpartByGuid(worksheet.counterpart.guid)
                    worksheetDataObserved = true
                }

            })

        // Initialising navigation and start to listen key
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(viewLifecycleOwner,
            Observer {result -> viewModel.setCounterpartByGuid(result)
            })

        //
        viewModel.navigateToSearch.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {

                    val action = WorksheetFragmentDirections.actionWorksheetFragmentToSearchFragment(SearchType.COUNTERPART)
                    navController.navigate(action)
                    viewModel.onNavigatedToSearch()
                }
            })

        viewModel.worksheetSent.observe(viewLifecycleOwner,
            Observer<Boolean> { isSent ->
                if (isSent == true) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.fab.visibility = View.GONE
                    uiScope.launch(Dispatchers.IO) {
                        try {
                            val todo = TodoRepository().sendWorksheet(
                                NetworkWorksheet(
                                    binding.textFieldDate.editText?.text.toString(),
                                    viewModel.counterpart.value!!.guid,
                                    binding.textFieldDuration.editText?.text.toString(),
                                    binding.textFieldDescription.editText?.text.toString()
                                )
                            )
                            viewModel.setWorksheetStatus(todo.title)
                        } catch (t:Throwable)
                        {
                            viewModel.setWorksheetStatus(t.toString())
                            t.printStackTrace()
                        } finally {
                            viewModel.worksheetSent()
                        }

                    }

                }
            })

        viewModel.worksheetStatus.observe(viewLifecycleOwner,
            Observer<String> { text ->
                    Snackbar.make(binding.root,text, Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                    binding.fab.visibility = View.VISIBLE

            })

        viewModel.worksheetSave.observe(viewLifecycleOwner,
            Observer<Boolean> { save ->
                if (save) {
                    uiScope.launch(Dispatchers.IO) {
                        var guid = 0L
                        if (viewModel.worksheetData.value != null) {
                            guid = viewModel.worksheetData.value!!.guid
                        }

                        TodoRepository().saveWorksheet(
                            DatabaseWorksheet(guid,viewModel.counterpart.value!!.guid,
                                binding.textFieldDescription.editText?.text.toString(),
                                binding.textFieldDate.editText?.text.toString(),
                                binding.textFieldDuration.editText?.text.toString()))
                    }
                }


            })

        viewModel.counterpart.observe(viewLifecycleOwner, Observer { counterpart -> binding.textFieldCounterpart.editText?.setText(counterpart.name) })

        binding.textFieldDate.editText?.setText(viewModel.date)

        binding.textFieldDuration.editText?.addTextChangedListener(MaskWatcher("##:##"))

        binding.textFieldDuration.setEndIconOnClickListener{ v ->
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setInputMode(INPUT_MODE_KEYBOARD)
                .build()

            picker.addOnPositiveButtonClickListener { _ ->
                val sTime = picker.hour.toString() + ":" + picker.minute.toString()
                binding.textFieldDuration.editText?.setText(sTime)
            }
            picker.show(parentFragmentManager,"TAG")
        }

        return binding.root
    }

}