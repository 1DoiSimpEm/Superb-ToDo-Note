package com.example.superbtodo.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.viewmodel.TaskViewModel
import com.example.superbtodo.databinding.FragmentListBinding
import com.example.superbtodo.fragments.list.adapters.ListAdapter
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding: FragmentListBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var adapter: ListAdapter
    private lateinit var deletedTask: Task
    private lateinit var selectedTask: Task


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)

        initAdapter()
        initViewModel()
        navigate()
        swipeToHandleEvent()
    }

    private fun navigate() {
        binding.moveToAddBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addingFragment)
        }
    }

    private fun initViewModel() {
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        mTaskViewModel.readAllData.observe(viewLifecycleOwner) { task ->
            adapter.setData(task)
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
                        deletedTask = adapter.getTaskAt(viewHolder.adapterPosition)
                        mTaskViewModel.deleteTask(deletedTask)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        Snackbar.make(
                            binding.recyclerView,
                            "${deletedTask.content} has just been deleted!",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Undo") {
                                mTaskViewModel.addTask(deletedTask)
                            }.show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        selectedTask = adapter.getTaskAt(viewHolder.adapterPosition)
                        if (!selectedTask.isDone) {
                            selectedTask.isDone = true
                            mTaskViewModel.updateTask(selectedTask)
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            Snackbar.make(
                                binding.recyclerView,
                                "You have just done ${selectedTask.content} task!",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Undo") {
                                    selectedTask.isDone = false
                                    mTaskViewModel.updateTask(selectedTask)
                                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                                }.show()
                        } else {
                            Toast.makeText(
                                context,
                                "You have already done this task!",
                                Toast.LENGTH_LONG
                            ).show()
                            mTaskViewModel.updateTask(selectedTask)
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                        }

                    }

                }

            }
        }).attachToRecyclerView(binding.recyclerView)
    }


}