package com.rogoznyak.erp_mobile_3.search.searchForUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.rogoznyak.erp_mobile_3.R
import com.rogoznyak.erp_mobile_3.database.getDatabase
import com.rogoznyak.erp_mobile_3.database.transformFromDatabaseUserToUser
import com.rogoznyak.erp_mobile_3.domain.User
import kotlinx.android.synthetic.main.search_for_user_fragment.view.*
import kotlinx.android.synthetic.main.search_fragment.view.*

class SearchForUserFragment : Fragment() {

    private lateinit var viewModel: SearchForUserFragmentViewModel
    private lateinit var adapter: SearchForUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application
        val dataSource = getDatabase(application).AppDao
        val viewModelFactory = SearchForUserFragmentViewModelFactory(dataSource, application)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchForUserFragmentViewModel::class.java)

        return inflater.inflate(R.layout.search_for_user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navController = findNavController()

        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let {
                view.listView.adapter = SearchForUserAdapter(
                    context,
                    it.transformFromDatabaseUserToUser()
                )
                view.listView.onItemClickListener =
                    OnItemClickListener { parent, view2, position, id ->
                        //here you can use the position to determine what checkbox to check
                        //this assumes that you have an array of your checkboxes as well. called checkbox
                        navController.previousBackStackEntry?.savedStateHandle?.set("user_guid",(parent.adapter.getItem(position) as User).guid)
                        navController.popBackStack()
                    }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }


}