package com.zareshahi.myreport.component

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.KeyboardVoice
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String,
    placeholderText:String="متن...",
    onDone: (KeyboardActionScope.() -> Unit)? = null,
    onValueChange: (String) -> Unit = {}
) {
    fun voiceIntent(): Intent {
        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voiceIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa")
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "صحبت کنید...")
        return voiceIntent
    }

    val voiceLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { actResult ->
            if (actResult.resultCode == Activity.RESULT_OK) {
                actResult.data?.let {
                    val result = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    onValueChange(result?.get(0).toString())
                }
            }
        }

    TextField(
        modifier = modifier
            .scale(0.95f)
            .fillMaxWidth()
            .padding(top = 7.dp, bottom = 4.dp),
        maxLines = 1, singleLine = true,
        shape = RoundedCornerShape(48.dp),
        value = value,
        keyboardActions = KeyboardActions(onSearch = onDone),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        placeholder = { Text(placeholderText, Modifier.fillMaxWidth(), maxLines = 1) },
        onValueChange = { onValueChange(it) },
        trailingIcon = {
            Icon(
                Icons.Rounded.KeyboardVoice,
                contentDescription = "ثبت با صدا",
                Modifier.clickable {
                    voiceLauncher.launch(voiceIntent())
                })
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )

    )

}