package org.fossasia.badgemagic.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.fossasia.badgemagic.R
import org.fossasia.badgemagic.core.android.log.Timber
import org.fossasia.badgemagic.data.SavedClipart
import org.fossasia.badgemagic.databinding.FragmentSavedClipartsBinding
import org.fossasia.badgemagic.ui.base.BaseFragment
import org.fossasia.badgemagic.util.ImageUtils
import org.fossasia.badgemagic.util.StorageUtils
import org.fossasia.badgemagic.viewmodels.SavedClipartViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SavedClipartFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            SavedClipartFragment()
    }

    private val viewModel by viewModel<SavedClipartViewModel>()
    private val storageUtils: StorageUtils by inject()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri == null) {
            Timber.tag("PhotoPicker").d("User aborted image selection")
            return@registerForActivityResult
        }
        Timber.tag("PhotoPicker").d("Selected URI: $uri")
        val newImage = storageUtils.importClipArt(uri)
        if (newImage) {
            Toast.makeText(requireContext(), R.string.clipart_import_success, Toast.LENGTH_LONG).show()
            viewModel.clipArtService.updateClipArts()
        } else
            Toast.makeText(requireContext(), R.string.clipart_import_error, Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSavedClipartsBinding>(inflater, R.layout.fragment_saved_cliparts, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getStorageClipartLiveData().observe(
            viewLifecycleOwner,
            Observer { list ->
                viewModel.adapter.setList(
                    list.map { SavedClipart(it.key, ImageUtils.convertToBitmap(it.value)) }
                )
            }
        )

        viewModel.loadButton.observe(this) {
            if (it) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}
