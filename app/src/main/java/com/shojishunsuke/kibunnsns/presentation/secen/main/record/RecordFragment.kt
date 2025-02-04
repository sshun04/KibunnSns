package com.shojishunsuke.kibunnsns.presentation.secen.main.record

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.shojishunsuke.kibunnsns.GlideApp
import com.shojishunsuke.kibunnsns.R
import com.shojishunsuke.kibunnsns.presentation.recycler_view.adapter.PagerAdapter
import com.shojishunsuke.kibunnsns.presentation.secen.setting.SettingActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_record.view.*

class RecordFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_VIEW = 1
        private const val RESULT_OK = -1
    }

    private lateinit var viewModel: RecordFragmentViewModel
    private lateinit var iconView: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)

        viewModel = requireActivity().run {
            ViewModelProviders.of(this).get(RecordFragmentViewModel::class.java)
        }

        iconView = view.acIcon
        val nameTextView = view.nameTextView
        val editNameIcon = view.editNameIcon
        val editImageIcon = view.editImageButton
        val settingIcon = view.settingIcon
        val viewPager = view.viewPager
        val tabLayout = view.tabLayout

        settingIcon.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        editImageIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setDataAndType(
                Uri.parse(Environment.getExternalStorageDirectory().path),
                "image/*"
            )
            startActivityForResult(intent, REQUEST_CODE_VIEW)
        }

        viewModel.userName.observe(this, Observer { userName ->
            nameTextView.text = userName
        })

        editNameIcon.setOnClickListener {
            setUpEditNameDialog(inflater)
        }

        val tabTitleList = listOf(
            resources.getString(R.string.page_my_post_title),
            resources.getString(R.string.page_calendar_title),
            resources.getString(R.string.page_chart_title)
        )

        tabLayout.apply {
            tabTitleList.forEach { title ->
                this.addTab(newTab().setText(title))
            }
        }
        viewPager.apply {
            adapter = PagerAdapter(childFragmentManager, tabTitleList)
        }

        tabLayout.setupWithViewPager(view.viewPager)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_VIEW) {
            val uri = data?.data ?: throw IllegalArgumentException()
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            viewModel.saveUserIcon(bitmap)
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.toast_error_get_photo),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.currentBitmap != null) {
            Glide.with(requireContext())
                .load(viewModel.currentBitmap)
                .placeholder(R.drawable.iconmonstr_user_20_96)
                .error(R.drawable.iconmonstr_user_20_96)
                .into(iconView)
        } else {
            GlideApp.with(requireContext())
                .load(viewModel.getUserIconUrl())
                .placeholder(R.drawable.iconmonstr_user_20_96)
                .error(R.drawable.iconmonstr_user_20_96)
                .into(iconView)
        }
    }

    private fun setUpEditNameDialog(inflater: LayoutInflater) {
        val parentView = inflater.inflate(R.layout.fragment_dialog_edit_name, null)
        val editText = parentView.findViewById<EditText>(R.id.editNickNameEditText)

        editText.setText(viewModel.userName.value)

        val editDialog = AlertDialog.Builder(requireContext())
            .setPositiveButton(
                resources.getString(R.string.button_dialog_positive),
                DialogInterface.OnClickListener { _, _ ->
                    viewModel.saveUserName(editText.text.toString())
                })
            .setNegativeButton(resources.getString(R.string.button_cancel), null)
            .create()

        editDialog.setView(parentView)
        editDialog.show()
    }
}