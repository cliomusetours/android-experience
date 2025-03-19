package com.cliomuseexperience.feature.map.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cliomuseapp.cliomuseapp.feature.tour.TourDetailScreen
import com.cliomuseexperience.core.extensions.toAudioTour
import com.cliomuseexperience.core.extensions.toBackground
import com.cliomuseexperience.core.extensions.toColor
import com.cliomuseexperience.core.extensions.toIcon
import com.cliomuseexperience.core.extensions.toMultimediaImage
import com.cliomuseexperience.core.extensions.toPointsListImage
import com.cliomuseexperience.core.extensions.toTourGroundImage
import com.cliomuseexperience.core.presentation.SdkActivity
import com.cliomuseexperience.core.presentation.composables.EventProcessor
import com.cliomuseexperience.core.presentation.composables.MapboxView
import com.cliomuseexperience.core.presentation.composables.MarqueeTextFold
import com.cliomuseexperience.core.presentation.theme.SdkBlueMarine
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.MapBounds
import com.cliomuseexperience.feature.experience.domain.model.Point
import com.cliomuseexperience.feature.experience.domain.model.StartingPoint
import com.cliomuseexperience.feature.experience.domain.model.Story
import com.cliomuseexperience.feature.experience.domain.model.Tour
import com.cliomuseexperience.feature.experience.ui.composables.PointOfInterestWithPlayerCard
import com.cliomuseexperience.feature.map.domain.MapEvent
import com.cliomuseexperience.feature.map.domain.UIEvent
import com.cliomuseexperience.feature.map.ui.composables.EnsurePlayingItemIsVisible
import com.cliomuseexperience.feature.map.ui.composables.IndoorImageScreen
import com.cliomuseexperience.feature.map.ui.composables.OnAnnotationTapItemScroll
import com.cliomuseexperience.feature.map.ui.composables.PlayerControls
import com.cliomuseexperience.feature.map.ui.composables.PlayerDetail
import com.cliomuseexperience.feature.map.ui.composables.SheetDragView
import com.cliomuseexperience.player.service.SimpleMediaService
import kotlinx.coroutines.launch


