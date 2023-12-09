package app.mp.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.mp.common.util.media.AudioPlayerState
import app.mp.common.util.network.ResponseResult
import app.mp.model.mapper.toModel
import app.mp.model.model.Track
import app.mp.model.repo.def.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val audioPlayerState: AudioPlayerState,
) : ViewModel() {

    private val _trackList = MutableLiveData<List<Track>>(emptyList())
    val trackList : LiveData<List<Track>> get() = _trackList


    val playerState = audioPlayerState.playerState.asLiveData()
    val currentTrackState = audioPlayerState.currentTrackState.asLiveData()

    fun getTrack(trackId: Int = 680316) {
        viewModelScope.launch {
            audioRepository.getTrackFromId(trackId).collect {
                when (it) {
                    is ResponseResult.Failed -> Unit
                    is ResponseResult.Success -> {
                        val track = it.data!!.toModel()
                        audioPlayerState.currentTrackState.update { info -> info.copy(name = track.name) }
                        _trackList.value = _trackList.value!!.plus(track)
                    }

                    is ResponseResult.Unknown -> Unit
                }
            }
        }
    }

    fun getSimilarTrack(trackId: Int = 680316) {
        viewModelScope.launch {
            audioRepository.getSimilarTracks(trackId).collect {
                when (it) {
                    is ResponseResult.Failed -> Unit
                    is ResponseResult.Success -> {
                        val trackList = it.data!!.toModel()
                        _trackList.value = _trackList.value!!.plus(trackList)
                    }

                    is ResponseResult.Unknown -> Unit
                }
            }
        }
    }


}