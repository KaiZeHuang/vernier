package com.kaizehuang.vernier.startup.legal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import com.kaizehuang.vernier.core.ui.components.SurfaceIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.R
import com.kaizehuang.vernier.core.designsystem.theme.ContinuousCornerShape
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.ShapeTokens
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val AnswerFieldShape = ContinuousCornerShape(10.dp)
private val ConditionGlyphSize = 22.dp

/** Where a condition's text starts, and so where the separator under it starts too. */
internal val ConditionTextInset = 14.dp + ConditionGlyphSize + 12.dp

/** The risk reminder above the terms; its key sentence is set in bold rather than in a color. */
@Composable
internal fun AgreementRiskBanner() {
    val emphasis = stringResource(R.string.agreement_risk_body_emphasis)
    val template = stringResource(R.string.agreement_risk_body_template, emphasis)
    val emphasisRange = remember(template, emphasis) {
        val start = template.indexOf(emphasis)
        if (start >= 0) start until (start + emphasis.length) else null
    }
    val riskSummary = buildAnnotatedString {
        append(template)
        emphasisRange?.let { range ->
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.Bold),
                start = range.first,
                end = range.last + 1,
            )
        }
    }

    AdaptiveContent(
        miuix = {
            MiuixCard(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                BasicComponent(
                    title = stringResource(R.string.agreement_risk_title),
                    startAction = {
                        SurfaceIcon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp).size(22.dp),
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    },
                )
                MiuixText(
                    text = riskSummary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    style = MiuixTheme.textStyles.paragraph,
                    color = MiuixTheme.colorScheme.onSurface,
                )
            }
        },
        material = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SurfaceTheme.palette.groupedSurface,
                        shape = MaterialTheme.shapes.large,
                    )
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SurfaceIcon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = stringResource(R.string.agreement_risk_title),
                        style = SurfaceTypography.Headline,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = riskSummary,
                    style = SurfaceTypography.Callout,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = SurfaceTypography.Callout.lineHeight * 1.25,
                )
            }
        },
    )
}

/** One row of the consent checklist; a met condition trades its glyph for a check mark. */
@Composable
internal fun ConditionRow(
    icon: ImageVector,
    text: String,
    isComplete: Boolean,
    trailing: (@Composable () -> Unit)? = null,
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isComplete) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "agreement_condition_icon_scale",
    )

    AdaptiveContent(
        miuix = {
            BasicComponent(
                title = text,
                startAction = {
                    SurfaceIcon(
                        imageVector = if (isComplete) Icons.Rounded.CheckCircle else icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp).size(ConditionGlyphSize),
                        tint = if (isComplete) {
                            MiuixTheme.colorScheme.onSurface
                        } else {
                            MiuixTheme.colorScheme.onSurfaceVariantActions
                        },
                    )
                },
                endActions = trailing?.let { content -> { content() } },
            )
        },
        material = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SurfaceIcon(
                    imageVector = if (isComplete) Icons.Rounded.CheckCircle else icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(ConditionGlyphSize)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        },
                    tint = if (isComplete) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Text(
                    text = text,
                    modifier = Modifier.weight(1f),
                    style = if (isComplete) SurfaceTypography.CalloutEmphasized else SurfaceTypography.Callout,
                    color = if (isComplete) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                trailing?.invoke()
            }
        },
    )
}

/** A compact field for the arithmetic answer, outlined in the text color once the answer is right. */
@Composable
internal fun AgreementAnswerField(
    state: TextFieldState,
    isCorrect: Boolean,
) {
    val palette = SurfaceTheme.palette
    val focusManager = LocalFocusManager.current
    val borderColor by animateColorAsState(
        targetValue = if (isCorrect) MaterialTheme.colorScheme.onSurface else palette.separator,
        label = "agreement_answer_border",
    )

    AdaptiveContent(
        miuix = {
            MiuixTextField(
                state = state,
                modifier = Modifier.width(88.dp),
                inputTransformation = AnswerInputTransformation,
                textStyle = MiuixTheme.textStyles.headline2.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                onKeyboardAction = { focusManager.clearFocus() },
                lineLimits = TextFieldLineLimits.SingleLine,
            )
        },
        material = {
            BasicTextField(
                state = state,
                modifier = Modifier.width(88.dp),
                inputTransformation = AnswerInputTransformation,
                textStyle = SurfaceTypography.Headline.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                onKeyboardAction = { focusManager.clearFocus() },
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorator = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .background(color = palette.groupedSurface, shape = AnswerFieldShape)
                            .border(width = 1.dp, color = borderColor, shape = AnswerFieldShape)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        innerTextField()
                    }
                },
            )
        },
    )
}

/** At most six characters: digits, after a minus sign only at the start. */
private val AnswerInputTransformation = InputTransformation.maxLength(6).then {
    val accepted = asCharSequence().withIndex().all { (index, char) ->
        char.isDigit() || (char == '-' && index == 0)
    }
    if (!accepted) {
        revertAllChanges()
    }
}
