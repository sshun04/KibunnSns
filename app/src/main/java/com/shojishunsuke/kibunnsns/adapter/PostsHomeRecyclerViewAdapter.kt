package com.shojishunsuke.kibunnsns.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.emoji.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.shojishunsuke.kibunnsns.GlideApp
import com.shojishunsuke.kibunnsns.R
import com.shojishunsuke.kibunnsns.clean_arc.presentation.PostsFragmentsViewModel
import com.shojishunsuke.kibunnsns.model.Post
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class PostsHomeRecyclerViewAdapter(
    private val context: Context,
    private val fragmentViewModel: PostsFragmentsViewModel,
    private var postsList: List<Post>,
    private val isHome: Boolean,
    private val listener: (Post) -> Unit
    ) :
    RecyclerView.Adapter<PostsHomeRecyclerViewAdapter.PostsRecyclerViewHolder>() {


    override fun onBindViewHolder(holder: PostsRecyclerViewHolder, position: Int) {
        val post = postsList[position]
        holder.userNameTextView.text = if (post.userName.isNotBlank()) post.userName else "匿名"
        holder.contentTextView.text = post.contentText
        holder.sentiScoreTextView.text = post.sentiScore.toString()
        holder.dateTextView.text = formatDate(post.date)
        holder.activityIcon.text =
            if (post.actID.isNotBlank()) post.actID else fragmentViewModel.getAppropriateIcon(post.sentiScore)

        if (post.iconPhotoLink.isNotBlank()) {
            GlideApp.with(context)
                .load(fragmentViewModel.getIconRef(post.iconPhotoLink))
                .into(holder.userIcon)
        } else {
            holder.userIcon.setImageResource(R.color.colorPrimary)
        }

        holder.cardView.setOnClickListener {
            fragmentViewModel.onPostSelected(it, post, isHome)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsRecyclerViewHolder {
        val inflater = LayoutInflater.from(context)
        val layoutResource = R.layout.item_post
        val mView = inflater.inflate(layoutResource, parent, false)
        return PostsRecyclerViewHolder(mView)
    }

    override fun getItemCount(): Int = postsList.size

    inner class PostsRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<LinearLayout>(R.id.postBaseView)
        val userNameTextView = view.findViewById<TextView>(R.id.userName)
        val userIcon = view.findViewById<CircleImageView>(R.id.userIcon)
        val contentTextView = view.findViewById<TextView>(R.id.contentTextView)
        val sentiScoreTextView = view.findViewById<TextView>(R.id.sentiScoreTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        val activityIcon = view.findViewById<EmojiTextView>(R.id.activityIcon)
    }


    private fun formatDate(postedDate: Date): String {
        val currentDate = Date()
        val timeDiffInSec = (currentDate.time - postedDate.time) / 1000

        val hourDiff = timeDiffInSec / 3600
        val minuteDiff = (timeDiffInSec % 3600) / 60
        val secDiff = timeDiffInSec % 60

        val outPutText = when {
            timeDiffInSec in 3600 * 24 until 3600 * 48 -> {
                "昨日"
            }
            timeDiffInSec in 3600 until 3600 * 24 -> {
                "$hourDiff" + "時間前"
            }
            timeDiffInSec in 360 until 3600 -> {
                "$minuteDiff" + "分前"
            }
            timeDiffInSec < 360 -> {
                "$secDiff" + "秒前"
            }
            else -> {
                val formatter = SimpleDateFormat("MM月dd日", Locale.JAPAN)
                formatter.format(postedDate)
            }

        }
        return outPutText

    }
}