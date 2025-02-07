package com.example.presentation.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.presentation.R
import com.example.presentation.model.UploadState
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ArtworkUploadViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArtworkUploadActivity : ComponentActivity() {

    private val viewModel by viewModels<ArtworkUploadViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )
            SowoonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ArtworkUploadScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArtworkUploadScreen(viewModel: ArtworkUploadViewModel) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { imageUri = it } }
    )

    val artworkName = remember { mutableStateOf("") }
    val artworkSize = remember { mutableStateOf("") }
    val artworkMaterial = remember { mutableStateOf("") }
    val artworkMadeIn = remember { mutableStateOf("") }
    val artworkReview = remember { mutableStateOf("") }
    val artworkMinimalPrice = remember { mutableStateOf("") }

    val artworkCategoryList = listOf("한국화", "수채화", "아크릴화", "도자기")
    var selectedCategory by remember { mutableStateOf("카테고리") }

    val uploadState by viewModel.uploadState.collectAsState()

    ModalBottomSheetLayout(
        sheetContent = { ModalBottomSheet(sheetState, singlePhotoPickerLauncher) },
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.background(Color.White)){
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                ArtworkUploadTopBar()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.LightGray)
                        .clickable {
                            scope.launch {
                                sheetState.show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ){
                    if(imageUri != null){
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "이미지",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }else{
                        Icon(Icons.Filled.Add, contentDescription = null, Modifier.size(50.dp))
                    }
                }
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 80.dp)
                ) {
                    outLinedTextField("작품 설명", artworkReview.value){ artworkReview.value = it}
                    outLinedTextField("작품 이름", artworkName.value){ artworkName.value = it}
                    outLinedTextField("크기", artworkSize.value){ artworkSize.value = it}
                    outLinedTextField("재료", artworkMaterial.value){ artworkMaterial.value = it}
                    outLinedTextField("재작년도", artworkMadeIn.value){ artworkMadeIn.value = it}
                    outLinedTextField("최소금액", artworkMinimalPrice.value){ artworkMinimalPrice.value = it}
                    DropdownMenuCategoryFilter(
                        categoryList = artworkCategoryList,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
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
                    Log.d("ArtworkUploadScreen_Success", "success")
                    (context as Activity).finish()
                }
                is UploadState.Error -> {
                    Log.d("ArtworkUploadScreen_Error", "error")
                }
            }


            OutlinedButton(
                onClick = { viewModel.uploadNewArtwork(
                    DomainArtwork(
                        material = artworkMaterial.value,
                        size = artworkSize.value,
                        name = artworkName.value,
                        category = selectedCategory,
                        madeIn = artworkMadeIn.value,
                        review = artworkReview.value,
                        minimalPrice = artworkMinimalPrice.value.toInt()
                    ),
                    imageUri!!
                )},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(vertical = 15.dp),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 15.dp)
            ) {
                Text("업로드", fontSize = 16.sp, color = Color.White)
            }
        }
    }


}

@Composable
fun DropdownMenuCategoryFilter(
    categoryList: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(vertical = 15.dp)
        ) {
            Text(text = selectedCategory, color = Color.Black, textAlign = TextAlign.Start)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categoryList.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun outLinedTextField(title:String, text: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(title) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
    )
}

@Preview(showSystemUi = true)
@Composable
fun ArtworkUploadActivityTest() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            ModalBottomSheetLayout(
                sheetContent = { ModalBottomSheet(
                    rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded),
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia(),
                        onResult = {}
                    )
                ) },
            ) {
                Box(){
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        ArtworkUploadTopBar()
                        Column(
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 80.dp)
                        ) {
                        }
                    }
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator() // 로딩 표시
                    }

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 20.dp)
                    ) {
                        Text("업로드", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModalBottomSheet(
    sheetState: ModalBottomSheetState,
    singlePhotoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    val scope = rememberCoroutineScope()
    Column {
        Text(text = "사진 선택", fontSize = 16.sp, color = Color.Black, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
                scope.launch {
                    sheetState.hide()
                }
            }
            .padding(15.dp))
        Text(text = "사진 촬영", fontSize = 16.sp, color = Color.Black, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    sheetState.hide()
                }
            }
            .padding(15.dp))
        Text(text = "취소", fontSize = 16.sp, color = Color.Black, modifier = Modifier
            .fillMaxWidth()
            .clickable {
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

