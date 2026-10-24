package com.kaizehuang.vernier.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme

/**
 * Colors and padding for Material's [androidx.compose.material3.Button], whose default shape is
 * already a capsule, in the three roles the app's buttons take.
 */
public object SurfaceButtonDefaults {
    /**
     * The main action of a page or dialog. A disabled one keeps a legible label, because here a
     * disabled button still says what it is waiting for.
     */
    @Composable
    public fun filledColors(): ButtonColors = ButtonDefaults.buttonColors(
        disabledContainerColor = SurfaceTheme.palette.groupedInset,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    /** A secondary action: an accent label on the inset fill. */
    @Composable
    public fun tonalColors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = SurfaceTheme.palette.groupedInset,
        contentColor = MaterialTheme.colorScheme.primary,
    )

    /** An action that stands out from its neighbours without filling in: an accent wash. */
    @Composable
    public fun tintedColors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        contentColor = MaterialTheme.colorScheme.primary,
    )

    /** Padding of a full-width button that ends a page, a card or a dialog. */
    public val LargeContentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
}
