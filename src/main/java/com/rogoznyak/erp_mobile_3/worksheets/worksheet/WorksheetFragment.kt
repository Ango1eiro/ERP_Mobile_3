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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.Utils.MaskWatcher
import com.rogoznyak.erp_mobile_3.database.DatabaseWorksheet
import com.rogoznyak.erp_mobile_3.databinding.WorksheetFragmentBinding
import com.rogoznyak.erp_mobile_3.network.NetworkWorksheet
import com.rogoznyak.erp_mobile_3.network.TodoRepository
import com.rogoznyak.erp_mobile_3.search.SearchType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


class WorksheetFragment : Fragment() {

    private lateinit var viewModel: WorksheetViewModel
    private lateinit var binding: WorksheetFragmentBinding

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onResume() {
        super.onResume()
        binding.editTextDate.clearFocus()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialising dataBinding and viewModel
        binding = WorksheetFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(WorksheetViewModel::class.java)
        binding.viewModel = viewModel

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
                    binding.textWorksheetStatus.setText(getString(R.string.status_in_progress))
                    binding.progressBar.visibility = View.VISIBLE
                    binding.fab.visibility = View.GONE
                    uiScope.launch(Dispatchers.IO) {
                        val todo = TodoRepository().sendWorksheet(NetworkWorksheet(viewModel.counterpart.value!!.guid,binding.editTextDuration.text.toString(),binding.editTextDescription.text.toString()))
                        viewModel.setWorksheetStatus(todo.title)
                        viewModel.worksheetSent()
                    }

                }
            })

        viewModel.worksheetStatus.observe(viewLifecycleOwner,
            Observer<String> { text ->
                    binding.textWorksheetStatus.setText(text)
                    binding.progressBar.visibility = View.GONE
                    binding.fab.visibility = View.VISIBLE

            })

        viewModel.worksheetSave.observe(viewLifecycleOwner,
            Observer<Boolean> { save ->
                if (save) {
                    uiScope.launch(Dispatchers.IO) {
                    TodoRepository().saveWorksheet(DatabaseWorksheet("s1d",viewModel.counterpart.value!!.guid,binding.editTextDescription.text.toString(),(binding.editTextDate as EditText).text.toString(),binding.editTextDuration.text.toString()))
                    }
                }


            })

        viewModel.counterpart.observe(viewLifecycleOwner, Observer { counterpart -> binding.editTextCounterpart.setText(counterpart.name) })

        binding.editTextDate.setText(viewModel.date)

        binding.editTextDuration.addTextChangedListener(MaskWatcher("##:##"))
//        binding.editTextDuration.filters = arrayOf<InputFilter>(LengthFilter(5))





        return binding.root
    }

}