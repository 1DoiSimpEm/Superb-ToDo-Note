package com.example.superbtodo.fragments.list


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.viewmodel.TaskViewModel
import com.example.superbtodo.databinding.FragmentListBinding
import com.example.superbtodo.adapters.ListAdapter
import com.example.superbtodo.services.*
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class ListFragment : Fragment(R.layout.fragment_list), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentListBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var adapter: ListAdapter
    private lateinit var deletedTask: Task
    private lateinit var selectedTask: Task


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)
        binding.toolbar.inflateMenu(R.menu.main_menu)
        initAnim()
        initAdapter()
        initViewModel()
        menuSelection()
        swipeToHandleEvent()
        navigate()
    }

    private fun initAnim() {
        binding.moveToAddBtn.startAnimation(
            AnimationUtils.loadAnimation(
                binding.moveToAddBtn.context,
                R.anim.fall_down
            )
        )
    }

    private fun navigate() {
        binding.moveToAddBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addingFragment)
        }
    }

    private fun initViewModel() {
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        mTaskViewModel.readNotDoneData().observe(viewLifecycleOwner) { tasks ->
            adapter.setData(tasks)
            if (tasks.size == 0) {
                binding.emptyLogo.visibility = View.VISIBLE
            } else {
                binding.emptyLogo.visibility = View.GONE
            }
            for (task in tasks){
                scheduleNotification(task.title,task.description,task.date)
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
                        deletedTask = adapter.getTaskAt(viewHolder.adapterPosition)
                        mTaskViewModel.deleteTask(deletedTask)
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
                        selectedTask = adapter.getTaskAt(viewHolder.adapterPosition)
                        if (!selectedTask.isDone) {
                            selectedTask.isDone = true
                            mTaskViewModel.updateTask(selectedTask)
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            Snackbar.make(
                                binding.recyclerView,
                                "You have just done ${selectedTask.title} task!",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Undo") {
                                    selectedTask.isDone = false
                                    mTaskViewModel.updateTask(selectedTask)
                                }.show()
                        } else {
                            Toast.makeText(
                                context,
                                "You have already done this task!",
                                Toast.LENGTH_LONG
                            ).show()
                            mTaskViewModel.updateTask(selectedTask)
                        }
                    }
                }

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
                    mTaskViewModel.deleteAllNotDoneTask()
                    true
                }
                R.id.menu_sortByDate -> {
                    mTaskViewModel.readNotDoneData().observe(viewLifecycleOwner) { task ->
                        task.sortBy { it.date }
                        adapter.setData(task)
                    }
                    true
                }
                R.id.menu_sortByTitle -> {
                    mTaskViewModel.readNotDoneData().observe(viewLifecycleOwner) { task ->
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

        mTaskViewModel.searchDbByTitle(searchQuery).observe(this) { list ->
            list.let {
                adapter.setData(it)
            }
        }
    }



    private fun createNotificationChannel()
    {
        val name = "Notification Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(title: String,message:String,time: String)
    {
        val intent = Intent(context, Notification::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, "$message\n in $time")
        val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val setTime = hourly.parse(time)?.time
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setTime?.let {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                it,
                pendingIntent
            )
        }
    }


}




