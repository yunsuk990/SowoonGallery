package com.example.presentation.view.Setting

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.model.UploadState
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ProfileEditViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                        .background(Color.White)
                ) {
                    ProfileEditScreen(
                        userInfo = userInfo!!,
                        onUploadBtnClick = { uri, domainUser ->
                            if(uploadState !is UploadState.Loading){
                                viewModel.updateUserProfile(uri = uri, updateUserInfo = domainUser)
                            }
                        }
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(userInfo: DomainUser, onUploadBtnClick: (Uri?, DomainUser) -> Unit) {
    var photoGalleryUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var profileUrl by remember { mutableStateOf<String>(userInfo.profileImage) }

    var userMode = if(userInfo.mode == 0) {
        "User"
    } else if(userInfo.mode == 1) {
        "Artist"
    } else {
        "Manager"
    }

    val CAMERAX_PERMISSIONS =  arrayOf(
        Manifest.permission.CAMERA,
    )

    val context = LocalContext.current

    var name by remember { mutableStateOf(userInfo.name) }
    var email by remember { mutableStateOf(userInfo.email) }
    var review by remember { mutableStateOf(userInfo.review) }
    var mode by remember { mutableStateOf(userMode) }
    var selectedDate by remember { mutableStateOf(userInfo.birth) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if(uri != null){
            Log.d("singlePhotoPickerLauncher", uri.toString())
            photoGalleryUri = uri
        }}
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoGalleryUri = cameraUri
            Log.d("CameraCapture", "사진 저장 완료: ${photoGalleryUri}")
        }
    }

    fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context.applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun createImageFile(): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "IMG_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    ModalBottomSheetLayout(
        sheetContent = {
            profileEditModalBottom(
                sheetState,
                getImageFromGallery = { singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},
                takeCamera = {
                    if(!hasRequiredPermissions()) {
                        ActivityCompat.requestPermissions(
                            context as Activity, CAMERAX_PERMISSIONS, 0
                        )
                    }else{
                        cameraUri = createImageFile()
                        cameraLauncher.launch(cameraUri!!)
                    }
                },
                changeSimpleImage = {
                    photoGalleryUri = Uri.EMPTY
                    profileUrl = ""
                }
            )
        },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 75.dp)
            ) {
                ProfileEditTopBar()
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProfileImage(photoGalleryUri = photoGalleryUri, profileUrl = profileUrl, onClick = { scope.launch { sheetState.show() }} )
                    Spacer(modifier = Modifier.height(30.dp))

                    var showDatePicker by remember { mutableStateOf(false) }
                    val datePickerState = rememberDatePickerState()
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        selectedDate = (formatter.format(Date(it)))
                    }

                    userInputTextField(
                        value = mode,
                        onValueChange = {},
                        label = "Mode",
                        enabled = false
                    )
                    userInputTextField(
                        value = PhoneNumberUtils.formatNumber(userInfo.phoneNumber, "KR"), label = "전화번호",
                        onValueChange = {},
                        minLines = 1,
                        enabled = false
                    )
                    userInputTextField(
                        value = name,
                        onValueChange = {newName -> name = newName},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        label = "이름")

                    userInputTextField(
                        value = email,
                        onValueChange = { newText -> email = newText},
                        label = "이메일",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        minLines = 1,
                        enabled = true
                    )

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors(cursorColor = Color.Black, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Black, focusedLabelColor = Color.Black, focusedContainerColor = Color.White, disabledContainerColor = Color.White),
                        label = {  Text(text = "태어난 날짜", color = Color.Black) },
                        textStyle = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        leadingIcon = {
                            IconButton( onClick = { showDatePicker = true}) { Icon(Icons.Filled.DateRange, contentDescription = null) }
                        },
                    )

                    if(showDatePicker){
                        Popup(
                            onDismissRequest = { showDatePicker = false },
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 4.dp)

                            ) {
                                DatePicker(
                                    state = datePickerState,
                                    showModeToggle = false,
                                    colors = DatePickerDefaults.colors(
                                        containerColor = colorResource(R.color.lightwhite),
                                    ),
                                    title = {}
                                )
                            }
                        }
                    }
                    userInputTextField(
                        value = review,
                        onValueChange = {newText -> review = newText},
                        label = "자기소개",
                        minLines = 8,
                    )
                }
            }
            uploadButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                photoGalleryUri = photoGalleryUri,
                onClick = {
                    val updateUserInfo = userInfo.copy(name = name, review = review, email = email, profileImage = photoGalleryUri.toString(), birth = selectedDate)
                    Log.d("uploadButton", updateUserInfo.toString())
                    onUploadBtnClick(photoGalleryUri, updateUserInfo)
                }
            )
        }

    }
}

@Composable
fun profileEditModalBottom(
    sheetState: ModalBottomSheetState,
    getImageFromGallery: () -> Unit,
    takeCamera: () -> Unit,
    changeSimpleImage: () -> Unit
    ) {
    val scope = rememberCoroutineScope()
    Column {
        Text(text = "사진 선택",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    getImageFromGallery()
                    scope.launch { sheetState.hide() }
                }
                .padding(15.dp))
        Text(text = "사진 촬영",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    takeCamera()
                    scope.launch { sheetState.hide() }
                }
                .padding(15.dp))
        Text(text = "기본 이미지로 변경",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    changeSimpleImage()
                    scope.launch { sheetState.hide() }
                }
                .padding(15.dp))
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
        shape = RectangleShape,
        contentPadding = PaddingValues(vertical = 15.dp)
    ) {
        Text(text = "저장하기", fontSize = 16.sp, color = Color.White)
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileEditTest(){
    Surface(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        var uploadState by remember { mutableStateOf<UploadState>(UploadState.Loading) }
        Column() {
            ProfileEditScreen(DomainUser(phoneNumber = "01099016074", profileImage = "123"), { _, _ ->})
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
fun userInputTextField(value: String, keyboardOptions: KeyboardOptions = KeyboardOptions(), onValueChange: (String) -> Unit, label: String, minLines: Int = 1, enabled: Boolean = true){
    OutlinedTextField(
        value = value,
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color.Black,
            letterSpacing = 0.5.sp
        ),
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Black,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Black,
            focusedLabelColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedLabelColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledTextColor = Color.Gray,
        ),
        keyboardOptions = keyboardOptions,
        onValueChange = { onValueChange(it) },
        label = {
            Text(text = label)
        },
        minLines = minLines,
        maxLines = minLines,
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    )
}


@Composable
fun ProfileImage(
    photoGalleryUri: Uri?,
    profileUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .noRippleClickable {
                onClick()
            },
        contentAlignment = Alignment.BottomEnd,
    ) {
        if (photoGalleryUri != null && photoGalleryUri != Uri.EMPTY) {
            AsyncImage(
                modifier = Modifier.size(120.dp).clip(CircleShape),
                model = photoGalleryUri,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.shadow(2.dp, shape = CircleShape).clip(CircleShape)
                    .background(Color.White).padding(2.dp)
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else if (profileUrl.isNotEmpty()) {
            AsyncImage(
                modifier = Modifier.size(120.dp).clip(CircleShape),
                model = profileUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.shadow(2.dp, shape = CircleShape).clip(CircleShape)
                    .background(Color.White).padding(5.dp)
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().clip(CircleShape)
                    .background(color = colorResource(R.color.lightgray)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
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
