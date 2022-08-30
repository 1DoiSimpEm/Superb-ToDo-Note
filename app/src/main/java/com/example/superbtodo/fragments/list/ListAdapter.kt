package com.example.superbtodo.fragments.list



import android.annotation.SuppressLint
import android.graphics.Paint
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task
import com.example.superbtodo.viewmodel.TaskViewModel

class ListAdapter() : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var tasks = emptyList<Task>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val contentTextView = itemView.findViewById(R.id.contentTxt) as TextView
        val timeTextView = itemView.findViewById(R.id.timeTxt) as TextView
        val timeLeftTextView = itemView.findViewById(R.id.timeLeftTxt) as TextView
        val isDoneCheckBox = itemView.findViewById(R.id.checkBtn) as RadioButton
        val taskLayout = itemView.findViewById(R.id.taskLayout) as RelativeLayout
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_layout,parent,false)
        return ViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem =  tasks[position]
        holder.contentTextView.text=currentItem.content
        holder.timeTextView.text=currentItem.date
        holder.timeLeftTextView.text=currentItem.timeLeft
        holder.isDoneCheckBox.isChecked=currentItem.isDone

        holder.taskLayout.setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToUpdateTaskDialogFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        if (holder.isDoneCheckBox.isChecked){
            holder.timeLeftTextView.visibility = View.GONE
            holder.contentTextView.apply{
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTextColor(R.color.gone)
            }
            holder.timeTextView.apply{
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTextColor(R.color.gone)
            }
        }
        else{
            holder.timeLeftTextView.visibility = View.VISIBLE
            holder.contentTextView.apply{
                paintFlags = 0
                setTextColor(R.color.black)
            }
            holder.timeTextView.apply{
                paintFlags = 0
                setTextColor(R.color.black)
            }
        }

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(task : List<Task>){
        this.tasks=task
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int):Task{
        return tasks[position]
    }


}
