package com.alvaroquintana.adivinacapitales.ui.ranking

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.common.inflate
import com.alvaroquintana.domain.User

class RankingListAdapter(var rankingList: MutableList<User>,
) : RecyclerView.Adapter<RankingListAdapter.RankingListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingListViewHolder {
        val view = parent.inflate(R.layout.item_ranking_user, false)
        return RankingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingListViewHolder, position: Int) {
        val user = rankingList[position]
        holder.userName.text = user.name
        holder.userPoints.text = user.points
    }

    override fun getItemCount(): Int {
        return rankingList.size
    }

    fun getItem(position: Int): User {
        return rankingList[position]
    }

    class RankingListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var userName: TextView = view.findViewById(R.id.userName)
        var userPoints: TextView = view.findViewById(R.id.userPoints)
    }
}