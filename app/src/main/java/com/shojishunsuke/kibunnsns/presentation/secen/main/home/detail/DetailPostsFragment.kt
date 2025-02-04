package com.shojishunsuke.kibunnsns.presentation.secen.main.home.detail

import android.os.Bundle
import android.text.format.DateUtils.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.shojishunsuke.kibunnsns.R
import com.shojishunsuke.kibunnsns.domain.model.Post
import com.shojishunsuke.kibunnsns.presentation.recycler_view.adapter.RecyclerViewPagingAdapter
import com.shojishunsuke.kibunnsns.presentation.recycler_view.listener.NestedEndlessScrollListener
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailPostsFragment : Fragment() {

    companion object {
        private const val EXTRA_POST: String = "post"

        private val enterTransitionSet = TransitionSet().apply {
            val slideAnim = Slide().apply {
                slideEdge = Gravity.RIGHT
                duration = 200
            }
            addTransition(slideAnim)
        }

        private val exitTransitionSet = TransitionSet().apply {
            val slideAnim = Slide().apply {
                slideEdge = Gravity.RIGHT
                duration = 400
            }
            addTransition(slideAnim)
        }

        fun setupFragment(post: Post, fragmentManager: FragmentManager) {
            fragmentManager.beginTransaction().also {
                it.add(R.id.detailFragmentContainer, getInstance(post))
                    .setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left)
                    .addToBackStack("detail")
                    .commit()
            }
        }

        private fun getInstance(post: Post): DetailPostsFragment {
            val bundle = Bundle().apply {
                putSerializable(EXTRA_POST, post)
            }
            return DetailPostsFragment().apply {
                arguments = bundle
                enterTransition = enterTransitionSet
                exitTransition = exitTransitionSet
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val post = arguments?.getSerializable(EXTRA_POST) as Post

        val viewModel = this.run {
            ViewModelProviders.of(
                this,
                DetailPostsFragmentViewModel.DetailPostsFragmentViewModelFactory(post)
            )
                .get(DetailPostsFragmentViewModel::class.java)
        }

        view.detailFragmentToolbar.setNavigationOnClickListener {
            for (i in 0 until requireFragmentManager().backStackEntryCount) {
                requireFragmentManager().popBackStack()
            }
        }

        view.selectedUserName.text =
            viewModel.getUserName() ?: resources.getString(R.string.user_name_default)

        Glide.with(requireContext())
            .load(post.iconPhotoLink)
            .error(R.drawable.icon_annonymous)
            .placeholder(R.drawable.icon_annonymous)
            .into(view.selectedUserIcon)

        view.selectedActIcon.text = viewModel.getEmojiCode()
        view.selectedContentText.text = viewModel.getContentText()

        view.selectedDate.text = formatDateTime(
            requireContext(), post.date.time,
            FORMAT_SHOW_YEAR or FORMAT_SHOW_DATE or FORMAT_SHOW_WEEKDAY or FORMAT_SHOW_TIME
        )

        val stagLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        val pagingAdapter = RecyclerViewPagingAdapter(requireContext()) { post ->
            viewModel.onItemClicked(post)
            setupFragment(post, requireFragmentManager())
        }

        val recyclerView = view.detailPostsRecyclerView.apply {
            adapter = pagingAdapter
            layoutManager = stagLayoutManager
            isNestedScrollingEnabled = false
        }

        val endlessScrollListener =
            NestedEndlessScrollListener(stagLayoutManager, recyclerView) {
                viewModel.onScrollBottom()
            }

        view.nestedScrollView.setOnScrollChangeListener(endlessScrollListener)

        viewModel.nextPosts.observe(viewLifecycleOwner, Observer {
            pagingAdapter.addNextCollection(it)
        })

        return view
    }
}