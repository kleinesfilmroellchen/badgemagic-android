package org.fossasia.badgemagic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.fossasia.badgemagic.adapter.SavedClipartsAdapter
import org.fossasia.badgemagic.data.SavedClipart
import org.fossasia.badgemagic.database.ClipArtService

class SavedClipartViewModel(
    val clipArtService: ClipArtService
) : ViewModel() {

    var cliparts = listOf<SavedClipart>()
    var adapter = SavedClipartsAdapter(cliparts, this)
    var loadButton: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadButton.value = false
    }

    fun getStorageClipartLiveData() = clipArtService.getClipsFromStorage()

    fun deleteClipart(position: Int) {
        if (cliparts.isNotEmpty() && position < cliparts.size)
            clipArtService.deleteClipart(cliparts[position].fileName)
    }

    fun loadClipart() {
        loadButton.value = true
    }

    fun setList(list: List<SavedClipart>) {
        cliparts = list
    }

    fun isEmpty() = cliparts.isEmpty()
}
