package com.example.presentation.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.Notification

@Composable
fun LoginToastMessage(dismissOnClick: () -> Unit, confirmOnClick: () -> Unit){
    Dialog(
        onDismissRequest = { dismissOnClick() }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(text = "로그인이 필요합니다. 로그인\n하시겠습니까?", modifier = Modifier.fillMaxWidth().padding(top = 15.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Row(
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    Button(
                        onClick = { dismissOnClick()},
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("취소", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { confirmOnClick() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogOutToastMessage(dismissOnClick: () -> Unit, confirmOnClick: () -> Unit){
    Dialog(
       onDismissRequest = { dismissOnClick() },
    ){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("로그아웃",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text("로그아웃 하겠습니까?", color = Color.Gray, fontSize = 14.sp)
                Row(
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        onClick = { dismissOnClick() }
                    ) {
                        Text("취소", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { confirmOnClick() }
                    ) {
                        Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SignOutToastMessage(dismissOnClick: () -> Unit, confirmOnClick: () -> Unit){
    Dialog(
        onDismissRequest = { dismissOnClick() },
    ){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("회원탈퇴",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text("탈퇴 버튼 선택 시 계정은 삭제되며\n복구되지 않습니다.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.padding(top = 25.dp))
                Text("정말로 탈퇴하시겠어요?", color = Color.Gray, fontSize = 14.sp)

                Row(
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        onClick = { dismissOnClick() }
                    ) {
                        Text("취소", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { confirmOnClick() }
                    ) {
                        Text("탈퇴", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun chatRoomExitToastMessage(dismissOnClick: () -> Unit, confirmOnClick: () -> Unit){
    Dialog(
        onDismissRequest = { dismissOnClick() },
    ){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("구매 확정하기",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text("나가기 버튼 선택 시 모든 대화기록이\n삭제됩니다.", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.padding(top = 25.dp))
                Text("채팅방을 나가시겠어요?", color = Color.Gray, fontSize = 14.sp)

                Row(
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        onClick = { dismissOnClick() }
                    ) {
                        Text("취소", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { confirmOnClick() }
                    ) {
                        Text("확정하기", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationToastMessage(dismissOnClick: () -> Unit, confirmOnClick: () -> Unit){
    Dialog(
        onDismissRequest = { dismissOnClick() }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(text = "알림 권한 변경이 필요해요. ", modifier = Modifier.fillMaxWidth().padding(top = 15.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("스마트폰 설정 > 애플리케이션 > 소운 > 알림", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                Row(
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    Button(
                        onClick = { dismissOnClick()},
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("취소", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { confirmOnClick() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Preview()
@Composable
fun testNotificationToastMessage(){
    NotificationToastMessage({}, {})
}

@Preview()
@Composable
fun testLoginToastMessage(){
    LoginToastMessage({}, {})
}

@Preview()
@Composable
fun testLogOutToastMessage(){
    LogOutToastMessage({}, {})
}

@Preview()
@Composable
fun testSignOutToastMessage(){
    SignOutToastMessage({}, {})
}

@Preview()
@Composable
fun testChatRoomExitToastMessage(){
    chatRoomExitToastMessage({}, {})
}