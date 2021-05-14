package com.rogoznyak.erp_mobile_3.search.searchForUser

import android.os.Bundle
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
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
import kotlinx.coroutines.*

class SearchForUserFragment : Fragment() {

    private lateinit var viewModel: SearchForUserFragmentViewModel
    private lateinit var adapter: SearchForUserAdapter

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val sV = menu.findItem(R.id.action_apply_filter).actionView as SearchView
        sV.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.setSearchString(p0)
                uiScope.launch {
                    viewModel.updateUsersWithFilter()
                    adapter.userList = viewModel.getUserList()
                    adapter.notifyDataSetChanged()
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.setSearchString(p0)
                uiScope.launch {
                    viewModel.updateUsersWithFilter()
                    adapter.userList = viewModel.getUserList()
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_apply_filter -> {
                 true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navController = findNavController()
        adapter = SearchForUserAdapter(context)
        uiScope.launch{
            viewModel.updateUsersWithFilter()
            adapter.userList = viewModel.getUserList()
            adapter.notifyDataSetChanged()
            view.listView.adapter = adapter
            view.listView.onItemClickListener =
                OnItemClickListener { parent, view2, position, id ->
                    //here you can use the position to determine what checkbox to check
                    //this assumes that you have an array of your checkboxes as well. called checkbox
                    navController.previousBackStackEntry?.savedStateHandle?.set("user_guid",(parent.adapter.getItem(position) as User).guid)
                    navController.popBackStack()
                }
        }

        super.onViewCreated(view, savedInstanceState)
    }


}