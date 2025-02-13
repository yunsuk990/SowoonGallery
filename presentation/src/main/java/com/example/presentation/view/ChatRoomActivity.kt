package com.example.presentation.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Paint.Align
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainMessage
import com.example.domain.model.DomainUser
import com.example.presentation.R
import com.example.presentation.utils.noRippleClickable
import com.example.presentation.view.ui.theme.SowoonTheme
import com.example.presentation.viewModel.ChatRoomViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ChatRoomActivity : ComponentActivity() {

    private val viewModel: ChatRoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val artworkIntent = intent.getStringExtra("artwork")
        val artwork: DomainArtwork = Gson().fromJson(artworkIntent, DomainArtwork::class.java)

        val destUserIntent = intent.getStringExtra("destUser")
        val destUser: DomainUser = Gson().fromJson(destUserIntent, DomainUser::class.java)

        Log.d("ChatRoomActivity_destUser", destUser.toString())

        setContent {

            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

            val currentUserUid by viewModel.currentUserUid.collectAsState()
            val messageList by viewModel.messageList.collectAsState()
            var inputMessage by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current


            //채팅방 확인
            LaunchedEffect(Unit) {
                Log.d("chatRoomRoot_LaunchedEffect", "called")
                viewModel.checkChatRoom(artwork.artistUid!!, artwork.key!!)
            }

            SowoonTheme {
                Scaffold(
                    topBar = {
                        ChatRoomActivityTopBar(name = artwork.name.toString())
                    },
                    modifier = Modifier.fillMaxSize().background(Color.White)
                ) { padding ->
                    chatRoomRoot(
                        inputMessage = inputMessage,
                        currentUserUid = currentUserUid,
                        messageList = messageList,
                        destUser = destUser,
                        onValueChange = { inputMessage = it },
                        inputBtnOnClicked = {
                            viewModel.sendMessage(
                                message = inputMessage,
                                opponentUid = destUser.uid,
                                artworkId = artwork.key!!
                            )
                            inputMessage = ""
                        },
                        modifier = Modifier.fillMaxSize().background(Color.White).padding(padding).noRippleClickable {
                            keyboardController?.hide()
                        }
                    )
                }
            } // A surface container using the 'background' color from the theme
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun chatRoomRoot(
    inputMessage: String,
    currentUserUid: String,
    destUser: DomainUser,
    messageList: List<DomainMessage>,
    onValueChange: (String) -> Unit,
    inputBtnOnClicked: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.background(Color.White).nestedScroll(
            rememberNestedScrollInteropConnection()
        )
    ) {
        MessageList(currentUserUid, messageList, destUser, Modifier.weight(1f).padding(start = 5.dp , end = 8.dp))
        userMessageTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .imePadding(),
            inputMessage = inputMessage,
            onValueChange = onValueChange,
            inputBtnOnClicked = { inputBtnOnClicked() }
        )
    }

}

