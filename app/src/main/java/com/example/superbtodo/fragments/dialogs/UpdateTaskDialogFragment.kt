package com.example.superbtodo.fragments.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle

import android.view.MotionEvent
import android.view.View

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.databinding.FragmentUpdatetaskdialogBinding
import com.example.superbtodo.utils.DateFormatUtil
import com.example.superbtodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*


class UpdateTaskDialogFragment : DialogFragment(R.layout.fragment_updatetaskdialog) {
    private lateinit var binding: FragmentUpdatetaskdialogBinding
    private lateinit var mTaskViewModel: TaskViewModel
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private val args: UpdateTaskDialogFragmentArgs by navArgs()
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0
    private lateinit var date: String
    private lateinit var time: String

    private object DateFormatter : DateFormatUtil()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
        binding = FragmentUpdatetaskdialogBinding.bind(view)
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        initGadgets()
        binding.addBtn.setOnClickListener {
            saveTask()
        }
        getDate()
        getTime()
    }

    private fun initGadgets() {
        binding.addTaskTitle.setText(args.currentTask.title)
        binding.addTaskDescription.setText(args.currentTask.description)
        binding.taskDate.setText(getOldDate())
        binding.taskTime.setText(getOldTime())
    }

    private fun getOldDate(): String =
        DateFormatter.dateFormat()
            .format(DateFormatter.hourly().parse(args.currentTask.date) as Date)

    private fun getOldTime(): String =
        DateFormatter.timeFormat()
            .format(DateFormatter.hourly().parse(args.currentTask.date) as Date)

    @SuppressLint("ClickableViewAccessibility")
    private fun getTime() {
        binding.taskTime.setOnTouchListener { _: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val calendar = Calendar.getInstance()
                hour = calendar.get(Calendar.HOUR)
                minute = calendar.get(Calendar.MINUTE)

                // Launch Time Picker Dialog
                timePickerDialog = TimePickerDialog(
                    activity, { _: TimePicker?, hourOfDay: Int, minute: Int ->
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
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()
            }
            true
        }
    }

    private fun saveTask() {
        val mTitle = binding.addTaskTitle.text.toString()
        val mDescription = binding.addTaskDescription.text.toString()
        val hourlyForLastUpdate = SimpleDateFormat("HH:mm - dd.MM.yyyy ", Locale.getDefault())
        val lastUpdate = "Last Update: " + hourlyForLastUpdate.format(System.currentTimeMillis())
        if (this::date.isInitialized and this::time.isInitialized) {
            val newDate = "$date $time"
            val task = Task(
                args.currentTask.id, newDate, mTitle, mDescription, lastUpdate, getCompletion(newDate)
            )
            mTaskViewModel.updateTask(task)
            dismiss()
        } else if (this::date.isInitialized and !this::time.isInitialized) {
            val newDate = date + " " + getOldTime()
            val task = Task(
                args.currentTask.id, newDate, mTitle, mDescription, lastUpdate, getCompletion(newDate)
            )
            mTaskViewModel.updateTask(task)
            dismiss()
        } else if (this::time.isInitialized and !this::date.isInitialized) {
            val newDate = getOldDate() + " " + time
            val task = Task(
                args.currentTask.id, newDate, mTitle, mDescription, lastUpdate, getCompletion(newDate)
            )
            mTaskViewModel.updateTask(task)
            dismiss()
        } else {
            val task = Task(
                args.currentTask.id,
                args.currentTask.date,
                mTitle,
                mDescription,
                lastUpdate,
                getCompletion(args.currentTask.date)
            )
            mTaskViewModel.updateTask(task)
            dismiss()
        }
    }

    private fun getCompletion(date : String): Boolean {
            return (System.currentTimeMillis() > (DateFormatter.hourly().parse(date) as Date).time)
    }


}