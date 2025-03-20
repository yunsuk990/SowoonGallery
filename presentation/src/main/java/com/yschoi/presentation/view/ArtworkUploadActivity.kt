package com.yschoi.presentation.view

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.presentation.R
import com.yschoi.presentation.model.UploadState
import com.yschoi.presentation.utils.noRippleClickable
import com.yschoi.presentation.view.ui.theme.SowoonTheme
import com.yschoi.presentation.viewModel.ArtworkUploadViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArtworkUploadActivity : ComponentActivity() {

    private val viewModel by viewModels<ArtworkUploadViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

            val uploadState by viewModel.uploadState.collectAsState()
            val userUid by viewModel.userUid.collectAsState()

            SowoonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ArtworkUploadScreen(
                        uploadState = uploadState,
                        uploadBtnOnClick = { list ->
                            if(!check(list)){
                                viewModel.uploadNewArtwork(list)
                            }
                        },
                        userUid = userUid,
                    )
                }
            }
        }
    }

    private fun check(list: List<Pair<Uri, DomainArtwork>>): Boolean{
        if(list.isEmpty()){
            Toast.makeText(this, "작품 정보를 작성해주세요.", Toast.LENGTH_SHORT).show()
            return true
        }
        for( (uri, artwork) in list){
            if(artwork.review!!.isEmpty()){
                Toast.makeText(this, "작품 설명 작성해주세요.", Toast.LENGTH_SHORT).show()
            }else if(artwork.name!!.isEmpty()){
                Toast.makeText(this, "작품 이름 작성해주세요.", Toast.LENGTH_SHORT).show()
            }else if(artwork.size!!.isEmpty()){
                Toast.makeText(this, "작품 크기 작성해주세요.", Toast.LENGTH_SHORT).show()
            }else if(artwork.material!!.isEmpty()){
                Toast.makeText(this, "작품 재료 작성해주세요.", Toast.LENGTH_SHORT).show()
            }else if(artwork.madeIn!!.isEmpty()){
                Toast.makeText(this, "작품 제작년도 작성해주세요.", Toast.LENGTH_SHORT).show()
            }else if(artwork.minimalPrice!!.isEmpty()){
                Toast.makeText(this, "작품 금액 작성해주세요.", Toast.LENGTH_SHORT).show()
            }else if(artwork.category!!.isEmpty()){
                Toast.makeText(this, "작품 카테고리 선택해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                continue
            }
            return true
        }
        return false
    }
}

