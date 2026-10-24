package com.kaizehuang.vernier.features.update.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import io.github.xiaotong6666.uihelper.dialog.AdaptiveDecisionDialog

/** [onDecline] is the user's answer; [onDismiss] only closes the dialog and leaves the choice open. */
@Composable
fun GitHubAccelerationDialog(
    onEnable: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit,
) {
    AdaptiveDecisionDialog(
        show = true,
        title = stringResource(R.string.github_acceleration_dialog_title),
        message = stringResource(R.string.github_acceleration_dialog_message),
        confirmLabel = stringResource(R.string.github_acceleration_dialog_enable),
        dismissLabel = stringResource(R.string.github_acceleration_dialog_decline),
        onConfirm = onEnable,
        onDismissButton = onDecline,
        onDismissRequest = onDismiss,
        icon = Icons.Rounded.Speed,
        materialShape = ShapeTokens.CornerExtraLarge,
        materialContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    )
}
