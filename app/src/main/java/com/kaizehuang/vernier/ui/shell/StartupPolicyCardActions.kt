package com.kaizehuang.vernier.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

internal class PolicyAction(
    val label: String,
    val onClick: () -> Unit,
)

@Composable
internal fun PolicyActionsMiuix(card: StartupPolicyCardUi) {
    val primary = card.primaryActionLabel?.let { label ->
        card.onPrimaryAction?.let { onClick -> PolicyAction(label, onClick) }
    }
    val secondary = card.secondaryActionLabel?.let { label ->
        card.onSecondaryAction?.let { onClick -> PolicyAction(label, onClick) }
    }
    if (primary == null && secondary == null) return

    val actions = listOfNotNull(primary, secondary)
    if (actions.size == 1) {
        val action = actions.single()
        MiuixButton(
            onClick = action.onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            colors = MiuixButtonDefaults.buttonColorsPrimary(),
        ) {
            MiuixText(text = action.label, style = MiuixTheme.textStyles.button)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            actions.forEachIndexed { index, action ->
                MiuixButton(
                    onClick = action.onClick,
                    modifier = Modifier.weight(1f),
                    colors = if (index == 0) {
                        MiuixButtonDefaults.buttonColorsPrimary()
                    } else {
                        MiuixButtonDefaults.buttonColors()
                    },
                ) {
                    MiuixText(text = action.label, style = MiuixTheme.textStyles.button)
                }
            }
        }
    }
}

/** A card's state. One that still needs a decision is inverted, so it reads first in either theme. */
@Composable
internal fun PolicyStatusCapsule(
    label: String,
    tone: StartupPolicyTone,
) {
    val colorScheme = MaterialTheme.colorScheme
    val inset = SurfaceTheme.palette.groupedInset
    val (container, content) = when (tone) {
        StartupPolicyTone.REQUIRED -> colorScheme.onSurface to colorScheme.surface
        StartupPolicyTone.READY -> inset to colorScheme.onSurface
        StartupPolicyTone.ACKNOWLEDGED,
        StartupPolicyTone.SUPPORT -> inset to colorScheme.onSurfaceVariant
    }
    WrapSafeText(
        text = label,
        modifier = Modifier
            .background(color = container, shape = ShapeTokens.CornerFull)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = SurfaceTypography.Caption,
        color = content,
    )
}

/**
 * The card's choices. A required card leads with its recommended action above a quieter way out.
 * An optional consent gives both answers the same weight, so neither is nudged.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun PolicyActions(
    card: StartupPolicyCardUi,
) {
    val primary = card.primaryActionLabel?.let { label ->
        card.onPrimaryAction?.let { onClick -> PolicyAction(label, onClick) }
    }
    val secondary = card.secondaryActionLabel?.let { label ->
        card.onSecondaryAction?.let { onClick -> PolicyAction(label, onClick) }
    }
    if (primary == null && secondary == null) {
        return
    }

    val actions = listOfNotNull(primary, secondary)
    if (actions.size == 1) {
        val action = actions.single()
        Button(
            onClick = action.onClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ButtonDefaults.MinHeight)
                .padding(top = 4.dp),
            shapes = ButtonDefaults.shapes(),
        ) {
            Text(text = action.label, maxLines = 1)
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        actions.forEachIndexed { index, action ->
            if (index == 0) {
                Button(
                    onClick = action.onClick,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = ButtonDefaults.MinHeight),
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = action.label, maxLines = 1)
                }
            } else {
                FilledTonalButton(
                    onClick = action.onClick,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = ButtonDefaults.MinHeight),
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = action.label, maxLines = 1)
                }
            }
        }
    }
}
