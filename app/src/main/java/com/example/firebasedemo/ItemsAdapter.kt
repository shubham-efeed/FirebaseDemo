package com.example.firebasedemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasedemo.databinding.ItemSkuBinding

class ItemsAdapter(val callback: (newValue: Int, sku: Sku) -> Unit) :
    ListAdapter<Sku, ItemsAdapter.ItemsViewHolder>(
        object : DiffUtil.ItemCallback<Sku>() {
            override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        }
    ) {
    inner class ItemsViewHolder(private val binding: ItemSkuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Sku?) {
            binding.tvSkuName.text = item?.name
//            binding.incDecLactationBtn.setMiddleText(item!!.quantity.toString())
//            binding.incDecLactationBtn.setOnChange {
//                if (item != null) {
//                    callback.invoke(it,item)
//                }
//            }

            item?.quantity?.let {
                binding.tilQuantity.editText?.setText(item.quantity.toString())
            }
            binding.btnInc.setOnClickListener {
                binding.tilQuantity.editText?.setText(
                    binding.tilQuantity.editText?.text.toString().toInt().plus(1).toString()
                )
                if (item != null) {
                    callback.invoke(
                        binding.tilQuantity.editText?.text.toString().toInt(),
                        item
                    )
                }
            }



            binding.btnDec.setOnClickListener {
                if (binding.tilQuantity.editText?.text.toString().toInt() > 0) {
                    binding.tilQuantity.editText?.setText(
                        binding.tilQuantity.editText?.text.toString().toInt().minus(1).toString()
                    )
                    if (item != null) {
                        callback.invoke(
                            binding.tilQuantity.editText?.text.toString().toInt(),
                            item
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val binding =
            ItemSkuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}