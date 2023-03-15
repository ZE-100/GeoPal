package com.z100.geopal

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
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

        binding.buttonFirst.setOnClickListener {
            showComplexDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showComplexDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_create_reminder)

        val etDescription = dialog.findViewById<EditText>(R.id.et_reminder_description)
        val etLocation = dialog.findViewById<EditText>(R.id.et_reminder_location)
        val btnCancel = dialog.findViewById<TextView>(R.id.btn_reminder_cancel)
        val btnSave = dialog.findViewById<Button>(R.id.btn_reminder_save)

        etLocation.doOnTextChanged {
            _, _, _, _ -> // TODO: Query for 3 locations
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val description = etDescription.text.toString()
            val location = etLocation.text.toString()
            saveReminder(description, location)
            dialog.dismiss()
        }

        dialog.show()

        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.create_reminder_dialog_anim
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun saveReminder(description: String?, location: String?) {
        Toast.makeText(requireContext(), "${description.toString()} : ${location.toString()}", Toast.LENGTH_SHORT).show()
    }
}
