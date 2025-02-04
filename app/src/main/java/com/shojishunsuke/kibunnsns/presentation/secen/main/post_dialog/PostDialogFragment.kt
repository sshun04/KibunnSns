package com.shojishunsuke.kibunnsns.presentation.secen.main.post_dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.shojishunsuke.kibunnsns.R
import com.shojishunsuke.kibunnsns.presentation.recycler_view.adapter.EmojiRecyclerViewAdapter
import com.shojishunsuke.kibunnsns.presentation.secen.main.home.detail.DetailPostsFragment
import kotlinx.android.synthetic.main.dialog_pop.view.*
import kotlinx.android.synthetic.main.fragment_dialog_post.view.*

class PostDialogFragment : DialogFragment() {
    private var selectedEmojiCode: String = ""
    private var posted = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val parentView =
            requireActivity().layoutInflater.inflate(R.layout.fragment_dialog_post, null)

        val viewModel = this.run {
            ViewModelProviders.of(
                this,
                PostDialogViewModel.PostDialogViewModelFactory(requireActivity().application)
            )
                .get(PostDialogViewModel::class.java)
        }

        val editText = parentView.contentEditText

        val dialog = AlertDialog.Builder(requireContext())
            .setView(parentView)
            .create()

        viewModel.currentPosted.observe(this, Observer { post ->
            DetailPostsFragment.setupFragment(post, requireFragmentManager())
            dismiss()
        })

        parentView.detailDateTextView.text = DateUtils.formatDateTime(
            requireContext(), System.currentTimeMillis(),
            DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_WEEKDAY
        )
        parentView.timeTextView.text = viewModel.timeString

        parentView.setActivityButton.setOnClickListener {
            val emojiDialog = AlertDialog.Builder(requireContext(), R.style.AppTheme_DialogTheme)
                .create()
            val emojiParentView = emojiDialog.layoutInflater.inflate(R.layout.dialog_pop, null)
            emojiParentView.emojiRecyclerView.apply {
                adapter = EmojiRecyclerViewAdapter(
                    requireContext(),
                    viewModel.requestWholeEmoji()
                ) { emojiCode ->
                    selectedEmojiCode = emojiCode
                    parentView.selectedEmojiTextView.text = selectedEmojiCode
                    parentView.selectEmojiBox.visibility = View.VISIBLE
                    parentView.setActivityButton.visibility = View.GONE
                    emojiDialog.dismiss()
                }
                layoutManager = GridLayoutManager(requireContext(), 7)
            }

            emojiDialog.setView(emojiParentView)
            emojiDialog.show()
        }

        parentView.selectEmojiBox.setOnClickListener {
            val emojiDialog = AlertDialog.Builder(requireContext(), R.style.AppTheme_DialogTheme)
                .create()
            val emojiParentView = emojiDialog.layoutInflater.inflate(R.layout.dialog_pop, null)
            emojiParentView.emojiRecyclerView.apply {
                adapter = EmojiRecyclerViewAdapter(
                    requireContext(),
                    viewModel.requestWholeEmoji()
                ) { emojiCode ->
                    selectedEmojiCode = emojiCode
                    parentView.selectedEmojiTextView.text = selectedEmojiCode
                    emojiDialog.dismiss()
                }
                layoutManager = GridLayoutManager(requireContext(), 7)
            }

            emojiDialog.setView(emojiParentView)
            emojiDialog.show()
        }

        parentView.postButton.setOnClickListener {
            val contentText = editText.text.toString()
            if (contentText.isNotBlank() && !posted) {
                viewModel.requestPost(contentText, selectedEmojiCode)
                posted = true
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.toast_enter_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            val keyBoardManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyBoardManager.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.clearFocus()
        }

        parentView.cancelButton.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}