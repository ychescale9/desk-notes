@file:JvmName("DeskNotes")

package io.github.reactivecircus.desknotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.DpPropKey
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowCrossAxisAlignment
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@ExperimentalLayout
fun main() {
    Window(
        title = "DeskNotes",
        size = IntSize(960, 720),
    ) {
        val notes = remember { mutableStateOf(emptyList<Note>()) }
        val selectingColor = remember { mutableStateOf(false) }
        val selectedNote = remember { mutableStateOf<Note?>(null) }

        DesktopMaterialTheme {
            Row(modifier = Modifier.fillMaxSize()) {
                // side bar
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 36.dp, vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Desk",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Notes",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val transitionDefinition = remember {
                        transitionDefinition<Boolean> {
                            state(false) {
                                this[RotationPropKey] = 0f
                                this[TranslateXPropKey] = 0f
                                this[TranslateYPropKey] = 0f
                            }
                            state(true) {
                                this[RotationPropKey] = 45f
                                this[TranslateXPropKey] = 4.5f
                                this[TranslateYPropKey] = -2f
                            }
                            transition(false to true) {
                                RotationPropKey using tween()
                                TranslateXPropKey using tween()
                                TranslateYPropKey using tween()
                            }
                            transition(true to false) {
                                RotationPropKey using tween()
                                TranslateXPropKey using tween()
                                TranslateYPropKey using tween()
                            }
                        }
                    }
                    val transitionState = transition(transitionDefinition, selectingColor.value)

                    FloatingActionButton(
                        onClick = {
                            selectingColor.value = !selectingColor.value
                        },
                        backgroundColor = MaterialTheme.colors.onSurface,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        Icon(
                            Icons.Rounded.Add.copy(defaultWidth = 48.dp, defaultHeight = 48.dp),
                            tint = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.drawLayer(
                                rotationZ = transitionState[RotationPropKey],
                                translationX = transitionState[TranslateXPropKey],
                                translationY = transitionState[TranslateYPropKey],
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = selectingColor.value,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 24.dp),
                        ) {
                            NoteColors.forEach { noteColor ->
                                Button(
                                    onClick = {
                                        val newNote = Note(
                                            content = "Note ${notes.value.size + 1}",
                                            color = noteColor,
                                        )
                                        notes.value = buildList {
                                            addAll(notes.value)
                                            add(newNote)
                                        }
                                        selectingColor.value = false
                                        selectedNote.value = null
                                    },
                                    shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
                                    colors = ButtonConstants.defaultButtonColors(
                                        backgroundColor = noteColor
                                    ),
                                    modifier = Modifier.padding(8.dp).size(24.dp)
                                ) {}
                            }
                        }
                    }
                }

                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    modifier = Modifier.width(2.dp).fillMaxHeight()
                )

                // content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 36.dp, end = 36.dp, top = 24.dp),
                ) {
                    Crossfade(
                        current = notes.value.isNotEmpty(),
                    ) { hasNotes ->
                        if (!hasNotes) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Create a note to get started",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.LightGray,
                                    ),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            Column {
                                Row {
                                    Icon(
                                        Icons.Rounded.Search.copy(defaultWidth = 24.dp, defaultHeight = 24.dp),
                                        tint = Color.LightGray,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Search",
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                            color = Color.LightGray,
                                        ),
                                        modifier = Modifier.align(Alignment.CenterVertically).padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(40.dp))

                                Text(
                                    text = "Notes",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 52.sp,
                                    ),
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                ScrollableColumn(modifier = Modifier.fillMaxSize()) {
                                    FlowRow(
                                        mainAxisSpacing = 32.dp,
                                        crossAxisSpacing = 32.dp,
                                        crossAxisAlignment = FlowCrossAxisAlignment.Center,
                                    ) {
                                        notes.value.forEach { note ->
                                            val transitionDefinition = remember {
                                                transitionDefinition<NoteState> {
                                                    state(NoteState.NOT_EDITING) {
                                                        this[WidthPropKey] = 220.dp
                                                        this[HeightPropKey] = 220.dp
                                                        this[OpacityPropKey] = 1f
                                                    }
                                                    state(NoteState.EDITING_CURRENT_NOTE) {
                                                        this[WidthPropKey] = 300.dp
                                                        this[HeightPropKey] = 300.dp
                                                        this[OpacityPropKey] = 1f
                                                    }
                                                    state(NoteState.EDITING_OTHER_NOTE) {
                                                        this[WidthPropKey] = 180.dp
                                                        this[HeightPropKey] = 180.dp
                                                        this[OpacityPropKey] = 0.25f
                                                    }
                                                    transition(NoteState.NOT_EDITING to NoteState.EDITING_CURRENT_NOTE) {
                                                        WidthPropKey using tween()
                                                        HeightPropKey using tween()
                                                        OpacityPropKey using tween()
                                                    }
                                                    transition(NoteState.NOT_EDITING to NoteState.EDITING_OTHER_NOTE) {
                                                        WidthPropKey using tween()
                                                        HeightPropKey using tween()
                                                        OpacityPropKey using tween()
                                                    }
                                                    transition(NoteState.EDITING_CURRENT_NOTE to NoteState.EDITING_OTHER_NOTE) {
                                                        WidthPropKey using tween()
                                                        HeightPropKey using tween()
                                                        OpacityPropKey using tween()
                                                    }
                                                    transition(NoteState.EDITING_CURRENT_NOTE to NoteState.NOT_EDITING) {
                                                        WidthPropKey using tween()
                                                        HeightPropKey using tween()
                                                        OpacityPropKey using tween()
                                                    }
                                                    transition(NoteState.EDITING_OTHER_NOTE to NoteState.NOT_EDITING) {
                                                        WidthPropKey using tween()
                                                        HeightPropKey using tween()
                                                        OpacityPropKey using tween()
                                                    }
                                                    transition(NoteState.EDITING_OTHER_NOTE to NoteState.EDITING_CURRENT_NOTE) {
                                                        WidthPropKey using tween(delayMillis = AnimationConstants.DefaultDurationMillis)
                                                        HeightPropKey using tween(delayMillis = AnimationConstants.DefaultDurationMillis)
                                                        OpacityPropKey using tween(delayMillis = AnimationConstants.DefaultDurationMillis)
                                                    }
                                                }
                                            }
                                            val transitionState = transition(
                                                transitionDefinition, when (selectedNote.value) {
                                                    null -> NoteState.NOT_EDITING
                                                    note -> NoteState.EDITING_CURRENT_NOTE
                                                    else -> NoteState.EDITING_OTHER_NOTE
                                                }
                                            )

                                            Card(
                                                backgroundColor = note.color,
                                                shape = MaterialTheme.shapes.medium.copy(all = CornerSize(24.dp)),
                                                modifier = Modifier
                                                    .size(
                                                        width = transitionState[WidthPropKey],
                                                        height = transitionState[HeightPropKey],
                                                    )
                                                    .drawOpacity(transitionState[OpacityPropKey])
                                                    .clickable {
                                                        if (selectedNote.value != note) {
                                                            selectedNote.value = note
                                                        } else {
                                                            selectedNote.value = null
                                                        }
                                                        selectingColor.value = false
                                                    }
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(24.dp)
                                                ) {
                                                    Text(
                                                        text = note.content,
                                                        style = MaterialTheme.typography.h6,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Note(val content: String, val color: Color)

val NoteColors = listOf(
    Color(0xFFFECF7D),
    Color(0xFFFEA67D),
    Color(0xFFBE9DFD),
    Color(0xFF01D9FD),
    Color(0xFFE7F09A),
)


private val RotationPropKey = FloatPropKey()
private val TranslateXPropKey = FloatPropKey()
private val TranslateYPropKey = FloatPropKey()

private val WidthPropKey = DpPropKey()
private val HeightPropKey = DpPropKey()
private val OpacityPropKey = FloatPropKey()

private enum class NoteState {
    NOT_EDITING, EDITING_CURRENT_NOTE, EDITING_OTHER_NOTE
}
