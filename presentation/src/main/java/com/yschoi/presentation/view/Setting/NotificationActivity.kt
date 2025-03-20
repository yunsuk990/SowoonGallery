package com.yschoi.presentation.view.Setting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.yschoi.presentation.R
import com.yschoi.presentation.utils.NotificationToastMessage
import com.yschoi.presentation.view.ui.theme.SowoonTheme

class NotificationActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SowoonTheme{
                Scaffold(
                    topBar = { notificationTopAppBar() }
                ){ padding ->
                    NotificationScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(padding),
                        confirmOnClick = {
                            val intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                intent.putExtra(
                                    Settings.EXTRA_APP_PACKAGE,
                                    getPackageName()
                                )
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                                intent.putExtra("app_package", getPackageName())
                                intent.putExtra("app_uid", applicationInfo.uid)
                            } else {
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.addCategory(Intent.CATEGORY_DEFAULT)
                                intent.setData(Uri.parse("package:" + getPackageName()))
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

fun checkPermission(context: Context): Boolean{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED){
            return true
        }else{
            return false
        }
    } else {
        return false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(modifier: Modifier, confirmOnClick: () -> Unit){
    val context = LocalContext.current
    var checked by remember { mutableStateOf(
        checkPermission(context)
    ) }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = "알림", fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(start = 15.dp, top = 30.dp))
        HorizontalDivider(
            thickness = 15.dp,
            color = colorResource(R.color.lightwhite),
            modifier = Modifier.padding(top = 15.dp)
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("채팅 알림", fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    showDialog = true
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    uncheckedBorderColor = Color.Transparent,
                    checkedTrackColor = colorResource(R.color.soldOut)
                ),
                thumbContent = {
                    Box(modifier = Modifier.size(SwitchDefaults.IconSize))
                }
            )
        }

        if(showDialog){
            NotificationToastMessage(
                dismissOnClick = {
                    showDialog = false
                },
                confirmOnClick = {
                    confirmOnClick()
                    showDialog = false
                }
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun notificationTopAppBar(){
    val context = LocalContext.current
    Column {
        CenterAlignedTopAppBar(
            title = { Text( text = "알림 설정", style = MaterialTheme.typography.titleMedium) },
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


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    SowoonTheme{
        Scaffold(
            topBar = { notificationTopAppBar() }
        ){ padding ->
            NotificationScreen(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
                confirmOnClick = {}
            )
        }
    }
}