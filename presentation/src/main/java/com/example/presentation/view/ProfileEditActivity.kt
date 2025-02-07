package com.example.presentation.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.UploadState
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ProfileEditViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditActivity : ComponentActivity() {

    private val viewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var userInfo = intent.getStringExtra("userInfo")?.let { it
            Gson().fromJson(it, DomainUser::class.java)
        } ?: DomainUser()
        viewModel.userInfo(userInfo)
        Log.d("userInfo", userInfo.toString())


        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

            val userInfo by viewModel.userInfoStateFlow.collectAsState()
            val uploadState by viewModel.uploadState.collectAsState()
            SowoonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    ProfileEditScreen(userInfo!!, onUploadBtnClick = { uri, domainUser ->
                        viewModel.updateUserProfile(uri = uri, updateUserInfo = domainUser)
                    })
                    when(uploadState){
                        is UploadState.Success -> {
                            Toast.makeText(this, "프로필을 수정하였습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is UploadState.Error -> {
                            Toast.makeText(this, (uploadState as UploadState.Error).message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is UploadState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileEditScreen(userInfo: DomainUser, onUploadBtnClick: (Uri?, DomainUser) -> Unit) {
    var photoGalleryUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(userInfo.name) }
    var age by remember { mutableStateOf(userInfo.age) }
    var review by remember { mutableStateOf(userInfo.review) }
    var mode by remember { mutableStateOf(if(userInfo.mode == 0) "User" else "Artist") }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri -> if(uri != null){ photoGalleryUri = uri } })
    ModalBottomSheetLayout(
        sheetContent = { ModalBottomSheet(sheetState, singlePhotoPickerLauncher) },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Column() {
                ProfileEditTopBar()
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    profileImage(photoGalleryUri, userInfo!!, onClick = { scope.launch { sheetState.show() }} )
                    Spacer(modifier = Modifier.height(30.dp))
                    profileInfo(
                        name = name,
                        nameChange = { name = it },
                        age = age,
                        ageChange = { age = it },
                        review = review,
                        reviewChange = { review = it },
                        mode = mode,
                    )
                }
            }
            uploadButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                photoGalleryUri = photoGalleryUri,
                onClick = {
                    val updateUserInfo = userInfo.copy(name = name, age = age, review = review, profileImage = photoGalleryUri.toString())
                    Log.d("uploadButton", updateUserInfo.toString())
                    onUploadBtnClick(photoGalleryUri, updateUserInfo)
                }
            )
        }

    }
}

@Composable
fun uploadButton(modifier: Modifier, photoGalleryUri: Uri?, onClick: () -> Unit){
    OutlinedButton(
        onClick = {
            onClick()
            Log.d("ProfileEditScreen_saveBtn_clicked", photoGalleryUri.toString())
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        ),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(vertical = 15.dp)
    ) {
        Text(text = "저장하기", fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun profileInfo(
    name: String,
    nameChange: (String) -> Unit,
    age: Int,
    ageChange: (Int) -> Unit,
    review: String,
    reviewChange: (String) -> Unit,
    mode: String
) {

    userInputTextField(mode, {}, "Mode", enabled = false)
    userInputTextField(name, nameChange, "이름")
    userInputTextField(age.toString(), { ageString -> ageChange(ageString.toInt())}, "나이")
    userInputTextField(review, reviewChange, "자기소개", minLines = 8)
}

@Preview(showSystemUi = true)
@Composable
fun ProfileEditTest(){
    Surface(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        var uploadState by remember { mutableStateOf<UploadState>(UploadState.Loading) }
        Column() {
            ProfileEditScreen(DomainUser(), { _, _ ->})
        }
        when(uploadState){
            is UploadState.Success -> {
            }
            is UploadState.Error -> {
            }
            is UploadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            else -> {}
        }
    }
}

@Composable
fun userInputTextField(value: String, onValueChange: (String) -> Unit, label: String, minLines: Int = 1, enabled: Boolean = true){
    OutlinedTextField(
        value = value,
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color.Black
        ),
        onValueChange = { onValueChange(it) },
        label = {
            Text(text = label, color = Color.Gray)
        },
        minLines = minLines,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun profileImage(
    photoGalleryUri: Uri?,
    userInfo: DomainUser,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .size(120.dp)
            .background(color = colorResource(id = R.color.lightgray))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ){
        if(userInfo.profileImage.isNotEmpty()){
            AsyncImage(model = userInfo.profileImage, contentDescription = null, contentScale = ContentScale.Crop)
        }else if(photoGalleryUri != null){
            AsyncImage(model = photoGalleryUri, contentDescription = null, contentScale = ContentScale.Crop)
        } else{
            Icon(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditTopBar(){
    var context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text( text = "프로필 수정하기", style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = { (context as Activity).finish() }) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "뒤로가기"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        actions = {},
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}
