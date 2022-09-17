package com.example.superbtodo.adapters


import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
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
import com.example.superbtodo.utils.TaskDiffUtil
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*


class ListAdapter(
    val callBack: (Task) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>(

) {

    private var tasks = mutableListOf<Task>()
    private var handler: Handler? = null
    private val hourly = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.US)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.titleTxt) as TextView
        val timeTextView = itemView.findViewById(R.id.timeTxt) as TextView
        val timeLeftTextView = itemView.findViewById(R.id.timeLeftTxt) as TextView
        val isDoneCheckBox = itemView.findViewById(R.id.checkBtn) as CheckBox
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
        holder.taskLayout.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fall_down)
        )
        initHolder(holder, position)
        timerUpdate(holder, position)
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
        holder.isDoneCheckBox.setOnClickListener {
            currentItem.isDone = true
            sendData(currentItem)
//            notifyItemChanged(position)
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

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.taskLayout.clearAnimation()
    }

    //    @SuppressLint("NotifyDataSetChanged")
    fun setData(newTasks: MutableList<Task>) {
        val diffUtil = TaskDiffUtil(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }


    fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    private fun initHolder(holder: ListAdapter.ViewHolder, position: Int) {
        val currentItem = tasks[position]
        holder.titleTextView.text = currentItem.title
        holder.timeTextView.text = currentItem.date
        holder.isDoneCheckBox.isChecked = currentItem.isDone
        holder.lastUpdateTextView.text = currentItem.lastUpdate
        try {
            val date = hourly.parse(currentItem.date)
            val outputDateString = dateFormat.format(date as Date)
            val textItems: ArrayList<String> = outputDateString.split(" ") as ArrayList<String>
            holder.day.text = textItems[0]
            holder.date.text = textItems[1]
            holder.month.text = textItems[2]

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun timerUpdate(holder: ViewHolder, position: Int) {
        handler = Handler(Looper.getMainLooper())
        var periodicUpdate: Runnable? = null
        periodicUpdate = Runnable {
            try {
                holder.timeLeftTextView.text = getTimeLeft(
                    hourly.format(System.currentTimeMillis()),
                    hourly.parse(holder.timeTextView.text.toString()) as Date
                )
                tasks[position].timeLeft = holder.timeLeftTextView.text.toString()
                if (tasks[position].date == hourly.format(System.currentTimeMillis()) && !tasks[position].isDone) {
                    tasks[position].isDone = true
                    sendData(tasks[position])
                }
                if (tasks[position].timeLeft.contains("-") && !tasks[position].isDone) {
                    tasks[position].isDone = true
                    sendData(tasks[position])
                }
                periodicUpdate?.let { handler?.postDelayed(it, 1000) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        handler?.post(periodicUpdate)
    }


    private fun getTimeLeft(timeNow: String, timeEnd: Date): String {
        val dob = hourly.parse(timeNow)
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
        callBack(task)
    }


}
