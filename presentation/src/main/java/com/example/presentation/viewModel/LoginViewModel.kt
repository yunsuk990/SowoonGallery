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
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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


    private val _storedVerificationId =  MutableStateFlow<String>("")
    val storedVerificationId: StateFlow<String> = _storedVerificationId

    private val _resendToken =  MutableStateFlow<PhoneAuthProvider.ForceResendingToken?>(null)
    val resendToken: StateFlow<PhoneAuthProvider.ForceResendingToken?> = _resendToken

    //최종가입 성공 유무
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


    //DB 가입 처리
    fun saveUserInfoRTD(domainUser: DomainUser){
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            delay(1500)
            val response = saveUserInfoUseCase.execute(domainUser)
            when(response){
                is Response.Success -> { _authState.value = AuthState.Authenticated }
                is Response.Error -> { _authState.value = AuthState.Error("가입 실패") }
            }

        }
    }

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
                is Response.Error -> {
                    Log.d("signInWithPhoneAuthCredential", "Error: ${response.message}, ${response.exception}")
                    if(response.exception is FirebaseAuthInvalidCredentialsException){
                        _authState.value = AuthState.Error(message =  "휴대전화 번호 인증에 실패했어요. 다시 입력해주세요.")
                    }else{
                        _authState.value = AuthState.Error("네트워크 에러입니다. 잠시 후 다시 시도해주세요.")
                    }
                }
            }
        }
    }



    // 전화번호 인증코드 요청
    fun verifyPhoneNumber(phoneNumber: String, activity: Activity){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 전화번호 인증코드 요청
    fun resendVerifyPhoneNumber(phoneNumber: String,  activity: Activity){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if(resendToken.value!= null){
            options.setForceResendingToken(resendToken.value!!)
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
                _authState.value = AuthState.Error(message =  "휴대전화 번호 인증에 실패했어요. 다시 입력해주세요.")
            } else if (e is FirebaseTooManyRequestsException) {
                _authState.value = AuthState.Error(message =  "인증번호 요청 횟수를 초과했어요. 잠시 후 다시 시도해주세요.")
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }else{
                _authState.value = AuthState.Error(message =  e.message.toString())
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d("sms", "onCodeSent:$verificationId" + "::::$token")

            _storedVerificationId.value = verificationId
            _resendToken.value = token
        }
    }

    fun resetAuthState() {
        _authState.value = null // 또는 기본 상태로 초기화
    }
}