@ExperimentalMaterial3Api
@Composable
fun ArtworkUploadScreen(
    uploadState: UploadState,
    uploadBtnOnClick: (List<Pair<Uri, DomainArtwork>>) -> Unit,
    userUid: String?,
) {
    val CAMERAX_PERMISSIONS =  arrayOf(
        Manifest.permission.CAMERA,
    )
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList())}

    var currentUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            currentUri = imageUri
            imageUris = listOf(imageUri!!)
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

    // 이미지별 데이터를 Map으로 관리
    var artworkDataMap by remember { mutableStateOf(mutableMapOf<Uri, DomainArtwork>())}

    val mutliplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris: List<Uri> ->
            imageUris = uris
            if(uris.isNotEmpty()){
                currentUri = uris.first()
            }
            scope.launch {
                sheetState.hide()
            }
        }
    )


    ModalBottomSheetLayout(
        sheetContent = {
            artworkUploadModalBottom(
                sheetState = sheetState,
                singlePhotoPickerLauncher = null,
                mutliplePhotoPickerLauncher = mutliplePhotoPickerLauncher,
                cameraStart = {
                    imageUri = createImageFile()
                    cameraLauncher.launch(imageUri!!)
                }
        ) },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                keyboardController?.hide()
            }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                ArtworkUploadTopBar()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(color = colorResource(R.color.lightwhite))
                        .clickable {
                            if(!hasRequiredPermissions()){
                                ActivityCompat.requestPermissions(
                                    context as Activity, CAMERAX_PERMISSIONS, 0
                                )
                            }else{
                                scope.launch {
                                    sheetState.show()
                                }
                            }
                        }
                ){
                    if(currentUri != null) {
                        AsyncImage(
                            model = currentUri,
                            contentDescription = "이미지",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )
                    }else{
                        Icon(painter = painterResource(R.drawable.photo_add), contentDescription = null, modifier = Modifier
                            .size(45.dp)
                            .align(Alignment.Center))
                    }
                }

                LazyRow(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(horizontal = 5.dp),
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(imageUris){ uri ->
                        Box(){
                            AsyncImage(
                                model = uri,
                                contentDescription = "이미지",
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .clickable {
                                        currentUri = uri
                                    },
                                contentScale = ContentScale.Crop
                            )
                            if(currentUri == uri){
                                Icon(painter = painterResource(R.drawable.check), tint = colorResource(R.color.check) ,contentDescription = null, modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(30.dp))
                            }
                        }
                    }
                }

                currentUri?.let { uri ->
                    val artwork = artworkDataMap[uri] ?: DomainArtwork(artistUid = userUid)
                    // currentUri가 변경될 때마다 새로운 값을 가져오도록 remember(currentUri) 사용
                    var artworkReview by remember(currentUri) { mutableStateOf(artwork.review ?: "") }
                    var artworkName by remember(currentUri) { mutableStateOf(artwork.name ?: "") }
                    var artworkSize by remember(currentUri) { mutableStateOf(artwork.size ?: "") }
                    var artworkMaterial by remember(currentUri) { mutableStateOf(artwork.material ?: "") }
                    var artworkMadeIn by remember(currentUri) { mutableStateOf(artwork.madeIn ?: "") }
                    var artworkMinimalPrice by remember(currentUri) { mutableStateOf(artwork.minimalPrice ?: "") }
                    var artworkCategory by remember(currentUri) { mutableStateOf(artwork.category) }


                    Column(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 150.dp)
                    ) {
                        outLinedTextField("작품 설명", artworkReview) { newText ->
                            artworkReview = newText
                            artworkDataMap[uri] = artwork.copy(review = newText)
                        }
                        outLinedTextField("작품 이름", artworkName) { newText ->
                            artworkName = newText
                            artworkDataMap[uri] = artwork.copy(name = newText)
                        }
                        outLinedTextField("크기", artworkSize) { newText ->
                            artworkSize = newText
                            artworkDataMap[uri] = artwork.copy(size = newText)
                        }
                        outLinedTextField("재료", artworkMaterial) { newText ->
                            artworkMaterial = newText
                            artworkDataMap[uri] = artwork.copy(material = newText)
                        }
                        outLinedTextField("제작년도", artworkMadeIn) { newText ->
                            artworkMadeIn = newText
                            artworkDataMap[uri] = artwork.copy(madeIn = newText)
                        }
                        outLinedTextField("최소금액 (만원)", artworkMinimalPrice) { newText ->
                            artworkMinimalPrice = newText
                            artworkDataMap[uri] = artwork.copy(minimalPrice = newText)
                        }
                        DropdownMenuCategoryFilter(
                            selectedCategory = if(artworkCategory!!.isEmpty()) "카테고리" else artworkCategory!!,
                            onCategorySelected = {
                                artworkCategory = it
                                artworkDataMap[uri] = artwork.copy(category = it)
                            }
                        )
                    }

                }
            }
            when(uploadState){
                is UploadState.Idle -> {}
                is UploadState.Loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator() // 로딩 표시
                    }
                }
                is UploadState.Success -> {
                    (context as Activity).finish()
                }
                is UploadState.Error -> {

                }
            }


            OutlinedButton(
                onClick = { uploadBtnOnClick(artworkDataMap.toList()) },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                contentPadding = PaddingValues(vertical = 15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text("업로드", fontSize = 16.sp, color = Color.White)
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuCategoryFilter(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val artworkCategoryList = listOf("한국화", "수채화", "아크릴화", "도자기")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it},
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp
                ),
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Black,
                    cursorColor = Color.Black
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false},
                containerColor = Color.White,
                matchTextFieldWidth = false
            ) {
                artworkCategoryList.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        onClick = {
                            onCategorySelected(s)
                            expanded = !expanded
                        },
                        text = { Text(text = s)}
                    )
                }
            }


        }
    }
}

@Composable
private fun outLinedTextField(title:String, text: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChange(it) },
        label = { Text(title) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = Color.Black,
            focusedBorderColor = Color.Black,
            cursorColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
    )
}

@Composable
fun artworkUploadModalBottom(
    sheetState: ModalBottomSheetState,
    singlePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>?,
    mutliplePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>?,
    cameraStart: () -> Unit,

    ) {
    val scope = rememberCoroutineScope()
    Column {
        Text(text = "사진 선택", fontSize = 16.sp, color = Color.Black, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if(singlePhotoPickerLauncher == null){
                    mutliplePhotoPickerLauncher?.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }else{
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                scope.launch {
                    sheetState.hide()
                }
            }
            .padding(15.dp))
        Text(text = "사진 촬영", fontSize = 16.sp, color = Color.Black, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                cameraStart()
                scope.launch {
                    sheetState.hide()
                }
            }
            .padding(15.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkUploadTopBar(){
    var context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "작품 업로드",
                style = MaterialTheme.typography.titleMedium
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = {
                (context as Activity).finish()
            }) { Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기") }
        },
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun ArtworkUploadActivityTest() {
    Surface(modifier = Modifier.fillMaxSize()) {
        ArtworkUploadScreen(
            uploadState = UploadState.Idle,
            uploadBtnOnClick = {},
            userUid = "123"
        )
        DropdownMenuCategoryFilter(
            selectedCategory = "adsaf",
            onCategorySelected = { it -> },
        )
    }
}