@Composable
fun MessageList(
    currentUserUid: String,
    messageList: List<DomainMessage>,
    destUser: DomainUser,
    modifier: Modifier
) {
    var listState = rememberLazyListState(initialFirstVisibleItemIndex = messageList.size)

    // 메시지를 날짜별로 그룹화하고 포맷 처리
    val groupedMessages = messageList
        .groupBy { it.timestamp.split("/").first() }
        .mapKeys { formatDate(it.key) }
        .toList()
        .sortedBy { it.first }

    LaunchedEffect(key1 = messageList.size) {
        if(messageList.isNotEmpty()){
            listState.scrollToItem(messageList.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        groupedMessages.forEach { (date, messages) ->
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 5.dp)){
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(color = Color.White).align(Alignment.Center).padding(vertical = 5.dp, horizontal = 12.dp),
                    ){
                        Text(
                            text = date, // 이미 날짜 포맷된 값
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            itemsIndexed(messages){ index, message ->
                val isFirstMessage = index == 0
                val isNewSender = isFirstMessage || messages[index - 1].senderUid != message.senderUid
                ChatBubble(message, currentUserUid, destUser, isNewSender)
            }
        }
    }
}

fun formatDate(dateStr: String): String {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    val date = dateFormat.parse(dateStr)
    val newDateFormat = SimpleDateFormat("yyyy년 M월 d일 EEEE", Locale.KOREA)
    return newDateFormat.format(date!!)
}

@Composable
fun ChatBubble(message: DomainMessage, currentUserUid: String, destUser: DomainUser, isNewSender: Boolean) {
    val isCurrentUser = message.senderUid == currentUserUid
    val bubbleColor = if(isCurrentUser) Color.Black else colorResource(R.color.messageGray)
    val textColor = if(isCurrentUser) Color.White else Color.Black
    val modifier = if(isCurrentUser) Modifier.padding(start = 70.dp) else Modifier.padding(end = 70.dp, start = 8.dp)

    val time = message.timestamp.split("/").last()

    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    ){
        val alignment = if(isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
        val padding = if(isCurrentUser) Modifier.padding(end = 5.dp, top = 3.dp) else Modifier.padding(start = 5.dp, top = 3.dp)

        Row(modifier = Modifier.align(alignment)){
            if(!isCurrentUser && isNewSender){
                userProfileImage(destUser.profileImage)
            }else{
                Spacer(modifier = Modifier.width(50.dp))
            }
            Column(modifier = modifier) {
                if(!isCurrentUser && isNewSender){
                    Text(text = destUser.name, color = Color.Black, fontSize = 14.sp)
                }
                Row(
                    verticalAlignment = Alignment.Bottom
                ){
                    if(isCurrentUser){
                        Text(text = time, fontSize = 10.sp, modifier = padding.align(Alignment.Bottom), lineHeight = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .clip(
                                if(isCurrentUser){
                                    if(isNewSender){
                                        RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 8.dp)
                                    }else{
                                        RoundedCornerShape(8.dp)
                                    }
                                }else{
                                    if(isNewSender){
                                        RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                                    }else{
                                        RoundedCornerShape(8.dp)
                                    }
                                }
                            )
                            .background(bubbleColor)
                            .padding(vertical = 5.dp, horizontal = 10.dp)
                            .align(Alignment.Bottom)
                    ){
                        SelectionContainer {
                            Text(text = message.message, color = textColor, fontSize = 16.sp)
                        }
                    }
                    if(!isCurrentUser){
                        Text(text = time, fontSize = 10.sp, modifier = padding.align(Alignment.Bottom), lineHeight = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun userProfileImage(imageUrl: String){
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(50.dp)
            .background(color = colorResource(id = R.color.lightgray)),
        contentAlignment = Alignment.Center,
    ){
//        Image(
//            painter = painterResource(id = imageUrl.toInt()),
//            contentDescription = null
//        )
        if(imageUrl.isEmpty()){
            Icon(painter = painterResource(R.drawable.profile), contentDescription = null, modifier = Modifier.size(20.dp))
        }else{
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun userMessageTextField(
    modifier: Modifier,
    inputMessage: String,
    onValueChange: (String) -> Unit = {},
    inputBtnOnClicked: () -> Unit,
){
    OutlinedTextField(
        value = inputMessage,
        modifier = modifier,
        onValueChange = { onValueChange(it)},
        placeholder = { Text(text = "메세지를 작성해주세요.")},
        shape = RoundedCornerShape(20.dp),
        trailingIcon = {
            IconButton(onClick = {
                if(inputMessage.isNotEmpty()) {inputBtnOnClicked()}
            }) {
                Icon(painter = painterResource(id = R.drawable.send), contentDescription = null)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(R.color.messageGray),
            unfocusedContainerColor = colorResource(R.color.messageGray),
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.Transparent,
            //disabledIndicatorColor = Color.Transparent,
            //cursorColor = Color.Black,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomActivityTopBar(name: String){
    var context = LocalContext.current
    Column() {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = name,
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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    Scaffold(
        topBar = {
            ChatRoomActivityTopBar("asdf")
        },
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        chatRoomRoot(
            inputMessage = "",
            currentUserUid = "123",
            messageList = listOf(
                DomainMessage(message = "hibrobrobrobro", senderUid = "123", timestamp = "2025.02.11/13:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "123", timestamp = "2025.02.11/13:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "1234", timestamp = "2025.02.11/14:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "1234", timestamp = "2025.02.11/14:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "1234", timestamp = "2025.02.11/14:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "1234", timestamp = "2025.02.11/14:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "1234", timestamp = "2025.02.11/14:54"),
                DomainMessage(message = "hibrobrobrobro", senderUid = "1234", timestamp = "2025.02.12/16:54"),
                ),
            onValueChange = {},
            inputBtnOnClicked = {},
            destUser = DomainUser(name = "최윤석"),
            modifier = Modifier.fillMaxSize().padding(it)
        )
    }
}