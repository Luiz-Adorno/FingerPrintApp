package com.example.fingerprintapp

import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private var cancellationSignal:  CancellationSignal? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
    get() = @RequiresApi(Build.VERSION_CODES.P)
    object : BiometricPrompt.AuthenticationCallback(){
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            notifyUser("err is: $errString")
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            notifyUser("Success ")
            startActivity(Intent(this@MainActivity,MainActivity2::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.btn)

        btn.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Titulo")
                .setSubtitle("Subtitulo blablabla hehehe")
                .setDescription("O rato roeu a roupa do rei de roma")
                .setNegativeButton("botao1",this.mainExecutor, DialogInterface.OnClickListener { dialogInterface, i ->
                    notifyUser("cancel button")
                }).build()
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }

    private fun getCancellationSignal(): CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was canceled by the user")
        }
        return cancellationSignal as CancellationSignal

    }

    private fun checkBiometricSupport(): Boolean{
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if(!keyguardManager.isKeyguardSecure){
            notifyUser("FingerPrint Authentication is not been enabled in settings")
            return false
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("FingerPrint Authentication permission is not enabled")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true

    }

    private fun notifyUser(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}