package com.example.superbtodo.fragments.add

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.databinding.FragmentAddingBinding
import com.example.superbtodo.viewmodel.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nordan.dialog.Animation
import com.nordan.dialog.NordanAlertDialog
import java.text.SimpleDateFormat
import java.util.*


class AddingFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddingBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var lastUpdate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddingBinding.bind(view)
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        binding.addBtn.setOnClickListener {
            addNewTask()
        }
        getDate()
        getTime()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getTime() {
        binding.taskTime.setOnTouchListener { _: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val calendar = Calendar.getInstance()
                hour = calendar.get(Calendar.HOUR)
                minute = calendar.get(Calendar.MINUTE)


                // Launch Time Picker Dialog
                timePickerDialog = TimePickerDialog(
                    activity,
                    { _: TimePicker?, hourOfDay: Int, minute: Int ->
                        time = String.format("%02d:%02d", hourOfDay, minute)
                        binding.taskTime.setText(time)
                        timePickerDialog.dismiss()
                    }, hour, minute, false
                )
                timePickerDialog.show()
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getDate() {
        binding.taskDate.setOnTouchListener { _: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val calendar = Calendar.getInstance()
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
                datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    { _: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                        date = String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear + 1, year1)
                        binding.taskDate.setText(date)
                        datePickerDialog.dismiss()
                    }, year, month, day
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()
            }
            true
        }
    }

    private fun getTimeLeft(timeNow: String, timeEnd: Date): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dob = sdf.parse(timeNow)
        val days = (timeEnd.time - dob!!.time) / 86400000
        val hours = (timeEnd.time - dob.time) % 86400000 / 3600000
        val minutes = (timeEnd.time - dob.time) % 86400000 % 3600000 / 60000
        return "$days days $hours hours $minutes minutes left"
    }

    private fun addNewTask() {
        if (validateFields()) {
            val mTitle = binding.addTaskTitle.text.toString()
            val mDescription = binding.addTaskDescription.text.toString()
            val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val hourlyForLastUpdate =
                SimpleDateFormat("HH:mm - dd.MM.yyyy ", Locale.getDefault())
            lastUpdate =
                "Last Update: " + hourlyForLastUpdate.format(System.currentTimeMillis())
            val mDate = "$date $time"
            val mTimeLeft = getTimeLeft(
                hourly.format(System.currentTimeMillis()),
                hourly.parse(mDate) as Date
            )
            val isDone = mTimeLeft.contains("-")
            val task = Task(0, mDate, mTitle, mDescription, mTimeLeft, lastUpdate, isDone)
            NordanAlertDialog.Builder(context as Activity?)
                .setAnimation(Animation.SIDE)
                .isCancellable(false)
                .setTitle("A task has just been added!")
                .setMessage("You have just added aa Task!")
                .setIcon(R.drawable.done_2, true)
                .setPositiveBtnText("Great!")
                .onPositiveClicked {
                    mTaskViewModel.addTask(task)
                    findNavController().navigate(R.id.action_addingFragment_to_listFragment)
                }
                .build().show()
        }

    }


    private fun validateFields(): Boolean {
        if (binding.addTaskTitle.text.isEmpty()) {
            Toast.makeText(context, "Please enter a valid title", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.addTaskDescription.text.isEmpty()) {
            Toast.makeText(context, "Please enter a valid description", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.taskDate.text.isEmpty()) {
            Toast.makeText(context, "Please pick the date", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.taskTime.text.isEmpty()) {
            Toast.makeText(context, "Please pick the time", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}