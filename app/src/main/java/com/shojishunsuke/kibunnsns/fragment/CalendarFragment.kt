package com.shojishunsuke.kibunnsns.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shojishunsuke.kibunnsns.R
import com.shojishunsuke.kibunnsns.adapter.PostRecordRecyclerViewAdapter
import com.shojishunsuke.kibunnsns.clean_arc.presentation.CalendarActivityViewModel
import kotlinx.android.synthetic.main.fragment_calendar.view.*

class CalendarFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val viewModel = requireActivity().run { ViewModelProviders.of(this).get(CalendarActivityViewModel::class.java) }

        view.calendarView.apply {
            setOnDateChangeListener { _, year, month, date ->
                viewModel.setDate(year, month, date)
            }
        }

        view.focusTodayButton.setOnClickListener {
            viewModel.onFocusToday()
            view.calendarView.date = viewModel.getDateInLong()
        }

        view.datePostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        viewModel.dateText.observe(viewLifecycleOwner, Observer {
            view.simpleDateTextView.text = it
        })
        viewModel.postsOfDate.observe(viewLifecycleOwner, Observer {
            view.datePostsRecyclerView.adapter =
                PostRecordRecyclerViewAdapter(requireContext()).apply {
                    addNextCollection(it)
                }
        })
        return view
    }
}