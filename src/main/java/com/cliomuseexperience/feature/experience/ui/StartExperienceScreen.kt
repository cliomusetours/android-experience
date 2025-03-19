package com.cliomuseexperience.feature.experience.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cliomuseexperience.core.api.Lang_ID
import com.cliomuseexperience.core.api.Tour_ID
import com.cliomuseexperience.core.extensions.toTourImage
import com.cliomuseexperience.core.presentation.SdkActivity
import com.cliomuseexperience.core.presentation.composables.EventProcessor
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R
import com.cliomuseexperience.feature.experience.domain.ExperienceEvent
import com.cliomuseexperience.feature.experience.domain.model.Item
import com.cliomuseexperience.feature.experience.domain.model.MapBounds
import com.cliomuseexperience.feature.experience.domain.model.Point
import com.cliomuseexperience.feature.experience.domain.model.StartingPoint
import com.cliomuseexperience.feature.experience.domain.model.Tour
import com.cliomuseexperience.feature.experience.ui.composables.AuthorCard
import com.cliomuseexperience.feature.experience.ui.composables.DownloadModal
import com.cliomuseexperience.feature.experience.ui.composables.MapSection
import com.cliomuseexperience.feature.experience.ui.composables.PointsOfInterestSection
import com.cliomuseexperience.feature.experience.ui.composables.SponsorSection
import com.cliomuseexperience.feature.experience.ui.composables.TourDetailFooter
import com.cliomuseexperience.feature.experience.ui.composables.TourDetailHeader
import com.cliomuseexperience.feature.map.domain.UIEvent
import com.cliomuseexperience.feature.map.ui.MapViewModel


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StartExperienceScreen(
    navController: NavController,
    mapViewModel: MapViewModel = hiltViewModel()  ,
    viewModel: ExperienceViewModel = hiltViewModel()
) {

    val tourId = Tour_ID
    val langId = Lang_ID
    val context = LocalContext.current
    val state = viewModel.viewState
    val tour = state.tour

    val requiredPermissions = mutableListOf<String>().apply {
        add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissions = requiredPermissions.toTypedArray()


    var allPermissionsGranted by remember {
        mutableStateOf(permissions.all {
            checkPermissionFor(
                context,
                it
            )
        })
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            allPermissionsGranted = permissions.values.all { it }
            if (allPermissionsGranted) {
                tourId?.let {
                    viewModel.getAccess(context = context, tourId = it, langId = langId ?: 2)
                }
            } else {
                // Acción para permisos denegados
            }
        }
    )

    // Flag to track if the player has been cleared on first entry.
    var isPlayerCleared by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(allPermissionsGranted) {
        if (allPermissionsGranted) {
            if (!isPlayerCleared) {
                tourId?.let {
                    mapViewModel.clearPlayerIfNeeded(context, it, langId)
                    viewModel.getAccess(context = context, tourId = it, langId = langId ?: 2)
                }
                isPlayerCleared = true
            }
        } else {
            permissionLauncher.launch(permissions)
        }
    }
    BackHandler {
        mapViewModel.clearPlayerIfNeeded(context, tourId, langId)
        navController.popBackStack()
        viewModel.navigateToApp()
    }


    EventProcessor(viewModel) { event ->
        when (event) {

            is ExperienceEvent.NavigateToDetail -> {
                navController.navigate("mapScreen")

            }

            is ExperienceEvent.NavigateToApp -> {
                (context as SdkActivity).finish()
            }

            is ExperienceEvent.DownloadingSuccess -> {
                viewModel.closeDownloadModal()
            }

            is ExperienceEvent.DownloadingFailed -> {
                // action when tourDownload is Failed
            }

            is ExperienceEvent.UnknownError -> {
                // action when unknown error
            }

            is ExperienceEvent.Error -> {
                // action when error
            }

            else -> {
                // action when else
            }
        }
    }


    StartExperienceContent(
        tourImage = tour?.imageFile,
        tourName = tour?.name,
        authorName = tour?.author,
        tourDescription = tour?.description,
        authorLogo = tour?.authorImage,
        authorDescription = tour?.authorDescription,
        itemsList = tour?.items,
        mapBounds = tour?.mapBounds,
        customMapStyle = tour?.mapStyle,
        showRoute = tour?.showRoute ?: true,
        finishingPoint = tour?.finishingPoint?.coordinates?.let {
            StartingPoint(
                address = tour.finishingPoint?.address,
                coordinates =it,
                name = tour.finishingPoint?.name
            )
        },
        startingPoint = tour?.startingPoint?.coordinates?.let {
            StartingPoint(
                address = tour.startingPoint?.address,
                coordinates = it,
                name = tour.startingPoint?.name
            )
        },
        footerImage = tour?.imageFile?.toTourImage(),
        pointsList = tour?.points,
        footerClickListener = {
            mapViewModel.onUIEvent(UIEvent.Play)
            tour?.let { viewModel.navigateToDetail() }
        },
        onMapListener = { item ->
            mapViewModel.selectItem(item)
        },
        onBackClickListener = {
            viewModel.navigateToApp()
        },
        tour = tour,
        hasSponsor = tour?.hasSponsor,
        sponsorTitle = tour?.sponsorTitle,
        sponsor = tour?.sponsor,
        sponsorWebsite = tour?.sponsorWebsite,
        sponsorImage = tour?.sponsorImage?.toTourImage()

    )

    if (state.downloading) {
        DownloadModal(
            progress = state.downloadProgress,
            totalSize = state.downloadTotalSize,
            currentSize = state.downloadCurrentSize,
            percentage = state.downloadPercentage,
            stateDownload = state.downloadState,
            cancelDownload = {
                (context as SdkActivity).finish()
            }
        ) {
            tourId?.let {
                viewModel.getAccess(context = context, tourId = it, langId = langId ?: 2)
            }
        }
    }
}


