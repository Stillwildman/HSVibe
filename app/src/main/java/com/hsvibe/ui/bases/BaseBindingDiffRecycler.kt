package com.hsvibe.ui.bases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Vincent on 2022/4/19.
 */
abstract class BaseBindingDiffRecycler<DataType: Any, T : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<DataType>) :
    ListAdapter<DataType, RecyclerView.ViewHolder>(diffCallback) {

    protected val TAG = javaClass.simpleName

    protected abstract fun getLayoutId(): Int

    protected abstract fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: T, position: Int)

    protected abstract fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: T, position: Int, payload: Any?)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: T = DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false)
        return BindingViewHolder(binding)
    }

    private inner class BindingViewHolder(val bindingView: T) : RecyclerView.ViewHolder(bindingView.root)

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindingViewHolder(holder, (holder as BaseBindingDiffRecycler<*, *>.BindingViewHolder).bindingView as T, position)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any?>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }
        else {
            onBindingViewHolder(holder, (holder as BaseBindingDiffRecycler<*, *>.BindingViewHolder).bindingView as T, position, payloads[0])
        }
    }

}