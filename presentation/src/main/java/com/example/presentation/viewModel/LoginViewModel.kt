package com.example.presentation.viewModel

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.presentation.model.AuthState
import com.example.presentation.model.User
import com.example.presentation.view.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.concurrent.TimeUnit

class LoginViewModel(var application: LoginActivity): ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase = Firebase.database

    var storedVerificationId = ""
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    //최종가입 성공 유무
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    //DB 가입 처리
    fun saveUserInfoRTD(uid: String, name: String, age: String){
        _authState.value = AuthState.Loading
        val userRef = database.getReference("users")
        userRef.child(uid).setValue(User(name, age.toInt()))
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("가입 실패")
            }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        _authState.value = AuthState.Loading
        auth.signInWithCredential(credential)
            .addOnCompleteListener(application) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Log.d("sms", "signInWithCredential:success + $user")
                    //사용자 가입 여부
                    if(user!=null){
                        checkUserRTD(user?.uid!!)
                    }
                } else {
                    Log.w("sms", "signInWithCredential:failure", task.exception)
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//
//                    }
                    _authState.value = AuthState.Error("인증코드 오류")
                }
            }.addOnFailureListener {
                _authState.value = AuthState.Error(it.toString())
            }
    }


    //가입된 사용자인지 확인
    fun checkUserRTD(uid: String){
        database.getReference("users").child(uid).get().addOnSuccessListener {snapshot ->
            //사용자 존재
            if(snapshot.exists()){
                Log.d("checkUserRTD", "SUCCESS")
                _authState.value = AuthState.ExistUser
            }else{
                _authState.value = AuthState.NewUser(uid)
            }
        }.addOnFailureListener {
            _authState.value = AuthState.Error("Database Error")
        }
    }


    // 전화번호 인증코드 요청
    fun verfiyPhoneNumber(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(application) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 전화번호 인증코드 요청
    fun resendVerifyPhoneNumber(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(application) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if(resendToken!= null){
            options.setForceResendingToken(resendToken!!)
        }
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //번호인증 혹은 기타 다른 인증이 끝난 상태
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("sms", "onVerificationCompleted:$credential")
            Toast.makeText(application, "인증코드가 전송되었습니다. 90초 이내에 입력해주세요.", Toast.LENGTH_SHORT).show()
//            storedCredential = credential
            //signInWithPhoneAuthCredential(credential)
        }

        //인증번호 실패 상태
        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("sms", "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }
            Toast.makeText(application, "전화번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d("sms", "onCodeSent:$verificationId" + "::::$token")
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    fun resetAuthState() {
        _authState.value = null // 또는 기본 상태로 초기화
    }


}