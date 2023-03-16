package com.z100.geopal.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.z100.geopal.databinding.FragmentSettingsBinding
import com.z100.geopal.service.TestDataService

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

        binding.swTestMode.setOnCheckedChangeListener {
            _, isChecked ->
            if (isChecked) testDataService.addTestRemindersToDB()
            else testDataService.removeTestRemindersFromDB()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
