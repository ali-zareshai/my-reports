package ir.esfandune.wave.compose.component.core

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@Composable
fun AnimatedContent(
    trueState: Boolean,
    modifier: Modifier = Modifier,
    clip: Boolean = false,
    states: @Composable (Boolean) -> Unit
) {
    AnimatedContent(modifier = modifier, targetState = trueState, transitionSpec = {
        if (targetState) {
            (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
        } else {
            (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
        }.using(
            SizeTransform(clip = clip)
        )
    }, label = "slideFade"
    ) { states(it) }
}

@Composable
fun ScaleAnimatedContent(
    trueState: Boolean,
    modifier: Modifier = Modifier,
    states: @Composable (Boolean) -> Unit
) {
    AnimatedContent(modifier = modifier, targetState = trueState, transitionSpec = {
            scaleIn().togetherWith(scaleOut())
    }, label = "scale"
    ) { states(it) }
}

@Composable
fun AnimatedContent(
    targetState: Int,
    modifier: Modifier = Modifier,
    clip: Boolean = false,
    states: @Composable (Int) -> Unit
) {
    AnimatedContent(targetState.toLong(), modifier, clip) { states(it.toInt()) }
}

@Composable
fun AnimatedContent(
    targetState: Long,
    modifier: Modifier = Modifier,
    clip: Boolean = false,
    states: @Composable (Long) -> Unit
) {
    AnimatedContent(modifier = modifier,
        targetState = targetState,
        transitionSpec = {
            // Compare the incoming number with the previous number.
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = clip)
            )
        }, label = "fadeSlide"
    ) { states(it) }
}

@Composable
fun AnimatedContentScale(
    trueState: Boolean,
    modifier: Modifier = Modifier,
    clip: Boolean = true,
    states: @Composable (Boolean) -> Unit
) {
    AnimatedContent(modifier = modifier, targetState = trueState, transitionSpec = {
        if (targetState) {
            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
        } else {
            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
        }.using(
            SizeTransform(clip = clip)
        )
    }, label = "scaleFade"
    ) { states(it) }
}
