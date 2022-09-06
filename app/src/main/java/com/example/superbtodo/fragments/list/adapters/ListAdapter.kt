package com.example.superbtodo.fragments.list.adapters


import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.fragments.list.ListFragmentDirections
import java.text.SimpleDateFormat
import java.util.*


class ListAdapter(
    val callBack: (Task) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var tasks = mutableListOf<Task>()
    private var handler: Handler? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.titleTxt) as TextView
        val timeTextView = itemView.findViewById(R.id.timeTxt) as TextView
        val timeLeftTextView = itemView.findViewById(R.id.timeLeftTxt) as TextView
        val isDoneCheckBox = itemView.findViewById(R.id.checkBtn) as RadioButton
        val taskLayout = itemView.findViewById(R.id.taskLayout) as RelativeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = tasks[position]
        holder.titleTextView.text = currentItem.title
        holder.timeTextView.text = currentItem.date
        holder.isDoneCheckBox.isChecked = currentItem.isDone
        timerUpdate(holder, position)
        holder.taskLayout.setOnClickListener {
            Toast.makeText(holder.itemView.context,currentItem.id.toString(),Toast.LENGTH_LONG).show()
            val action =
                ListFragmentDirections.actionListFragmentToUpdateTaskDialogFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        holder.isDoneCheckBox.setOnClickListener {
            currentItem.isDone = true
            sendData(currentItem)
            notifyItemChanged(position)
        }
        if (holder.isDoneCheckBox.isChecked) {
            strikeThroughText(holder)
        } else {
            normalizeText(holder)
        }

    }


    override fun getItemCount(): Int {
        return tasks.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(task: MutableList<Task>) {
        this.tasks = task
        notifyDataSetChanged()
    }


    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    private fun timerUpdate(holder: ViewHolder, position: Int) {
        val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        handler = Handler(Looper.getMainLooper())
        var periodicUpdate: Runnable? = null
        periodicUpdate = Runnable {
            try {
                holder.timeLeftTextView.text = getTimeLeft(
                    hourly.format(System.currentTimeMillis()),
                    hourly.parse(holder.timeTextView.text.toString()) as Date
                )
                tasks[position].timeLeft = holder.timeLeftTextView.text.toString()
                periodicUpdate?.let { handler?.postDelayed(it, 1000) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        handler?.post(periodicUpdate)
    }

    private fun getTimeLeft(timeNow: String, timeEnd: Date): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dob = sdf.parse(timeNow)
        val days = (timeEnd.time - dob!!.time) / 86400000
        val hours = (timeEnd.time - dob.time) % 86400000 / 3600000
        val minutes = (timeEnd.time - dob.time) % 86400000 % 3600000 / 60000
        return "$days days $hours hours $minutes minutes left"
    }

    private fun normalizeText(holder: ViewHolder) {
        holder.timeLeftTextView.visibility = View.VISIBLE
        holder.titleTextView.apply {
            paintFlags = 0
        }
        holder.timeTextView.apply {
            paintFlags = 0
        }
    }


    private fun strikeThroughText(holder: ViewHolder) {
        holder.timeLeftTextView.visibility = View.GONE
        holder.titleTextView.apply {
            paintFlags =  Paint.STRIKE_THRU_TEXT_FLAG
        }
        holder.timeTextView.apply {
            paintFlags =  Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    private fun sendData(task: Task) {
        callBack(task)
    }
}
