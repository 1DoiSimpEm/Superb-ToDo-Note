package com.example.superbtodo.adapters


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.fragments.bins.TrashBinFragmentDirections
import com.example.superbtodo.fragments.list.ListFragmentDirections
import com.example.superbtodo.utils.DateFormatUtil
import com.example.superbtodo.utils.TaskDiffUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import java.util.*


class ListAdapter(
    val onCLick: (Task) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var tasks = mutableListOf<Task>()

    private object DateFormatter : DateFormatUtil()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.titleTxt) as TextView
        val timeTextView = itemView.findViewById(R.id.timeTxt) as TextView
        val timeLeftTextView = itemView.findViewById(R.id.timeLeftTxt) as TextView
        val isDoneCheckBox = itemView.findViewById(R.id.checkBtn) as MaterialCheckBox
        val taskLayout = itemView.findViewById(R.id.taskLayout) as MaterialCardView
        val lastUpdateTextView = itemView.findViewById(R.id.lastUpdate) as TextView
        val alarmImageView = itemView.findViewById(R.id.imgAlarm) as ImageView
        val day = itemView.findViewById(R.id.day) as TextView
        val date = itemView.findViewById(R.id.date) as TextView
        val month = itemView.findViewById(R.id.month) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        initHolder(holder, position)
        holderNavigate(holder, position)
        holderCheckHandle(holder, position)

    }

    private fun holderNavigate(holder: ListAdapter.ViewHolder, position: Int) {
        val currentItem = tasks[position]
        holder.taskLayout.setOnClickListener {
            if (currentItem.isDone) {
                val action =
                    TrashBinFragmentDirections.actionDoneFragmentToUpdateTaskDialogFragment(
                        currentItem
                    )
                holder.itemView.findNavController().navigate(action)
            } else {
                val action =
                    ListFragmentDirections.actionListFragmentToUpdateTaskDialogFragment(
                        currentItem
                    )
                holder.itemView.findNavController().navigate(action)
            }

        }
    }

    private fun holderCheckHandle(holder: ListAdapter.ViewHolder, position: Int) {
        val currentItem = tasks[position]
        holder.isDoneCheckBox.setOnClickListener { view ->
            if ((view as CompoundButton).isChecked) {
                currentItem.isDone = true
                sendData(currentItem)
            }
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


    fun setData(newTasks: MutableList<Task>) {
        val diffUtil = TaskDiffUtil(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
    }


    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    private fun initHolder(holder: ListAdapter.ViewHolder, position: Int) {
        val currentItem = tasks[position]
        holder.titleTextView.text = currentItem.title
        holder.timeTextView.text = DateFormatter.timeFormat()
            .format(DateFormatter.hourly().parse(currentItem.date) as Date)
        holder.isDoneCheckBox.isChecked = currentItem.isDone
        holder.lastUpdateTextView.text = currentItem.lastUpdate
        holder.timeLeftTextView.text = holder.itemView.context.getString(R.string.on_progress)
        if (System.currentTimeMillis() > DateFormatter.hourly().parse(currentItem.date)!!.time)
            holder.timeLeftTextView.text = holder.itemView.context.getString(R.string.lazy)

        try {
            val date = DateFormatter.hourly().parse(currentItem.date)
            val outputDateString = DateFormatter.dateFormatWithChar().format(date as Date)
            val textItems: ArrayList<String> = outputDateString.split(" ") as ArrayList<String>
            holder.day.text = textItems[0]
            holder.date.text = textItems[1]
            holder.month.text = textItems[2]

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun normalizeText(holder: ViewHolder) {
        holder.timeLeftTextView.visibility = View.VISIBLE
        holder.titleTextView.apply {
            paintFlags = 0
        }
        holder.timeTextView.apply {
            paintFlags = 0
        }
        holder.alarmImageView.isVisible = true
    }


    private fun strikeThroughText(holder: ViewHolder) {
        holder.timeLeftTextView.visibility = View.GONE
        holder.titleTextView.apply {
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
        holder.timeTextView.apply {
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
        holder.alarmImageView.isVisible = false
    }

    private fun sendData(task: Task) {
        onCLick(task)
    }


}