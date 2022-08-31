package com.example.superbtodo.fragments.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.databinding.FragmentUpdatetaskdialogBinding
import com.example.superbtodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class UpdateTaskDialogFragment : DialogFragment(R.layout.fragment_updatetaskdialog),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val args: UpdateTaskDialogFragmentArgs by navArgs()
    private lateinit var binding: FragmentUpdatetaskdialogBinding
    private lateinit var mTaskViewModel: TaskViewModel

    //date and time variables
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0
    private lateinit var date: String
    //task variables


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUpdatetaskdialogBinding.bind(view)
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        initData()
        pickDate()
        binding.saveBtn.setOnClickListener {
            saveTask()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    private fun initData() {
        binding.TitleEditTxt.setText(args.currentTask.content)
        binding.dateTxt.text = args.currentTask.date
        binding.specificTimeTxt.text = args.currentTask.timeLeft
    }

    private fun saveTask() {
        val mContent = binding.TitleEditTxt.text.toString()
        if (mContent.isEmpty()) {
            Toast.makeText(context, "Task description must not be empty!", Toast.LENGTH_LONG).show()
        } else if (!this::date.isInitialized) {
            val task = Task(args.currentTask.id, args.currentTask.date, mContent, args.currentTask.timeLeft, args.currentTask.isDone)
            mTaskViewModel.updateTask(task)
            dismiss()
        } else {

            val mDate = date
            val mTimeLeft = getTimeLeft()
            val isDone = mTimeLeft.contains("-")
            val task = Task(args.currentTask.id, mDate, mContent, mTimeLeft, isDone)
            mTaskViewModel.updateTask(task)
            dismiss()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeLeft(): String {
        val today = Date()
        val dobs = date
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dob = sdf.parse(dobs)

        val days = -(today.time - dob!!.time) / 86400000
        val hours = -(today.time - dob.time) % 86400000 / 3600000
        val minutes = -(today.time - dob.time) % 86400000 % 3600000 / 60000
        return "$days days $hours hours $minutes minutes left"


    }

    private fun pickDate() {
        binding.datePickerBtn.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        date = "$savedDay/${savedMonth + 1}/$savedYear"
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(viewe: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        date += " $savedHour:$savedMinute"
        binding.apply{
            dateTxt.text = date
            specificTimeTxt.text = getTimeLeft()
        }

    }
}