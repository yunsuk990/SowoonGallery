package com.example.presentation.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.domain.model.DomainArtwork
import com.example.domain.model.DomainChatRoom
import com.example.domain.model.DomainChatRoomWithUser
import com.example.presentation.R
import com.example.presentation.viewModel.MainViewModel
import com.google.gson.Gson

@Composable
fun ChattingScreen(viewModel: MainViewModel, navController: NavController) {
    val chatRoomsList by viewModel.chatRoomsList.collectAsState()
    LaunchedEffect(viewModel.userInfoStateFlow.value.uid){
        viewModel.loadChatLists()
    }

    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        ChattingTopBar()
        chatListItem(chatRoomsList)
    }

}

@Composable
fun chatListItem(chatRoomsList: List<DomainChatRoomWithUser>) {
    LazyColumn(){
        items(chatRoomsList.size){index ->
            chatRoom(chatRoom = chatRoomsList[index])
        }
    }
}

@Composable
fun chatRoom(chatRoom: DomainChatRoomWithUser) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp).clickable {
//            context.startActivity(Intent(context, ChatRoomActivity::class.java).putExtra(
//                "artwork", Gson().toJson(chatRoom.chatRoom.ar, DomainArtwork::class.java)
//            ))
        }
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(80.dp)
                .background(color = colorResource(id = R.color.lightgray)),
            contentAlignment = Alignment.Center,
        ){
//            AsyncImage(
//                model = chatRoom.chatRoom.artwork.url,
//                contentDescription = null,
//                contentScale = ContentScale.Crop
//            )
        }
        Column(modifier = Modifier.padding(start = 10.dp, top = 5.dp)) {
            Row(modifier = Modifier.padding(bottom = 5.dp)) {
                Text(text = chatRoom.destUser.name, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Text(chatRoom.chatRoom.lastMessage.timestamp, color = Color.Gray, fontSize = 15.sp)
            }
            Text(text = chatRoom.chatRoom.lastMessage.message, color = Color.Black, fontSize = 15.sp)
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
