/*
 * Copyright (c) 2025 Iceberry-qdd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iceberry.notifybox.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    query: String?,
    backgroundColor: Color = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
    onQueryChanged: (String) -> Unit,
    onBack: () -> Unit,
    onClearQuery: () -> Unit
) {
    val focusRequester = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusEvent { Log.i("NBSearchTopBar", it.toString()) }
            .fillMaxWidth()
            .background(color = backgroundColor)
            .statusBarsPadding()
            .height(TopAppBarDefaults.TopAppBarExpandedHeight),
        value = query ?: "",
        onValueChange = onQueryChanged,
        placeholder = {
            Text("应用名/标题/内容")
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.clickable(onClick = onBack),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回"
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable(onClick = onClearQuery),
                imageVector = Icons.Filled.Clear,
                contentDescription = "清除搜索内容"
            )
        },
        visualTransformation = VisualTransformation.None,
        keyboardActions = KeyboardActions(onSearch = { query?.let { onQueryChanged(it) } }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            showKeyboardOnFocus = true
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            errorContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            focusedIndicatorColor = backgroundColor,
            unfocusedIndicatorColor = backgroundColor,
            errorIndicatorColor = backgroundColor,
            disabledIndicatorColor = backgroundColor
        )
    )
}

@PreviewLightDark
@Composable
private fun SearchTopBarPreview() {
    NBApplicationTheme {
        SearchTopBar(
            onBack = {},
            modifier = Modifier,
            query = "微信",
            onQueryChanged = {},
            onClearQuery = {}
        )
    }
}