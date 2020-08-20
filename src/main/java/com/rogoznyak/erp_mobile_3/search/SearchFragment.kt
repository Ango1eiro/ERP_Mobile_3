package com.rogoznyak.erp_mobile_3.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rogoznyak.erp_mobile_3.MyCustomApplication
import com.rogoznyak.erp_mobile_3.database.getDatabase
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseCounterpartToCounterpart
import com.rogoznyak.erp_mobile_3.databinding.SearchFragmentBinding
import com.rogoznyak.erp_mobile_3.databinding.TasksFragmentBinding
import javax.inject.Inject

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchFragmentViewModel

    val args: SearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SearchFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val dataSource = getDatabase(application).AppDao
        val viewModelFactory = SearchFragmentViewModelFactory(dataSource, application)
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(SearchFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val navController = findNavController()

        binding.textView.text = args.searchType.toString()

        binding.textView.setOnClickListener { v ->
            navController.previousBackStackEntry?.savedStateHandle?.set("key","123123123")
            navController.popBackStack()
        }

        val manager = LinearLayoutManager(activity)

//        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int) =  when (position) {
//                0 -> 3
//                else -> 1
//            }
//        }
        binding.itemList.layoutManager = manager

        val adapter = SleepNightAdapter(SleepNightListener { guid ->
            navController.previousBackStackEntry?.savedStateHandle?.set("key",guid)
            navController.popBackStack()
        })
        binding.itemList.adapter = adapter

        viewModel.counterparts.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it.transformFromDatabaseCounterpartToCounterpart())
            }
        })



        return binding.root
    }

}

enum class SearchType {
    COUNTERPART,USER
}