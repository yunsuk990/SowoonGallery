package com.example.presentation.view

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.UploadState
import com.example.presentation.viewModel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileEditDialog(
    viewModel: MainViewModel,
    onDismissClick: () -> Unit
) {
    var photoGalleryUri by remember { mutableStateOf<Uri?>(null) }
    val userInfo by viewModel.userInfoStateFlow.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if(uri != null){
                Log.d("photoGalleryImage", uri.toString())
                photoGalleryUri = uri
            }
        }
    )

    Dialog(onDismissRequest = {
        onDismissClick()
    }) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Profile", modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 15.dp), color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        profileImage(photoGalleryUri, singlePhotoPickerLauncher, userInfo)
                        Text(text = "choose image", fontSize = 16.sp, color = Color.Black, modifier = Modifier
                            .padding(start = 15.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }, textDecoration = TextDecoration.Underline)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    profileInfo(onDismissClick, viewModel, photoGalleryUri, userInfo)
                }
                when(uploadState){
                    is UploadState.Success -> {
                        Toast.makeText(context, "프로필을 수정하였습니다.", Toast.LENGTH_SHORT).show()
                        viewModel.resetUploadState()
                        onDismissClick()
                    }
                    is UploadState.Error -> {
                        Toast.makeText(context, (uploadState as UploadState.Error).message, Toast.LENGTH_SHORT).show()
                        viewModel.resetUploadState()
                        onDismissClick()
                    }
                    is UploadState.Loading -> { CircularProgressIndicator()}
                    else -> {}
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileInfo(
    onDismissClick: () -> Unit,
    viewModel: MainViewModel,
    photoGalleryUri: Uri?,
    userInfo: DomainUser
) {
    var name by remember { mutableStateOf(userInfo.name) }
    var age by remember { mutableStateOf(userInfo.age) }
    OutlinedTextField(
        value = name,
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color.Black
        ),
        onValueChange = { name = it },
        label = {
            Text(text = "Full name", color = Color.Gray)
        },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = age.toString(),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color.Black
        ),
        onValueChange = { age = it.toInt() },
        label = {
            Text(text = "Age", color = Color.Gray)
        },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedButton(
        onClick = {
            Log.d("ProfileEditScreen_saveBtn_clicked", photoGalleryUri.toString())
            Log.d("ProfileEditScreen_saveBtn_clicked", "name: ${name}, age: $age")
            viewModel.updateUserProfile(uri = photoGalleryUri, name = name, age = age)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(vertical = 15.dp)
    ) {
        Text(text = "저장하기", fontSize = 16.sp, color = Color.Black)
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileEditTest(){
    Surface(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        Column() {
           // ProfileEditDialog()
        }
    }
}


@Composable
fun profileImage(
    photoGalleryUri: Uri?,
    singlePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    userInfo: DomainUser,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .size(100.dp)
            .background(color = colorResource(id = R.color.lightgray))
            .clickable {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
        contentAlignment = Alignment.Center,
    ){
        if(userInfo.profileImage != null){
            AsyncImage(model = userInfo.profileImage, contentDescription = null, contentScale = ContentScale.Crop)
        }else if(photoGalleryUri != null){
            AsyncImage(model = photoGalleryUri, contentDescription = null, contentScale = ContentScale.Crop)
        } else{
            androidx.compose.material.Icon(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun bottomSheetLayout(){
    val bottomState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            Column {
                Text(text = "사진 선택", fontSize = 16.sp, color = Color.Black, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            bottomState.hide()
                        }
                    }
                    .padding(15.dp))
                Text(text = "사진 촬영", fontSize = 16.sp, color = Color.Black, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            bottomState.hide()
                        }
                    }
                    .padding(15.dp))
                Text(text = "취소", fontSize = 16.sp, color = Color.Black, modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            bottomState.hide()
                        }
                    }
                    .padding(15.dp))
            }
        },
        sheetGesturesEnabled = false,
    ) {

    }
}