@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val state = viewModel.viewState

    val mapBounds: MapBounds? = state.tour?.mapBounds
    val finishingPoint: StartingPoint? = state.tour?.finishingPoint
    val startingPoint: StartingPoint? = state.tour?.startingPoint

    LaunchedEffect(true) {
        val intent = Intent(context, SimpleMediaService::class.java)
        //startForegroundService(context, intent)
        (context as SdkActivity).startService()
    }

    EventProcessor(viewModel) { event ->
        when (event) {
            is MapEvent.UnknownError -> {
                // action when unknown error
            }

            is MapEvent.BackButtonClicked -> {
                navController.popBackStack()
            }

            is MapEvent.Error -> {
                // action when error
            }

            else -> {
                // action when else
            }
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        MapContent(
            vm = viewModel,
            progress = viewModel.progress,
            duration = viewModel.duration.toFloat(),
            isPlaying = viewModel.isPlaying,
            selectedItemState = state.currentItem,
            nextStepsList = state.nextPoints,
            itemIndex = state.tour?.items?.indexOf(state.currentItem)?.plus(1) ?: 1,
            itemName = state.currentItem?.name,
            onCompleteAudio = { viewModel.playNextStory() },
            mapBounds = mapBounds,
            customMapStyle = state.tour?.mapStyle,
            showRoute = state.tour?.showRoute ?: true,
            multimediaImage = state.currentStory?.imageFile?.toMultimediaImage(),
            multimediaVideo = state.currentStory?.videoFile,
            finishingPoint = finishingPoint,
            startingPoint = startingPoint,
            itemList = state.tour?.items,
            selectStoryClickListener = { story ->
                if (viewModel.viewState.currentStory?.id != story?.id) {
                    viewModel.selectStory(story)
                 } },
            selectItemClickListener = { clickedItem ->
                clickedItem?.let {
                    if (viewModel.viewState.currentItem?.id != it.id) {
                        viewModel.selectItem(it)

                    }
                }
            },
            slideProgressListener = { progress ->
                //      viewModel.audioPlayer.getPlayer().seekTo(progress)
            },
            storyTitle = state.currentStory?.title,
            storyCategory = state.currentStory?.category?.values?.firstOrNull(),
            audioFile = state.currentStory?.audioFile?.toAudioTour(),
            storyBody = state.currentStory?.body,
            storyColorBackground = state.currentStory?.category?.type.toColor(),
            storyBackground = state.currentStory?.category?.type.toBackground(),
            resumeTitleColor = state.currentStory?.category?.type.toBackground(),
            storyValue = state.currentStory?.category?.values?.firstOrNull()?.toIcon(),
            itemImage = state.currentItem?.imageFile?.toPointsListImage(),
            onUiEvent = { event -> viewModel.onUIEvent(event) },
            playResourceProvider = {
                if (viewModel.isPlaying) android.R.drawable.ic_media_pause
                else android.R.drawable.ic_media_play
            },
            progressProvider = {
                Pair(viewModel.progress, viewModel.progressString)
            },
            remainingTimeProvider = {
                viewModel.remainingTimeString
            },
            pointsList = state.tour?.points,
            isIndoors = state.tour?.isIndoors ?: false,
            indoorImage = state.tour?.groundImageFile?.toTourGroundImage(),
            onBackClickListener = { viewModel.clickBackButton() },
            tour = state.tour
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContent(
    vm: MapViewModel,
    storyTitle: String?,
    storyCategory: String?,
    audioFile: String?,
    progress: Float,
    duration: Float,
    itemImage: ImageBitmap?,
    itemIndex: Int?,
    itemName: String?,
    multimediaImage: ImageBitmap?,
    multimediaVideo: String?,
    nextStepsList: List<Item>?,
    isPlaying: Boolean,
    onCompleteAudio: () -> Unit,
    mapBounds: MapBounds?,
    customMapStyle: String?,
    showRoute: Boolean,
    finishingPoint: StartingPoint?,
    startingPoint: StartingPoint?,
    storyBody: String?,
    storyBackground: Color,
    storyValue: Int?,
    storyColorBackground: Color,
    pointsList: List<Point>?,
    itemList: List<Item>?,
    selectStoryClickListener: (Story?) -> Unit,
    selectItemClickListener: (Item?) -> Unit,
    selectedItemState: Item?,
    slideProgressListener: (progress: Long) -> Unit,
    playResourceProvider: () -> Int,
    progressProvider: () -> Pair<Float, String>,
    remainingTimeProvider: () -> String,
    onUiEvent: (UIEvent) -> Unit,
    isIndoors: Boolean,
    indoorImage: ImageBitmap?,
    resumeTitleColor: Color,
    onBackClickListener: () -> Unit,
    tour: Tour?

) {

    var selectedAnnotation by remember { mutableStateOf<Item?>(null) }
    var stepsState by remember { mutableStateOf(true) }
    val bottomPadding by animateDpAsState(targetValue = if (stepsState) 345.dp else 205.dp)
    var viewDetailState by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (stepsState) 0f else 180f,
        animationSpec = tween(
            durationMillis = 10,
            easing = FastOutSlowInEasing
        )
    )

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val expandedSheetHeight = screenHeight * 0.9f
    val listState = rememberLazyListState()


    BottomSheetScaffold(
        modifier = Modifier.background(color = SdkBlueMarine),
        sheetContent = {
            if (bottomSheetScaffoldState.bottomSheetState.currentValue != SheetValue.Expanded)
                PlayerControls(
                    isPlaying = isPlaying,
                    isExpanded = false,
                    duration = duration,
                    storyTitle = storyTitle,
                    storyCategory = storyCategory,
                    onUiEvent = { onUiEvent(it) },
                    playResourceProvider = playResourceProvider,
                    progressProvider = progressProvider,
                    remainingTimeProvider = remainingTimeProvider,
                )
            else
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(expandedSheetHeight)
                        .padding(top = 16.dp)
                ){

            androidx.compose.animation.AnimatedVisibility(
                    visible = bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 100)
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 100)
                    ) + fadeOut(targetAlpha = 0.0f)
                ) {
                    PlayerDetail(
                        vm = vm,
                        multimediaImage = multimediaImage,
                        multimediaVideo = multimediaVideo,
                        nextStepsList = nextStepsList,
                        itemIndex = itemIndex,
                        itemName = itemName,
                        onStepClickListener = { newItem ->
                            selectItemClickListener(newItem)
                        },
                        progress = progress,
                        duration = duration,
                        isPlaying = isPlaying,
                        isExpanded = true,
                        onCompleteAudio = {
                            onCompleteAudio()
                        },
                        slideProgressListener = { progress -> slideProgressListener(progress) },
                        storyTitle = storyTitle,
                        storyCategory = storyCategory,
                        audioFile = audioFile,
                        storyBody = storyBody,
                        storyBackground = storyBackground,
                        storyColorBackground = storyColorBackground,
                        itemsList = itemList,
                        storyValue = storyValue,
                        onUiEvent = { onUiEvent(it) },
                        playResourceProvider = playResourceProvider,
                        progressProvider = progressProvider,
                        remainingTimeProvider = remainingTimeProvider,
                        resumeTitleColor = resumeTitleColor
                    )
                }
                }


        },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 75.dp,
        sheetShape = when (bottomSheetScaffoldState.bottomSheetState.currentValue) {
            SheetValue.Expanded -> RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
            else -> RoundedCornerShape(0.dp)
        },
        sheetContainerColor = SdkBlueMarine,
        sheetDragHandle = {
            SheetDragView()
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding, top =5.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isIndoors) {
                    key(bottomPadding) {
                        IndoorImageScreen(
                            indoorImage,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }  else {
                MapboxView(
                    modifier = Modifier.fillMaxSize(),
                    mapBounds = mapBounds,
                    finishingPoint = finishingPoint,
                    startingPoint = startingPoint,
                    itemsList = itemList ?: listOf(),
                    loadAnnotationsFlag = true,
                    loadStartFinishPointIconsFlag = false,
                    pointList = pointsList,
                    loadRouteFlag = true,
                    applyZoom = true,
                    onAnnotationClick = { item ->
                        selectedAnnotation = item
                    },
                    loadCustomMapStyle = true,
                    show3DButton = true,
                    openGoogleMaps = false,
                    customMapStyle = customMapStyle,
                    showRoute = showRoute,
                    tour = tour,
                    showLocationButton = true,
                    showPuck = true
                )
                    OnAnnotationTapItemScroll(
                        lazyListState = listState,
                        items = itemList ?: listOf(),
                        currentItem = selectedAnnotation
                    )
            }}
            if (!viewDetailState) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        )
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White)
                        .padding(top = 6.dp, bottom = 110.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,){

                                    stepsState = !stepsState
                                    viewDetailState = false

            }
                            .padding(16.dp) // Increase the tap target around the icon
                    ) {
                        Icon(
                            modifier = Modifier
                                .width(40.dp)
                                .height(20.dp)
                                .rotate(rotationAngle),
                            imageVector = Icons.Sharp.KeyboardArrowDown,
                            contentDescription = if (stepsState)
                                stringResource(id = R.string.arrow_for_collapsing)
                            else
                                stringResource(id = R.string.arrow_for_expanding)
                        )
                    }

                    if (!stepsState) {
                        itemImage?.let { image ->
                            MarqueeTextFold(
                                image = image,
                                itemIndex = itemIndex,
                                title = itemName ?: "",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }



                    AnimatedVisibility(
                        visible = stepsState,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 0)
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(durationMillis = 0)
                        )
                    ) {


                        Column {
                            Spacer(modifier = Modifier.height(10.dp))

                            itemList?.let { interestPoints ->
                                LazyRow(state = listState) {
                                    itemsIndexed(interestPoints) { index, interestPoint ->

                                        val isThisItemSelected = interestPoint.stories
                                            ?.any { it.isSelected } == true  // true if ANY of this item's stories isSelected

                                        val isThisItemPlaying = isThisItemSelected && isPlaying

                                        PointOfInterestWithPlayerCard(
                                            item = interestPoint,
                                            index = index + 1,
                                            isItemPlaying = isThisItemPlaying,
                                            onPlayerClickListener = { clickedItem ->
                                                if (clickedItem != null) {
                                                    if (selectedItemState?.id == clickedItem.id) {
                                                        if (isPlaying) {
                                                            onUiEvent(UIEvent.Pause)
                                                        } else {
                                                            onUiEvent(UIEvent.Play)
                                                        }
                                                    } else {
                                                        selectItemClickListener(clickedItem)
                                                    }
                                                }
                                                viewDetailState = false
                                            },
                                            onClick = { clickedItem ->
                                                selectItemClickListener(clickedItem)
                                                viewDetailState = true
                                            }

                                        )

                                    }

                                }
                                EnsurePlayingItemIsVisible(
                                    lazyListState = listState,
                                    items = interestPoints,
                                    isPlaying = isPlaying,
                                    currentItem = selectedItemState
                                )
                            }

                        }
                    }

                }

            } else {
                itemList?.let { items ->
                    TourDetailScreen(
                        itemsList = items,
                        selectedItem = selectedItemState,
                        onItemChangeListener = { item ->
                            item?.let { newItem ->
                                selectItemClickListener(newItem)
                            }
                        },
                        onStoryClickListener = { story ->
                            selectStoryClickListener(story)
                        },
                        onBackClickListener = {
                            viewDetailState = false
                        }
                    )
                }

            }

            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .width(40.dp)
                    .height(40.dp)
                    .align(Alignment.TopStart)
                    .clickable { onBackClickListener() },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = stringResource(id = R.string.back_button),
                alignment = Alignment.TopStart
            )

        }


    }

}

