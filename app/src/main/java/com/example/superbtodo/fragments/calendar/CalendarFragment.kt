package com.example.superbtodo.fragments.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.superbtodo.R
import com.example.superbtodo.databinding.FragmentCalendarBinding


class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private lateinit var  binding : FragmentCalendarBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentCalendarBinding.bind(view)

    }




}