package com.example.presentation.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DomainUser
import com.example.domain.model.Response
import com.example.domain.usecase.authUseCase.CheckUserRtdbUseCase
import com.example.domain.usecase.authUseCase.SaveUserInfoUseCase
import com.example.domain.usecase.SignInWithPhoneUseCase
import com.example.presentation.model.AuthState
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val checkUserRtdbUseCase: CheckUserRtdbUseCase,
    private val signInWithPhoneUseCase: SignInWithPhoneUseCase
): ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    var storedVerificationId = ""
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    //최종가입 성공 유무
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


    //DB 가입 처리
    fun saveUserInfoRTD(uid: String, name: String, age: String){
        _authState.value = AuthState.Loading
        saveUserInfoUseCase.execute(DomainUser(uid,name,age.toInt()))
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("가입 실패")
            }
    }

//    //가입된 사용자인지 확인
//    fun checkUserRTD(uid: String){
//        checkUserRtdbUseCase.execute(uid).addOnSuccessListener { snapshot ->
//            //사용자 존재
//            if(snapshot.exists()){
//                _authState.value = AuthState.ExistUser
//            }else{
//                _authState.value = AuthState.NewUser(uid)
//            }
//        }.addOnFailureListener {
//            _authState.value = AuthState.Error("Database Error")
//        }
//    }

    //인증 버튼 클릭 시 (로그인 처리)
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            // true -> 기존사용자, false -> 새 사용자
            val response = signInWithPhoneUseCase.execute(credential)
            when(response){
                is Response.Success -> {
                    if(response.data != null){
                        _authState.value = AuthState.NewUser(response.data!!)
                    }else{
                        _authState.value = AuthState.ExistUser
                    }
                }
                is Response.Error -> { _authState.value = AuthState.Error("인증코드 오류") }
            }
        }
    }



    // 전화번호 인증코드 요청
    fun verifyPhoneNumber(phoneNumber: String, activity: Activity){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 전화번호 인증코드 요청
    fun resendVerifyPhoneNumber(phoneNumber: String,  activity: Activity){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
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
            //Toast.makeText(application, "인증코드가 전송되었습니다. 90초 이내에 입력해주세요.", Toast.LENGTH_SHORT).show()
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
            //Toast.makeText(application, "전화번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
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