package com.kipucode.ui.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kipucode.domain.model.Avatar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.button.OutlineButton
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito

@Composable
fun AvatarSelectionDialog(
    currentAvatarId: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier,
    avatarList: List<Avatar> = AvatarProvider.avatars,
    isLoading : Boolean = false
) {
    var tempSelectedId by remember(currentAvatarId) { mutableStateOf(currentAvatarId) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = "Elige tu Avatar",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = KipuDarkBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Selecciona el que mejor te represente",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar actualmente seleccionado (Preview en grande)
                val previewAvatar = AvatarProvider.getAvatarById(tempSelectedId)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .background(BackgroundGray, CircleShape)
                        .padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = previewAvatar.resId),
                        contentDescription = "Avatar Seleccionado",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Grilla de avatares seleccionables
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 64.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = avatarList,
                        key = { it.id }
                    ) { avatar ->
                        AvatarItem(
                            avatar = avatar,
                            selected = avatar.id == tempSelectedId,
                            onClick = { tempSelectedId = avatar.id },
                            size = 60.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Acciones: Cancelar y Guardar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlineButton(
                        textButton = "Cancelar",
                        onClickFilledButton = onDismiss,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp
                    )
                    FilledButton(
                        textButton = "Guardar",
                        isLoading = isLoading,
                        onClickFilledButton = {
                            onSave(tempSelectedId)
                        },
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAvatarSelectionDialog() {
    AvatarSelectionDialog(
        currentAvatarId = "avatar_005",
        onDismiss = {},
        onSave = {}
    )
}



