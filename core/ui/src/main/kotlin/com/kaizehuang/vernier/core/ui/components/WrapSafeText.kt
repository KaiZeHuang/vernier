package com.kaizehuang.vernier.core.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle

/** Compatibility facade; generic implementation lives in uihelper.adaptive. */
@Composable
public fun WrapSafeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
): Unit = io.github.xiaotong6666.uihelper.adaptive.WrapSafeText(
    text = text,
    modifier = modifier,
    style = style,
    color = color,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
)
