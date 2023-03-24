package com.z100.geopal.ui.fragments

import android.app.Dialog
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity.BOTTOM
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import com.z100.geopal.R
import com.z100.geopal.MainActivity.Factory.dbHelper
import com.z100.geopal.adapter.RemindersViewAdapter
import com.z100.geopal.databinding.FragmentDashboardBinding
import com.z100.geopal.pojo.Location
import com.z100.geopal.pojo.NominatimLocation
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.service.api.ApiRequestService
import com.z100.geopal.service.api.Callback
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiRequestService: ApiRequestService

    private lateinit var rvRemindersListAdapter: RemindersViewAdapter
    private lateinit var etLocationD: EditText
    private lateinit var btnCancelD: TextView
    private lateinit var btnSaveD: Button
    private lateinit var sprDropDownD: Spinner

    private var nameHolder = ""
    private var latHolder = -1.0
    private var lonHolder = -1.0
    private var searching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiRequestService = ApiRequestService()

        setupRecyclerView()
        setupBindings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        rvRemindersListAdapter = RemindersViewAdapter(requireContext(), dbHelper.findAllReminders())

        binding.rvRemindersList.setHasFixedSize(false)
        binding.rvRemindersList.adapter = rvRemindersListAdapter

        rvRemindersListAdapter.notifyDataSetChanged()
    }

    private fun setupBindings() {
        binding.fab.setOnClickListener {
            showCreateReminderDialog()
        }
    }

    private fun showCreateReminderDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_create_reminder)

        etLocationD = dialog.findViewById(R.id.et_reminder_location)
        btnCancelD = dialog.findViewById(R.id.btn_reminder_cancel)
        btnSaveD = dialog.findViewById(R.id.btn_reminder_save)
        sprDropDownD = dialog.findViewById(R.id.spr_drop_down)

        etLocationD.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            private var timer: Timer = Timer()
            private val DELAY: Long = 1000 // Milliseconds

            override fun afterTextChanged(p0: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread {
                                if (!searching) {
                                    getLocationSuggestions(sprDropDownD, etLocationD.text.toString())
                                }
                                etLocationD.isEnabled = false
                                searching = true
                            }
                        }
                    },
                    DELAY
                )
            }
        })

        btnCancelD.setOnClickListener { dialog.dismiss() }

        btnSaveD.setOnClickListener {
            saveReminder(etLocationD.text.toString())
            dialog.dismiss()
        }

        dialog.show()

        dialog.window!!.setLayout(MATCH_PARENT, WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.create_reminder_dialog_anim
        dialog.window!!.setGravity(BOTTOM)
    }

    private fun getLocationSuggestions(spinner: Spinner, location: String) {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val dropDownItems: MutableList<NominatimLocation> = mutableListOf()
        apiRequestService.getLocationSearchResults(location, Callback<List<NominatimLocation>> { res, _ ->
            res?.forEach {
                dropDownItems.add(it)
                adapter.add(it.display_name)
            } ?: adapter.add("Something went wrong!")
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, l: Long) {
                etLocationD.setText(dropDownItems[pos].display_name)
                nameHolder = dropDownItems[pos].display_name
                latHolder = dropDownItems[pos].lat
                lonHolder = dropDownItems[pos].lon
            }
        }
    }

    private fun saveReminder(description: String) {
        val reminder = Reminder(null, description,
            Location(nameHolder, latHolder, lonHolder), null)

        dbHelper.insertReminder(reminder)
        setupRecyclerView()
    }
}
