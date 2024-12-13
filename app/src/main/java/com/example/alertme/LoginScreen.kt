package com.example.alertme

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavController) {
    val mContext = androidx.compose.ui.platform.LocalContext.current

    // Menyimpan nilai email dan password
    val emailState = remember { androidx.compose.runtime.mutableStateOf("") }
    val passwordState = remember { androidx.compose.runtime.mutableStateOf("") }

    // FirebaseAuth instance
    val auth = FirebaseAuth.getInstance()

    // Google Sign-In Client
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            mContext,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    // Launcher for Google Sign-In
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Toast.makeText(mContext, "Login berhasil: ${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") // Navigasi setelah login sukses
                    } else {
                        Toast.makeText(mContext, "Login gagal: ${signInTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(mContext, "Login dengan Google gagal.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk login dengan Google
    fun loginWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Fungsi untuk login menggunakan email dan password
    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mContext, "Login berhasil! Selamat datang ${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") // Navigasi setelah sukses
                } else {
                    Toast.makeText(mContext, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F1E4))
            .padding(22.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.mipmap.logo_foreground),
            contentDescription = "Login image",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Login to access amazing features!",
            fontSize = 17.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // BasicTextField untuk email
        BasicTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(21.dp))
                .padding(14.dp)
                .height(26.dp),
            decorationBox = { innerTextField ->
                if (emailState.value.isEmpty()) {
                    Text(
                        text = "Email Address",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(21.dp))

        // BasicTextField untuk password
        BasicTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(21.dp))
                .padding(14.dp)
                .height(26.dp),
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                if (passwordState.value.isEmpty()) {
                    Text(
                        text = "Password",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(21.dp))

        // Tombol LOGIN
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                when {
                    emailState.value.isEmpty() -> {
                        Toast.makeText(mContext, "Masukkan email terlebih dahulu.", Toast.LENGTH_LONG).show()
                    }
                    passwordState.value.isEmpty() -> {
                        Toast.makeText(mContext, "Masukkan password terlebih dahulu.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        loginUser(emailState.value, passwordState.value)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFFFF9800)
            )
        ) {
            Text(
                modifier = Modifier.padding(7.dp),
                text = "LOGIN",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Tombol Login dengan Google
        Button(
            onClick = { loginWithGoogle() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.mipmap.google_logo_foreground),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Login with Google", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account?",
                fontSize = 15.sp,
                color = Color.Black
            )
            TextButton(
                onClick = { navController.navigate("register") },
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(
                    text = "Sign up now",
                    fontSize = 15.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController() // Membuat NavController untuk preview
    LoginScreen(navController = navController)
}
