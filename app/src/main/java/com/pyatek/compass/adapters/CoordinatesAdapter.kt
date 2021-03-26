package com.pyatek.compass.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.pyatek.compass.BR
import com.pyatek.compass.R
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity
import com.pyatek.compass.helpers.DateUtil
import com.pyatek.compass.helpers.DateUtil.DATE_DD_MM_YYYY_HH_MM
import kotlinx.android.synthetic.main.item_coordinates.view.*

interface ICoordinatesListener {
    fun onMapClick(id: Int)
    fun onSelected(coordinates: HistoryCoordinatesEntity)
}

class CoordinatesAdapter(
    var dataList: List<HistoryCoordinatesEntity>,
    private val listener: ICoordinatesListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_coordinates,
            parent,
            false
        )

        return HomeMenuViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homeHolder = (holder as HomeMenuViewHolder)
        homeHolder.binding.setVariable(BR.jm, dataList[position])
        homeHolder.binding.executePendingBindings()
        homeHolder.bind(dataList[position], listener)
    }

    class HomeMenuViewHolder(rowView: View, parentContext: Context) : RecyclerView.ViewHolder(rowView) {
        val view: View = rowView
        val binding: ViewDataBinding = DataBindingUtil.bind(rowView)!!
        val context: Context = parentContext

        fun bind(
            item: HistoryCoordinatesEntity,
            listener: ICoordinatesListener
        ) {
            view.coordinatesDateTextView.text = DateUtil.getFormattedDateFromTimestamp(item.timestamp, DATE_DD_MM_YYYY_HH_MM)
            view.coordinatesSelectImageButton.setOnClickListener {
                listener.onSelected(item)
            }

            view.coordinatesShowMapImageButton.setOnClickListener {
                listener.onMapClick(item.id)
            }
        }
    }
}