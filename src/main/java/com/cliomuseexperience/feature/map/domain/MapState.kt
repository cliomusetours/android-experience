package com.cliomuseexperience.feature.map.domain

import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.Story
import com.cliomuseexperience.feature.experience.domain.model.Tour


data class MapState(
    val tour: Tour? = null,
    val touItemsList: List<Item>? = null,
    val itemToursList: List<Story>? = null,
    val currentStory: Story? = null,
    val currentItem: Item? = null,
    val storyList: List<Story>? = null,
    val nextPoints: List<Item>? = null
)

sealed class UIEvent {
    object PlayPause : UIEvent()
    object Play : UIEvent()
    object Pause : UIEvent()
    object Backward : UIEvent()
    object Forward : UIEvent()
    object SkipPrevious : UIEvent()
    object SkipNext : UIEvent()
    object TapOnAnnotation : UIEvent()
    data class UpdateProgress(val newProgress: Float) : UIEvent()
}
sealed class UIState {
    object Initial : UIState()
    object Ready : UIState()
}

sealed class MapEvent{
    data class Error(val message: String) : MapEvent()
    data object UnknownError : MapEvent()
    data object BackButtonClicked : MapEvent()
}