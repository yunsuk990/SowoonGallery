package com.yschoi.presentation.view.Setting

import android.app.Activity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yschoi.domain.model.DomainUser
import com.yschoi.presentation.R
import com.yschoi.presentation.model.UploadState
import com.yschoi.presentation.utils.SignOutToastMessage
import com.yschoi.presentation.view.ui.theme.SowoonTheme
import com.yschoi.presentation.viewModel.AccountViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : ComponentActivity() {

    private val viewModel: AccountViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent.getStringExtra("userInfo")
        val userInfo = Gson().fromJson(intent, DomainUser::class.java)
        viewModel.userInfo(userInfo)

        setContent {
            val loginActivityLauncher = rememberLauncherForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
                finish()
            }
            SowoonTheme {
                val uploadState by viewModel.uploadState.collectAsState()
                AccountScreen(
                    userInfo = userInfo,
                    updateBtnOnClick = { domainUser ->
                        viewModel.updateUserProfile(domainUser)
                    },
                    signOut = {
                        viewModel.signOut()
                        finish()
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

@Composable
fun AccountScreen(
    userInfo: DomainUser,
    updateBtnOnClick: (DomainUser) -> Unit,
    signOut: () -> Unit
){

    var email by remember { mutableStateOf(userInfo.email) }
    var dialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
       AccountTopAppBar()
       Column(
           modifier = Modifier
               .fillMaxWidth()
               .padding(horizontal = 15.dp, vertical = 25.dp),
           verticalArrangement = Arrangement.spacedBy(15.dp)
       ) {
           textField(
               value = PhoneNumberUtils.formatNumber(userInfo.phoneNumber, "KR"),
               label = "전화번호",
               onValueChange = {},
               modifier = Modifier.fillMaxWidth(),
               enabled = false
           )
           Row(
               horizontalArrangement = Arrangement.spacedBy(10.dp),
               verticalAlignment = Alignment.CenterVertically
           ){
               textField(
                   value = email,
                   label = "이메일",
                   onValueChange = { email = it },
                   modifier = Modifier
                       .fillMaxWidth()
                       .weight(1f),
                   enabled = true
               )
               Button(
                   shape = RoundedCornerShape(8.dp),
                   onClick = {
                       updateBtnOnClick(
                           userInfo.copy(email = email)
                       )
                   },
                   enabled = userInfo.email != email,
                   colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                   modifier = Modifier,
                   contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp)
               ) { Text("변경하기") }
           }
           Spacer(modifier = Modifier.weight(1f))
           OutlinedButton(
               modifier = Modifier.fillMaxWidth(),
               colors = ButtonDefaults.buttonColors(
                   containerColor = Color.Black
               ),
               shape = RoundedCornerShape(30.dp),
               contentPadding = PaddingValues(vertical = 15.dp),
               onClick = {
                   dialogOpen = true
               },
           ) { Text("탈퇴하기", fontSize = 16.sp, color = Color.White) }
       }
        if(dialogOpen){
            SignOutToastMessage(
                dismissOnClick = { dialogOpen = false},
                confirmOnClick = {
                    signOut()
                    dialogOpen = false
                }
            )
        }
    }
}


@Composable
fun textField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier, enabled: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = {  onValueChange(it) },
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledLabelColor = Color.Black,
            disabledTextColor = Color.Black,
            disabledBorderColor = Color.Black,
            focusedContainerColor = Color.White,
            focusedLabelColor = Color.Black,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            unfocusedTextColor = Color.Black,
            unfocusedLabelColor = Color.Black,
        ),
        label = { Text(text = label) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopAppBar(){
    val context = LocalContext.current
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "계정 관리", style = MaterialTheme.typography.titleMedium) },
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview3() {
    SowoonTheme {
        AccountScreen(
            userInfo = DomainUser(phoneNumber = "01099016074"),
            updateBtnOnClick = { },
            signOut = {   }
        )
    }
}