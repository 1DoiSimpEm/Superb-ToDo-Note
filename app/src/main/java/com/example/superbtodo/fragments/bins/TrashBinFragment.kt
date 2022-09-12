package com.example.superbtodo.fragments.bins

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.databinding.FragmentTrashBinBinding
import com.example.superbtodo.adapters.ListAdapter
import com.example.superbtodo.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class TrashBinFragment : Fragment(R.layout.fragment_trash_bin), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentTrashBinBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var adapter: ListAdapter
    private lateinit var deletedTask: Task
    private lateinit var selectedTask: Task

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashBinBinding.bind(view)
        binding.toolbar.inflateMenu(R.menu.main_menu)
        initAdapter()
        initViewModel()
        menuSelection()
        swipeToHandleEvent()
    }

    private fun initViewModel() {
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        mTaskViewModel.readDoneData().observe(viewLifecycleOwner) { task ->
            adapter.setData(task)
            if (task.size == 0) {
                binding.emptyLogo.visibility = View.VISIBLE
            } else {
                binding.emptyLogo.visibility = View.GONE
            }
        }
    }

    private fun initAdapter() {
        adapter = ListAdapter { task ->
            handlerTaskData(task)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun handlerTaskData(task: Task) {
        mTaskViewModel.updateTask(task)
    }


    private fun swipeToHandleEvent() {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deletedTask = adapter.getTaskAt(viewHolder.layoutPosition)
                        mTaskViewModel.deleteTask(deletedTask)
                        adapter.notifyItemRemoved(viewHolder.layoutPosition)
                        Snackbar.make(
                            binding.recyclerView,
                            "${deletedTask.title} has just been deleted!",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Undo") {
                                mTaskViewModel.addTask(deletedTask)
                            }.show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        selectedTask = adapter.getTaskAt(viewHolder.layoutPosition)
                        Toast.makeText(
                            context,
                            "You have already done this task!",
                            Toast.LENGTH_LONG
                        ).show()
                        mTaskViewModel.updateTask(selectedTask)
                        adapter.notifyItemChanged(viewHolder.layoutPosition)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context!!, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate()


                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun menuSelection() {
        binding.toolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.menu_search -> {
                    val searchView = menu.actionView as? SearchView
                    searchView?.isSubmitButtonEnabled = true
                    searchView?.setOnQueryTextListener(this)
                    true
                }
                R.id.menu_delete -> {
                    mTaskViewModel.deleteAllDoneTask()
                    true
                }
                R.id.menu_sortByDate -> {
                    mTaskViewModel.readDoneData().observe(viewLifecycleOwner) { task ->
                        task.sortBy { it.date }
                        adapter.setData(task)
                    }
                    true
                }
                R.id.menu_sortByTitle -> {
                    mTaskViewModel.readDoneData().observe(viewLifecycleOwner) { task ->
                        task.sortBy { it.title }
                        adapter.setData(task)
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchDB(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchDB(query)
        }
        return true
    }

    private fun searchDB(query: String) {
        val searchQuery = "%$query%"
        mTaskViewModel.searchIsDoneDbByTitle(searchQuery).observe(this) { list ->
            list.let {
                adapter.setData(it)
            }
        }
    }
}