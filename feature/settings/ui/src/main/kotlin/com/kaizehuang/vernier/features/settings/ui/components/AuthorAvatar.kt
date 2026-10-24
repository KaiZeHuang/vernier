package com.kaizehuang.vernier.features.settings.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun AuthorAvatar(
    profile: AuthorProfile,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val avatarBitmap by produceState<ImageBitmap?>(null, profile.avatarAssetPath, context) {
        value = withContext(Dispatchers.IO) {
            profile.avatarAssetPath?.let { assetPath ->
                runCatching {
                    context.assets.open(assetPath).use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val bitmap = avatarBitmap
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            WrapSafeText(
                text = profile.name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}
