package com.example.presentation.viewModel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.presentation.view.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

class LoginViewModel(var application: LoginActivity): ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    var storedVerificationId = ""
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    var signInRespond = mutableStateOf(false)

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        signInRespond.value = true
        auth.signInWithCredential(credential)
            .addOnCompleteListener(application) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Log.d("sms", "signInWithCredential:success + $user")
                    signInRespond.value = false
                    application.finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("sms", "signInWithCredential:failure", task.exception)
                    signInRespond.value = false
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(application, "인증코드가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
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
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("sms", "onVerificationCompleted:$credential")
            Toast.makeText(application, "인증코드가 전송되었습니다. 90초 이내에 입력해주세요.", Toast.LENGTH_SHORT).show()
            signInWithPhoneAuthCredential(credential)
        }

        //인증번호 실패 상태
        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
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
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("sms", "onCodeSent:$verificationId" + "::::$token")

//            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }


}