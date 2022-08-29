package com.example.superbtodo.fragments.list



import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.superbtodo.R
import com.example.superbtodo.data.Task

class ListAdapter() : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var tasks = emptyList<Task>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val contentTextView = itemView.findViewById(R.id.contentTxt) as TextView
        val timeTextView = itemView.findViewById(R.id.timeTxt) as TextView
        val timeLeftTextView = itemView.findViewById(R.id.timeLeftTxt) as TextView
        val isDoneCheckBox = itemView.findViewById(R.id.isDoneCheckBox) as CheckBox
        val taskLayout = itemView.findViewById(R.id.taskLayout) as ConstraintLayout
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_layout,parent,false)
        return ViewHolder(itemView)
    }

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
