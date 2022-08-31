package com.example.superbtodo.fragments.add

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.viewmodel.TaskViewModel
import com.example.superbtodo.databinding.FragmentAddingBinding
import java.text.SimpleDateFormat
import java.util.*

class AddingFragment : Fragment(R.layout.fragment_adding),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentAddingBinding
    private lateinit var mTaskViewModel: TaskViewModel
    //date and time variables
    private var day =0
    private var month =0
    private var year =0
    private var hour =0
    private var minute =0

    private var savedDay =0
    private var savedMonth=0
    private var savedYear =0
    private var savedHour =0
    private var savedMinute =0
    private lateinit var date :String
    //task variables




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAddingBinding.bind(view)
        mTaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        hideDateStuff()
        pickDate()
        binding.addBtn.setOnClickListener{
            addNewTask()
        }
    }

    private fun hideDateStuff() {
        binding.dateTxt.visibility=View.GONE
        binding.specificTimeTxt.visibility=View.GONE
    }

    private fun addNewTask() {
        val mContent= binding.TitleEditTxt.text.toString()
        if (mContent.isEmpty()  ){
            Toast.makeText(context,"Task description must not be empty!",Toast.LENGTH_LONG).show()
        }
        else if(!this::date.isInitialized){
            Toast.makeText(context,"You did not pick the date and time!",Toast.LENGTH_LONG).show()
        }
        else{
            val mDate = date
            val mTimeLeft = getTimeLeft().toString()
            val isDone = mTimeLeft.contains("-")
            val task = Task(0,mDate,mContent,mTimeLeft,isDone)
            mTaskViewModel.addTask(task)
            findNavController().navigate(R.id.action_addingFragment_to_listFragment)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeLeft(): String {
        val today = Date()
        val dobs = date
        val sdf= SimpleDateFormat("dd/MM/yyyy HH:mm")
        val dob = sdf.parse(dobs)
        val days = -(today.time- dob!!.time)/86400000
        val hours = -(today.time-dob.time)%86400000/3600000
        val minutes = -(today.time - dob.time)%86400000%3600000/60000
        return "$days days $hours hours $minutes minutes left"


    }

    private fun pickDate() {
        binding.datePickerBtn.setOnClickListener{
            getDateTimeCalendar()
            DatePickerDialog(requireContext(),this,year,month,day).show()
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
        date ="$savedDay/${savedMonth+1}/$savedYear"
        TimePickerDialog(requireContext(),this,hour,minute,true).show()
    }

    override fun onTimeSet(viewe: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute=minute
        date+=" $savedHour:$savedMinute"
        binding.apply {
            dateTxt.text=date
            specificTimeTxt.text=getTimeLeft()
            dateTxt.visibility=View.VISIBLE
            specificTimeTxt.visibility=View.VISIBLE
        }

    }
}