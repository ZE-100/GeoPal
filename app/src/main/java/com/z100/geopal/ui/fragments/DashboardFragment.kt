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
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.z100.geopal.R
import com.z100.geopal.adapter.RemindersViewAdapter
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.databinding.FragmentDashboardBinding
import com.z100.geopal.pojo.Location
import com.z100.geopal.pojo.NominatimLocation
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.service.ApiRequestService
import org.json.JSONArray
import org.json.JSONObject

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiRequestService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiRequestService()

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

        val etLocation: EditText = dialog.findViewById(R.id.et_reminder_location)
        val btnCancel: TextView = dialog.findViewById(R.id.btn_reminder_cancel)
        val btnSave: Button = dialog.findViewById(R.id.btn_reminder_save)
        val sprDropDown: Spinner = dialog.findViewById(R.id.spr_drop_down)

        etLocation.doOnTextChanged { location, _, _, _ -> getLocations(sprDropDown, dialog, location.toString()) }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            saveReminder(dialog)
            dialog.dismiss()
        }

        dialog.show()

        dialog.window!!.setLayout(MATCH_PARENT, WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.create_reminder_dialog_anim
        dialog.window!!.setGravity(BOTTOM)
    }

    private fun setupRecyclerView() {
        val adapter = RemindersViewAdapter(requireContext(),
            ReminderDBHelper(requireContext()).findAllReminders())

        binding.rvRemindersList.setHasFixedSize(false)
        binding.rvRemindersList.adapter = adapter

        adapter.notifyDataSetChanged()
    }

    private fun getLocations(spinner: Spinner, dialog: Dialog, location: String) {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val dropDownItems: MutableList<NominatimLocation> = mutableListOf()
        apiService.getLocationSearchResults(location) { res, _ ->
            val jsonArray = JSONArray(res)
            for (i in 0 until jsonArray.length()) {
                dropDownItems.add(createNominationFromJson(jsonArray.getJSONObject(i)))
                adapter.add(jsonArray.getJSONObject(i).getString("display_name"))
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, l: Long) {
                dialog.findViewById<TextView>(R.id.et_reminder_location).text = dropDownItems[position].displayName
                dialog.findViewById<TextView>(R.id.tv_reminder_location_lat).text = dropDownItems[position].lat.toString()
                dialog.findViewById<TextView>(R.id.tv_reminder_location_lon).text = dropDownItems[position].lon.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun saveReminder(dialog: Dialog) {
        val location = Location(
            dialog.findViewById<EditText>(R.id.et_reminder_location).text.toString(),
            dialog.findViewById<TextView>(R.id.tv_reminder_location_lat).text.toString().toDouble(),
            dialog.findViewById<TextView>(R.id.tv_reminder_location_lon).text.toString().toDouble()
        )

        val reminder = Reminder(
            null, dialog.findViewById<EditText>(R.id.et_reminder_description).text.toString(),
            location, null
        )

        ReminderDBHelper(requireContext()).insertReminder(reminder)
        setupRecyclerView()
    }

    private fun createNominationFromJson(json: JSONObject): NominatimLocation {
        return NominatimLocation(
            json.getString("place_id").toLong(),
            json.getString("display_name"),
            json.getString("lat").toDouble(),
            json.getString("lon").toDouble()
        )
    }
}
