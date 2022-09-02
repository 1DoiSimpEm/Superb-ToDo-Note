package com.example.superbtodo.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)
        adapter = ListAdapter{ task ->
            handlerTaskData(task)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        mTaskViewModel.readAllData.observe(viewLifecycleOwner) { task ->
            adapter.setData(task)
        }

//        isItemEmpty()
        binding.moveToAddBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addingFragment)
        }
        swipeToDeleteItem()
    }

    private fun handlerTaskData(task: Task) {
        mTaskViewModel.updateTask(Task(task.id,task.date,task.content,task.timeLeft,task.isDone))
    }

    private fun isItemEmpty() {
        if (adapter.itemCount == 0) {
            binding.emptyLogo.visibility = View.VISIBLE
        } else {
            binding.emptyLogo.visibility = View.GONE
        }
    }

    private fun swipeToDeleteItem() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deletedTask = adapter.getTaskAt(viewHolder.adapterPosition)
                mTaskViewModel.deleteTask(adapter.getTaskAt(viewHolder.adapterPosition))
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
        }).attachToRecyclerView(binding.recyclerView)
    }

}