package com.z100.geopal.ui.fragments

import android.app.Dialog
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity.BOTTOM
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.z100.geopal.R
import com.z100.geopal.adapter.RemindersViewAdapter
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.fab.setOnClickListener {
            showCreateReminderDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCreateReminderDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_create_reminder)

         val etDescription: EditText = dialog.findViewById(R.id.et_reminder_description)
         val etLocation: EditText = dialog.findViewById(R.id.et_reminder_location)
         val btnCancel: TextView = dialog.findViewById(R.id.btn_reminder_cancel)
         val btnSave: Button = dialog.findViewById(R.id.btn_reminder_save)

        etLocation.doOnTextChanged {
            _, _, _, _ -> // TODO: Query for 3 locations
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            saveReminder(etDescription.text.toString(), etLocation.text.toString())
            dialog.dismiss()
        }

        dialog.show()

        dialog.window!!.setLayout(MATCH_PARENT, WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.create_reminder_dialog_anim
        dialog.window!!.setGravity(BOTTOM)

        etDescription.requestFocus()
    }

    private fun setupRecyclerView() {

        val dbHelper = ReminderDBHelper(requireContext())

        val adapter = RemindersViewAdapter(requireContext(), dbHelper.findAllReminders())
        binding.rvRemindersList.setHasFixedSize(false)
        binding.rvRemindersList.adapter = adapter

        adapter.notifyDataSetChanged()
    }
    private fun saveReminder(description: String?, location: String?) {
        Toast.makeText(requireContext(), "${description.toString()} : ${location.toString()}", Toast.LENGTH_SHORT).show()
    }
}
