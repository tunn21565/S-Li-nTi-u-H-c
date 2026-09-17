package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AttendanceStatus
import com.example.data.model.BehaviorType
import com.example.data.model.Student
import com.example.ui.theme.BoyAvatarBg
import com.example.ui.theme.BoyAvatarText
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedContainer
import com.example.ui.theme.GirlAvatarBg
import com.example.ui.theme.GirlAvatarText
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.InfoBlueContainer
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.util.ZaloShareHelper

@Composable
fun StudentAvatar(
    stt: Int,
    gender: String,
    modifier: Modifier = Modifier,
    size: Int = 44,
    photoUri: String? = null,
    onClick: (() -> Unit)? = null,
    showEditBadge: Boolean = false
) {
    val isBoy = gender.equals("Nam", ignoreCase = true)
    val bgColor = if (isBoy) BoyAvatarBg else GirlAvatarBg
    val textColor = if (isBoy) BoyAvatarText else GirlAvatarText

    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .then(clickableModifier)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (!photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Ảnh học sinh $stt",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$stt",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size / 3.2).sp
                    )
                    Icon(
                        imageVector = if (isBoy) Icons.Default.Male else Icons.Default.Female,
                        contentDescription = gender,
                        tint = textColor.copy(alpha = 0.85f),
                        modifier = Modifier.size((size / 3.5).dp)
                    )
                }
            }
        }

        if (showEditBadge) {
            val badgeSize = (size / 2.8).coerceAtLeast(18.0).dp
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(SchoolPrimary)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Dán hình",
                    tint = Color.White,
                    modifier = Modifier.size((size / 4.2).coerceAtLeast(11.0).dp)
                )
            }
        }
    }
}

@Composable
fun AttachPhotoDialog(
    student: Student,
    onSavePhoto: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputUrl by remember { mutableStateOf(student.photoUri) }
    var previewUri by remember { mutableStateOf(student.photoUri) }
    var statusMessage by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            previewUri = uri.toString()
            inputUrl = uri.toString()
            statusMessage = "Đã chọn ảnh từ máy!"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = SchoolPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Dán Hình Cho Học Sinh",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Em: ${student.fullName} (STT: ${student.stt})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Preview
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(3.dp, SchoolPrimary, CircleShape)
                        .background(if (student.gender == "Nam") BoyAvatarBg else GirlAvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewUri.isNotBlank()) {
                        AsyncImage(
                            model = previewUri,
                            contentDescription = "Ảnh xem trước",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = SchoolPrimary,
                                modifier = Modifier.size(34.dp)
                            )
                            Text(
                                text = "Chưa có ảnh thẻ",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (statusMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusMessage,
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Command 1: Dán từ Clipboard
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clipData = clipboard?.primaryClip
                        val text = clipData?.getItemAt(0)?.text?.toString()?.trim()
                        if (!text.isNullOrBlank()) {
                            inputUrl = text
                            previewUri = text
                            statusMessage = "Đã dán liên kết ảnh từ bộ nhớ tạm!"
                        } else {
                            statusMessage = "Bộ nhớ tạm không có liên kết ảnh."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paste_from_clipboard_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📋 Dán ảnh từ Bộ nhớ tạm (Clipboard)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Command 2: Chọn từ thư viện điện thoại
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_from_gallery_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🖼️ Chọn ảnh từ thiết bị", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Command 3: Nhập URL
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = {
                        inputUrl = it
                        previewUri = it.trim()
                    },
                    label = { Text("Dán đường link ảnh (URL)", fontSize = 12.sp) },
                    placeholder = { Text("https://... hoặc file://...", fontSize = 11.sp) },
                    singleLine = true,
                    trailingIcon = {
                        if (inputUrl.isNotBlank()) {
                            IconButton(onClick = {
                                inputUrl = ""
                                previewUri = ""
                                statusMessage = ""
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (previewUri.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = {
                            inputUrl = ""
                            previewUri = ""
                            statusMessage = "Đã gỡ ảnh học sinh."
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xóa ảnh hiện tại", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSavePhoto(previewUri.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary),
                modifier = Modifier.testTag("save_photo_confirm_button")
            ) {
                Text("Lưu Ảnh", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun AttendanceBadge(status: AttendanceStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        AttendanceStatus.DI_HOC -> SuccessGreenContainer to SuccessGreen
        AttendanceStatus.DI_TRE -> WarningAmberContainer to WarningAmber
        AttendanceStatus.NGHI_CO_PHEP -> InfoBlueContainer to InfoBlue
        AttendanceStatus.NGHI_KHONG_PHEP -> ErrorRedContainer to ErrorRed
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun BehaviorBadge(type: BehaviorType, category: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = if (type == BehaviorType.PRAISE) {
        SuccessGreenContainer to SuccessGreen
    } else {
        ErrorRedContainer to ErrorRed
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = category,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MessagePreviewDialog(
    recipientName: String,
    recipientPhone: String,
    message: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    tint = SchoolPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Gửi Tin Nhắn Zalo Phụ Huynh",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Người nhận: $recipientName ($recipientPhone)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Nội dung tin nhắn chuẩn bị gửi:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "💡 Khi bấm 'Mở Zalo', hệ thống sẽ tự động sao chép tin nhắn và mở Zalo số $recipientPhone để thầy/cô dán gửi ngay.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    ZaloShareHelper.openZaloChat(context, recipientPhone, message)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
                modifier = Modifier.testTag("open_zalo_button")
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mở Zalo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                OutlinedButton(
                    onClick = {
                        ZaloShareHelper.copyToClipboard(context, message)
                    },
                    modifier = Modifier.testTag("copy_message_button")
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sao chép", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                OutlinedButton(
                    onClick = {
                        ZaloShareHelper.shareMessage(context, message)
                        onDismiss()
                    },
                    modifier = Modifier.testTag("share_message_button")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chia sẻ", fontSize = 12.sp)
                }
            }
        }
    )
}
