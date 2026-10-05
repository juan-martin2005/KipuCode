package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.ModuleItemStatus
import com.kipucode.viewmodel.ModuleItemUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSelectorBottomSheet(
    modules: List<ModuleItemUiModel>,
    selectedModuleId: String?,
    onSelectModule: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Seleccionar módulo",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Elige el módulo que deseas explorar y practicar.",
                fontFamily = Nunito,
                fontSize = 13.sp,
                color = Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(modules, key = { it.course.id }) { item ->
                    val isSelected = item.course.id == selectedModuleId
                    val borderColor = if (isSelected) KipuTeal else Color(0xFFE5E7EB)
                    val bgColor = if (isSelected) Color(0xFFEFF8FA) else Color.White

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectModule(item.course.id)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when (item.status) {
                                            ModuleItemStatus.CURRENT -> KipuTeal.copy(alpha = 0.15f)
                                            ModuleItemStatus.COMPLETED -> Color(0xFFE8F5E9)
                                            ModuleItemStatus.AVAILABLE -> Color(0xFFF0F4F8)
                                        },
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${item.course.orderIndex}",
                                    fontFamily = Nunito,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when (item.status) {
                                        ModuleItemStatus.CURRENT -> KipuTeal
                                        ModuleItemStatus.COMPLETED -> Color(0xFF2E7D32)
                                        ModuleItemStatus.AVAILABLE -> Gray
                                    },
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Módulo ${item.course.orderIndex}".uppercase(),
                                        fontSize = 11.sp,
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) KipuTeal else Gray
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (item.status) {
                                            ModuleItemStatus.CURRENT -> KipuTeal
                                            ModuleItemStatus.COMPLETED -> Color(0xFFE8F5E9)
                                            ModuleItemStatus.AVAILABLE -> Color(0xFFF0F4F8)
                                        }
                                    ) {
                                        Text(
                                            text = when (item.status) {
                                                ModuleItemStatus.CURRENT -> "En curso"
                                                ModuleItemStatus.COMPLETED -> "Completado"
                                                ModuleItemStatus.AVAILABLE -> "Disponible"
                                            },
                                            fontSize = 9.5.sp,
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.status) {
                                                ModuleItemStatus.CURRENT -> Color.White
                                                ModuleItemStatus.COMPLETED -> Color(0xFF2E7D32)
                                                ModuleItemStatus.AVAILABLE -> Gray
                                            },
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = item.course.title,
                                    fontFamily = Nunito,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = KipuDarkBlue
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check),
                                    contentDescription = "Seleccionado",
                                    tint = KipuTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
