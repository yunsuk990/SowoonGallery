package com.example.presentation.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.domain.model.*
import com.example.presentation.R
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun ChattingScreen(viewModel: MainViewModel, navController: NavController) {
    val chatRoomsList by viewModel.chatRoomsList.collectAsState()
    val context = LocalContext.current
    val userInfo by viewModel.userInfoStateFlow.collectAsState()

    Log.d("ChattingScreen", chatRoomsList.toString())

    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        ChattingTopBar()
        chatListItem(chatRoomsList, userInfo = userInfo, onChatRoomClick = { chatRoom ->
            context.startActivity(Intent(context, ChatRoomActivity::class.java)
                .putExtra("artwork", Gson().toJson(chatRoom.artwork, DomainArtwork::class.java))
                .putExtra("destUser", Gson().toJson(chatRoom.destUser, DomainUser::class.java))
            )
        })
    }

}

@Composable
fun chatListItem(
    chatRoomsList: List<DomainChatRoomWithUser>,
    onChatRoomClick: (DomainChatRoomWithUser) -> Unit,
    userInfo: DomainUser
) {
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(){
        items(chatRoomsList.size){index ->
            chatRoom(chatRoom = chatRoomsList[index], userUid = userInfo.uid,  onChatRoomClick = onChatRoomClick)
        }
    }
}

@Composable
fun chatRoom(chatRoom: DomainChatRoomWithUser, onChatRoomClick: (DomainChatRoomWithUser) -> Unit, userUid: String) {
    var timestamp = chatRoom.chatRoom.lastMessage.timestamp
    var dateFormat = SimpleDateFormat("yyyy.M.d/HH:mm", Locale.KOREA)
    var dateYearFormat = SimpleDateFormat("yyyy.M.d", Locale.KOREA)
    val koreanDateFormat = SimpleDateFormat("M월 d일", Locale.KOREA)

    val today = dateYearFormat.parse(dateYearFormat.format(Date()))!!
    val lastday = dateYearFormat.parse(timestamp)!!
    val diff = TimeUnit.DAYS.convert(lastday.time - today.time, TimeUnit.MILLISECONDS)

    var time = when {
        diff == 0L -> timestamp.split("/").last() // 같은 날이면 "HH:mm" 만 표시
        diff == -1L -> "어제" // 하루 차이면 "어제" 표시
        diff <= -365L -> dateYearFormat.format(lastday)
        else -> koreanDateFormat.format(lastday) // 날짜만 표시 ("MM.dd")
    }


    Row(
        modifier = Modifier.clickable {
            onChatRoomClick(chatRoom)
        }.padding( top = 8.dp, start = 10.dp, end = 10.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(80.dp)
                .background(color = colorResource(id = R.color.lightgray)),
            contentAlignment = Alignment.Center,
        ){
            AsyncImage(
                model = chatRoom.artwork.url,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.padding(start = 10.dp, top = 5.dp)) {
            Row(modifier = Modifier.padding(bottom = 5.dp)) {
                Text(text = chatRoom.artwork.name!!, color = Color.Black, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(time, color = Color.Gray, fontSize = 12.sp)
            }
            Row(){
                Text(
                    text = chatRoom.chatRoom.lastMessage.message,
                    color = Color.Gray,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                if(chatRoom.chatRoom.unreadMessages[userUid] != 0){
                    Box(
                        modifier = Modifier.padding(end = 3.dp).size(25.dp).clip(CircleShape).background(colorResource(R.color.red))
                    ){
                        Text(chatRoom.chatRoom.unreadMessages[userUid].toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "채팅", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        navigationIcon = {},
        actions = {},
    )
    Divider(thickness = 0.5.dp, color = Color.LightGray)
}

@Preview(showSystemUi = true)
@Composable
fun ChattingScreenTest(){
    Surface() {
        Column {
            chatListItem(
                chatRoomsList = listOf(
                    DomainChatRoomWithUser(
                        destUser = DomainUser(),
                        chatRoom = DomainChatRoom(lastMessage = DomainMessage(message = "asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf", timestamp = "2021.12.10/13:03")),
                        artwork = DomainArtwork(name = "yunsukasdfasdfasdfasasdfasdasdfasdfasdfasdf")
                ),
                    DomainChatRoomWithUser(
                        destUser = DomainUser(),
                        chatRoom = DomainChatRoom(unreadMessages = mapOf("123" to 3), lastMessage = DomainMessage(message = "asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf", timestamp = "2021.12.10/13:03")),
                        artwork = DomainArtwork(name = "yunsukasdfasdfasdfasasdfasdasdfasdfasdfasdf")
                    )
                ),
                onChatRoomClick = {},
                userInfo = DomainUser(uid = "123")
            )
        }
    }
}
