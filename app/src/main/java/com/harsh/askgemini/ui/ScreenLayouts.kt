package com.harsh.askgemini.ui

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.askgemini.R
import com.harsh.askgemini.data.Screen
import com.harsh.askgemini.util.Cupboard.cleanedString
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay

@Composable
fun SuccessLayout(outputText: String, textToCopy: String) {
    val localClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(end = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.BubbleChart,
                    contentDescription = "Prompt icon",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.requiredSize(36.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy answer",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .requiredSize(28.dp)
                        .clickable {
                            localClipboardManager.setText(AnnotatedString(textToCopy.cleanedString()))
                            Toast
                                .makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT)
                                .show()
                        }
                )

            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                MarkdownText(
                    markdown = outputText,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = TextStyle(fontFamily = FontFamily.Serif),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    isTextSelectable = true,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun ErrorLayout(errorMessage: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp),
            fontFamily = FontFamily.Serif
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DotLoadingAnimation(
    modifier: Modifier = Modifier,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    circleSize: Dp = 36.dp,
    animationDelay: Int = 400,
    initialAlpha: Float = 0.3f,
) {
    // 3 Circles
    val circles = listOf(
        remember { Animatable(initialValue = initialAlpha) },
        remember { Animatable(initialValue = initialAlpha) },
        remember { Animatable(initialValue = initialAlpha) },
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = (animationDelay / circles.size).toLong() * index)

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = animationDelay),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    // Container for 3 Circles
    Row(modifier = modifier) {
        circles.forEachIndexed { index, animatable ->
            if (index != 0) Spacer(Modifier.width(6.dp)) // Gap between circle

            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(color = circleColor.copy(animatable.value))
            )
        }
    }
}

@Composable
fun ScreenEntryCard(screen: Screen, background: Color, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(25.dp)),
        colors = CardDefaults.cardColors(
            containerColor = background.copy(0.55F)
        )
    ) {

        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(screen.titleResId),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontFamily = FontFamily(Font(R.font.lemony, FontWeight.ExtraBold)),
                color = Color.Black.copy(0.85F),
            )

            Text(
                text = stringResource(screen.descriptionResId),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontFamily = FontFamily(Font(R.font.lemony, FontWeight.ExtraBold)),
            )

            Spacer(modifier = Modifier.weight(1F))

            FloatingActionButton(
                onClick = { onItemClick(screen.routeId) },
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Go",
                    tint = Color.Black.copy(0.75F)
                )
            }
        }
    }
}
