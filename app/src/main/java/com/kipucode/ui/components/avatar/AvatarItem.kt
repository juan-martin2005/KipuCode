package com.kipucode.ui.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kipucode.R
import com.kipucode.domain.model.Avatar
import com.kipucode.ui.theme.KipuTeal

@Composable
fun AvatarItem(
    avatar: Avatar,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatar.resId),
            contentDescription = avatar.name.ifEmpty { "Avatar" },
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .then(
                    if (selected) Modifier.border(3.dp, KipuTeal, CircleShape) else Modifier
                )
        )

        if (selected) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_correct),
                contentDescription = "Seleccionado",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(color = KipuTeal)
                    .padding(2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAvatarItem() {
    AvatarItem(
        avatar = AvatarProvider.defaultAvatar,
        selected = true,
        onClick = {}
    )
}


