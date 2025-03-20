package com.yschoi.presentation.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.model.DomainChatRoom
import com.yschoi.domain.model.DomainChatRoomWithUser
import com.yschoi.domain.model.DomainMessage
import com.yschoi.domain.model.DomainUser
import com.yschoi.presentation.R
import com.yschoi.presentation.utils.noRippleClickable
import com.yschoi.presentation.view.ui.theme.SowoonTheme
import com.yschoi.presentation.viewModel.ChatRoomViewModel
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

        //작품 정보
        val artworkIntent = intent.getStringExtra("artwork")
        val artwork: DomainArtwork = Gson().fromJson(artworkIntent, DomainArtwork::class.java)


        val destUserIntent = intent.getStringExtra("destUser")
        val destUser: DomainUser? = if(destUserIntent == null){
            //탈퇴한 사용자
            null
        }else {
            Gson().fromJson(destUserIntent, DomainUser::class.java)
        }


        val chatRoomWithUserIntent = intent.getStringExtra("chatRoom")
        val chatRoomWithUser: DomainChatRoom? =
            if(chatRoomWithUserIntent == null) null else Gson().fromJson(chatRoomWithUserIntent, DomainChatRoom::class.java)

        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.White,
                darkIcons = !isSystemInDarkTheme()
            )

            val currentUserUid by viewModel.currentUserUid.collectAsState()
            val messageList by viewModel.messageList.collectAsState()
            val currentUser by viewModel.currentUserInfo.collectAsState()
            val chatRoom by viewModel.chatRoom.collectAsState()
            var inputMessage by remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current
            var details by remember { mutableStateOf(false) }
            val exitChatRoom by viewModel.exitRoomInfo.collectAsState()

            if(exitChatRoom){
                finish()
            }

            //채팅방 확인
            LaunchedEffect(currentUser?.uid) {
                currentUser?.let {
                    if(chatRoomWithUser != null){
                        val destUserUid = chatRoomWithUser.status.keys.filter { it != currentUserUid }.first()
                        if(currentUser!!.mode == 1){
                            viewModel.checkChatRoom(userUid = destUserUid, artistUid = currentUserUid, artwork.key!!)
                        }else{
                            viewModel.checkChatRoom(userUid = currentUserUid, artistUid = destUserUid, artwork.key!!)
                        }
                    }else{
                        destUser?.let {
                            viewModel.checkChatRoom(userUid = currentUserUid, artistUid = destUser.uid, artwork.key!!)
                        }
                    }
                }
            }

            SowoonTheme {
                Scaffold(
                    topBar = {
                        ChatRoomActivityTopBar(name = if(!details) artwork.name.toString() else "Details", details = details,
                            detailsOnChange = {
                                newBool -> details = newBool
                            })
                    },
                    modifier = Modifier.fillMaxSize().background(Color.White)
                ) { padding ->
                    if(!details){
                            chatRoomRoot(
                                artwork = artwork,
                                currentUser = currentUser,
                                inputMessage = inputMessage,
                                currentUserUid = currentUserUid,
                                messageList = messageList,
                                destUser = destUser,
                                onValueChange = { inputMessage = it },
                                inputBtnOnClicked = {
                                    destUser?.let {
                                        viewModel.sendMessage(
                                            message = inputMessage,
                                            artworkId = artwork.key!!,
                                            destUser = destUser,
                                            artwork = artwork
                                        )
                                        inputMessage = ""
                                    }
                                },
                                updateArtworkSoldState = { state ->
                                    destUser?.let {
                                        viewModel.setArtworkState(artistUid = currentUserUid, sold = state, artworkId = artwork.key!!, destUid = destUser.uid)
                                    }
                                },
                                modifier = Modifier.fillMaxSize().background(Color.White).padding(padding).noRippleClickable {
                                    keyboardController?.hide()
                                }
                            )
                    }else{
                        ArtworkInfo(
                            artwork = artwork,
                            destUser = destUser,
                            modifier = Modifier.fillMaxSize().background(Color.White).padding(padding),
                            cardOnClick = {
                                startActivity(
                                    Intent(this, ArtworkDetailActivity::class.java)
                                        .putExtra("artwork", Gson().toJson(artwork))
                                )
                            },
                            currentUser = currentUser,
                            chatExitBtnOnClick = {
                                viewModel.exitChatRoom()
                            },
                            chatRoom = chatRoom
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.chatRoom.value != null){
            viewModel.enterChatRoom(viewModel.chatRoom.value!!.roomId)
        }
    }

    override fun onPause() {
        super.onPause()
        if(viewModel.chatRoom.value != null){
            viewModel.leaveChatRoom("onPause")
        }

    }
}

@Composable
fun ArtworkInfo(
    artwork: DomainArtwork,
    destUser: DomainUser?,
    modifier: Modifier,
    chatRoom: DomainChatRoom?,
    cardOnClick: () -> Unit,
    currentUser: DomainUser?,
    chatExitBtnOnClick: () -> Unit
){
    val destUserName = destUser?.name ?: "탈퇴한 사용자"
    val destUserMode = destUser?.mode ?: 0
    val destUserProfileImage = destUser?.profileImage ?: ""
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 25.dp)
        ){
            Text("Artwork", fontSize = 16.sp, color= Color.Black, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 25.dp).noRippleClickable {
                    cardOnClick()
                }
            ) {
                Box(){
                    AsyncImage(
                        model = artwork.url,
                        contentDescription = null,
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth().height(150.dp).padding(start = 10.dp , top = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(destUserName, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier.padding(start = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(color = colorResource(R.color.lightgray))
                                .padding(horizontal = 10.dp)
                        ){
                            if(destUserMode == 0){
                                Text("User", fontSize = 14.sp, color = Color.Black)
                            }else{
                                Text("Artist", fontSize = 14.sp, color = Color.Black)
                            }
                        }
                    }
                    Text(artwork.name!!, color = Color.Black, fontSize = 15.sp)
                    Text(artwork.review!!, color = Color.Gray, fontSize = 14.sp, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Divider(thickness = 0.5.dp, color = Color.LightGray, modifier = Modifier.fillMaxWidth())

        Column(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 25.dp)
        ) {
            Text("Users", fontSize = 16.sp, color= Color.Black, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.padding(top = 20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    userProfileImage(currentUser?.profileImage!!)
                    Text(currentUser?.name!!, color = Color.Black, fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    userProfileImage(destUserProfileImage)
                    Text(destUserName, color = Color.Black, fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp))
                }
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        if(chatRoom?.users?.size == 1){
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 15.dp)) {
                Button(
                    onClick = {
                        chatExitBtnOnClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text("채팅방 나가기", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun chatRoomRoot(
    inputMessage: String,
    currentUserUid: String,
    destUser: DomainUser?,
    messageList: List<DomainMessage>,
    onValueChange: (String) -> Unit,
    inputBtnOnClicked: () -> Unit,
    modifier: Modifier,
    artwork: DomainArtwork,
    updateArtworkSoldState: (Boolean) -> Unit,
    currentUser: DomainUser?,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(if(artwork.sold) "거래완료" else "판매중") }
    var artworkState = listOf("판매중","거래완료")

    Box(modifier = modifier){
        Column(
            modifier = Modifier.nestedScroll(
                rememberNestedScrollInteropConnection()
            )
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically){
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).noRippleClickable {
                        context.startActivity(Intent(context, ArtworkDetailActivity::class.java).putExtra("artwork", Gson().toJson(artwork)))
                    }
                ){
                    AsyncImage(
                        model = artwork.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(60.dp)
                    )
                }
                Column(
                    modifier = Modifier.padding(start = 15.dp),
                ) {
                    Text(DecimalFormat("#,###").format(artwork.minimalPrice.toInt() * 10000)+ "원", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if(currentUser?.mode != 0){
                        expanded = it
                    } },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.soldOut)),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.menuAnchor()
                    ) { Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedState, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if(currentUser?.mode != 0){
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false},
                        containerColor = Color.White,
                        matchTextFieldWidth = false
                    ) {
                        artworkState.forEachIndexed { index, s ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedState = s
                                    updateArtworkSoldState(if(index == 0) false else true)
                                    expanded = !expanded
                                },
                                text = { Text(text = s) }
                            )
                        }
                    }
                }
            }
            Divider(thickness = 0.5.dp, modifier = Modifier.fillMaxWidth(), color = Color.LightGray)

            MessageList(currentUserUid, messageList, destUser, Modifier.weight(1f).padding(start = 5.dp , end = 8.dp, bottom = 8.dp))
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

}

@Composable
fun MessageList(
    currentUserUid: String,
    messageList: List<DomainMessage>,
    destUser: DomainUser?,
    modifier: Modifier
) {
    var listState = rememberLazyListState(initialFirstVisibleItemIndex = messageList.size)

    LaunchedEffect(key1 = messageList.size) {
        if(messageList.isNotEmpty()){
            listState.scrollToItem(messageList.size - 1)
        }
    }

    // 메시지를 날짜별로 그룹화하고 포맷 처리
    val groupedMessages = messageList
        .groupBy { it.timestamp.split("/").first() }
        .mapKeys { formatDate(it.key) }
        .toList()
        .sortedBy { it.first }

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
fun ChatBubble(message: DomainMessage, currentUserUid: String, destUser: DomainUser?, isNewSender: Boolean) {
    val isCurrentUser = message.senderUid == currentUserUid
    val bubbleColor = if(isCurrentUser) Color.Black else colorResource(R.color.messageGray)
    val textColor = if(isCurrentUser) Color.White else Color.Black
    val modifier = if(isCurrentUser) Modifier.padding(start = 70.dp) else Modifier.padding(end = 70.dp, start = 8.dp)

    val time = message.timestamp.split("/").last()

    val destUserImage = if(destUser == null) "" else destUser.profileImage
    val destUserName = if(destUser == null) "탈퇴한 사용자" else destUser.name


    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    ){
        val alignment = if(isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
        val padding = if(isCurrentUser) Modifier.padding(end = 5.dp, top = 3.dp) else Modifier.padding(start = 5.dp, top = 3.dp)

        Row(modifier = Modifier.align(alignment).padding(top = if(isNewSender) 15.dp else 0.dp)){
            if(!isCurrentUser && isNewSender){
                userProfileImage(destUserImage)
            }else{
                Spacer(modifier = Modifier.width(50.dp))
            }
            Column(modifier = modifier) {
                if(!isCurrentUser && isNewSender){
                    Text(text = destUserName, color = Color.Black, fontSize = 14.sp)
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
fun ChatRoomActivityTopBar(name: String, details: Boolean, detailsOnChange: (Boolean) -> Unit){
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
                    if(details){
                        detailsOnChange(!details)
                    }else{
                        (context as Activity).onBackPressed()
                    }
                }) { Icon(painter = painterResource(id = R.drawable.back), contentDescription = "뒤로가기") }
            },
            actions = {
                if(!details){
                    IconButton(onClick = {
                        detailsOnChange(!details)
                    }) { Icon(painterResource(R.drawable.alert), contentDescription = null)}
                }
            }
        )
        Divider(thickness = 0.5.dp, color = Color.LightGray)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    var details by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ChatRoomActivityTopBar(name = if(!details) "artwork" else "Details", details = details, detailsOnChange = { details = it})
        },
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) { padding ->
        if(!details){
            chatRoomRoot(
                inputMessage = "",
                currentUserUid = "123",
                destUser = DomainUser(name = "최윤석"),
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
                modifier = Modifier.fillMaxSize().padding(padding),
                artwork = DomainArtwork(name = "asdfasdf",minimalPrice = "12"),
                currentUser = DomainUser(mode = 1),
                updateArtworkSoldState = {}
            )
        }else{
            ArtworkInfo(
                artwork = DomainArtwork(minimalPrice = "12",
                name = "asfasdfasf", review = "asfasdfasdfasdfasdklfasdklfkasdfjfklajsdklfjaklsdflkasdjfklasdjfklasdjfkljasklfkljlasfasdfasdfasdfasdklfasdklfkasdfjfklajsdklfjaklsdflkasdjfklasdjfklasdjfkljasklfkljlasfasdfasdfasdfasdklfasdklfkasdfjfklajsdklfjaklsdflkasdjfklasdjfklasdjfkljasklfkljlasfasdfasdfasdfasdklfasdklfkasdfjfklajsdklfjaklsdflkasdjfklasdjfklasdjfkljasklfkljlasfasdfasdfasdfasdklfasdklfkasdfjfklajsdklfjaklsdflkasdjfklasdjfklasdjfkljasklfkljlasfasdfasdfasdfasdklfasdklfkasdfjfklajsdklfjaklsdflkasdjfklasdjfklasdjfkljasklfkljl"),
                destUser = DomainUser(name = "정은숙"),
                modifier = Modifier.fillMaxSize().background(Color.White).padding(padding),
                cardOnClick = {},
                currentUser = DomainUser(name = "최윤석"),
                chatExitBtnOnClick = {},
                chatRoom = DomainChatRoom()
            )
        }
    }
}