fun checkPermissionFor(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED


@Composable
fun StartExperienceContent(
    tourImage: String?,
    tourName: String?,
    authorName: String?,
    tourDescription: String?,
    authorLogo: String?,
    authorDescription: String?,
    itemsList: List<Item>?,
    mapBounds: MapBounds?,
    tour: Tour?,
    customMapStyle: String?,
    showRoute: Boolean,
    finishingPoint: StartingPoint?,
    startingPoint: StartingPoint?,
    footerImage: ImageBitmap?,
    pointsList: List<Point>?,
    footerClickListener: () -> Unit,
    onMapListener: (Item) -> Unit,
    onBackClickListener: () -> Unit,
    hasSponsor: Int?,
    sponsorTitle: String?,
    sponsor: String?,
    sponsorWebsite: String?,
    sponsorImage: ImageBitmap?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                TourDetailHeader(
                    tourImage = tourImage,
                    tourName = tourName,
                    authorName = authorName,
                    onBackClickListener = { }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            tourDescription?.let { description ->
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = description,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 25.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color(0xFF161616)
                        )
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
            item {
                AuthorCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    authorLogo = authorLogo,
                    authorName = authorName
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            authorDescription?.let { authorDesc ->
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = authorDesc,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = CommissionerFontFamily,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color(0xFF161616)
                        )
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
            if (finishingPoint?.coordinates != null && startingPoint?.coordinates != null) {
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFCDCDCD))
                    )
                }
                item {
                    tour?.let { tourData ->
                        MapSection(
                            mapBounds = mapBounds,
                            finishingPoint = finishingPoint,
                            startingPoint = startingPoint,
                            itemsList = itemsList ?: emptyList(),
                            pointsList = pointsList,
                            onMapListener = onMapListener,
                            customMapStyle = customMapStyle ?: "",
                            showRoute = showRoute,
                            tour = tourData
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
            if (!itemsList.isNullOrEmpty()) {
                item {
                    PointsOfInterestSection(
                        interestPoints = itemsList,
                        withPlayer = false
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            if (hasSponsor != 0) {
                item {
                    SponsorSection(
                        sponsorTitle = sponsorTitle,
                        sponsor = sponsor,
                        sponsorWebsite = sponsorWebsite,
                        sponsorImage = sponsorImage
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = stringResource(id = R.string.back_button),
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
                .clickable { onBackClickListener() }
        )


        // Fixed footer at the bottom
        TourDetailFooter(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.BottomCenter),
            image = footerImage,
            clikListener = footerClickListener
        )
    }
}