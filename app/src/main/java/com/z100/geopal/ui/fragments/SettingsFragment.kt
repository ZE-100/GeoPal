package com.z100.geopal.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.z100.geopal.activity.MainActivity.Factory.spDataService
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.databinding.FragmentSettingsBinding
import com.z100.geopal.service.data.TestDataService
import com.z100.geopal.util.Globals.Factory.SP_SETTINGS_TEST_MODE_TOGGLE

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var testDataService: TestDataService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        testDataService = TestDataService(requireContext())

        setupBindings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBindings() {
        binding.swTestMode.isChecked = spDataService.get(SP_SETTINGS_TEST_MODE_TOGGLE)

        binding.swTestMode.setOnCheckedChangeListener { _, isChecked ->
            spDataService.run { toggleTestMode() }
            testDataService.apply {
                if (isChecked) addTestRemindersToDB()
                else removeTestRemindersFromDB()
            }
        }

        binding.btnDeleteAll.setOnClickListener {
            ReminderDBHelper(requireContext()).deleteAll()
        }
    }
}
