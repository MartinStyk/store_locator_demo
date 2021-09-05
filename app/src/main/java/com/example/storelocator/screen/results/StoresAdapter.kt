package com.example.storelocator.screen.results

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.storelocator.databinding.ItemStoreBinding
import com.example.storelocator.model.store.Store
import com.example.storelocator.util.SignalingLiveData
import javax.inject.Inject

class StoresAdapter @Inject constructor() : RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {

    var stores: List<Store> = emptyList()
        set(value) {
            val old = field
            field = value
            DiffUtil.calculateDiff(StoreDiffCallback(value, old)).dispatchUpdatesTo(this)
        }

    private val showDetailSignal = SignalingLiveData<Store>()
    val showDetail: LiveData<Store> = showDetailSignal

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(stores[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresAdapter.StoreViewHolder {
        return StoreViewHolder(ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = stores.size

    inner class StoreViewHolder(private val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root) {

        inner class StoreViewModel(val store: Store) {
            fun showDetail() = showDetailSignal.postValue(store)
        }

        fun bind(store: Store) {
            binding.viewModel = StoreViewModel(store)
        }
    }

    private class StoreDiffCallback(private val newList: List<Store>,
                                  private val oldList: List<Store>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]
    }

}