package com.cliomuseapp.cliomuseapp.feature.tour.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuseexperience.core.presentation.theme.CommissionerFontFamily
import com.cliomuseexperience.experiencecliomuse.R

@Composable
fun GoBackMapSection(
    onBackClickListener: () -> Unit,
    goMapClickListener: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(40.dp),
    ) {

        Image(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clickable { onBackClickListener() },
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = stringResource(id = R.string.back_button)
        )


        goMapClickListener?.let {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .clickable { goMapClickListener() },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(id = R.string.go_to_map),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = CommissionerFontFamily,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFE2545A)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    modifier = Modifier
                        .fillMaxHeight(),
                    painter = painterResource(id = R.drawable.ic_map_circle_red),
                    contentDescription = stringResource(id = R.string.back_button)
                )
            }
        }


    }
}

@Preview
@Composable
fun GoBackMapSectionPreview() {
    GoBackMapSection (
        onBackClickListener = {},
        goMapClickListener = {}
    )

}
