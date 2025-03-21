package com.bytebandits.krishiaayog

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.bytebandits.krishiaayog.DataClass.SignUpRequest
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.custombblack
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.viewmodel.HomeScreenViewModel
import com.bytebandits.krishiaayog.viewmodel.SignUpViewModel

@Composable
fun SignupPage(navController: NavController, SignupViewModel: SignUpViewModel) {

    val signupResult = SignupViewModel.signupResult.value
    val ownername = remember { mutableStateOf("") }
    val ph = remember { mutableStateOf("") }
    val user = remember { mutableStateOf("") }
    val pass = remember { mutableStateOf("") }
    val context = LocalContext.current


    Box(modifier = Modifier.fillMaxSize()){
        Image(modifier = Modifier.fillMaxSize(), painter = painterResource(id = R.drawable.loginpagepicture), contentDescription = "Login page background", contentScale = ContentScale.Crop)
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth().offset(y= 120.dp)) {
            Text("KrishiAayog",fontFamily = Lufga, fontWeight = FontWeight.SemiBold, fontSize = 42.sp, color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))// Takes up the bottom half of the screen
                .background(Color.White)
                .align(Alignment.BottomCenter)

        )
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top =20.dp, bottom = 10.dp)) {


        val ( name, phone, username, password, signupbutton) = createRefs()

        TextField(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp)
                .constrainAs(name) {
                    bottom.linkTo(phone.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    centerHorizontallyTo(parent)
                },
            value = ownername.value,
            onValueChange = { ownername.value = it },  // Update username state
            label = { Text("Name") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedLabelColor = Color.Black, unfocusedContainerColor = greenColor, focusedContainerColor = greenColor),
            shape = RoundedCornerShape(10.dp)
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp)
                .constrainAs(phone) {
                    bottom.linkTo(username.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    centerHorizontallyTo(parent)
                },
            value = ph.value,
            onValueChange = { ph.value = it },  // Update username state
            label = { Text("Phone") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Phone
            ),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedLabelColor = Color.Black, unfocusedContainerColor = greenColor, focusedContainerColor = greenColor),
            shape = RoundedCornerShape(10.dp),

        )

        TextField(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp)
                .constrainAs(username) {
                    bottom.linkTo(password.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    centerHorizontallyTo(parent)
                },
            value = user.value,
            onValueChange = { user.value = it },  // Update username state
            label = { Text("Username") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedLabelColor = Color.Black, unfocusedContainerColor = greenColor, focusedContainerColor = greenColor),
            shape = RoundedCornerShape(10.dp)
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp)
                .constrainAs(password) {
                    bottom.linkTo(signupbutton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    centerHorizontallyTo(parent)
                },
            value = pass.value,
            onValueChange = { pass.value = it },  // Update password state
            label = { Text("Password") },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedLabelColor = Color.Black, unfocusedContainerColor = greenColor, focusedContainerColor = greenColor),
            visualTransformation = PasswordVisualTransformation(), // To hide password text
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Handle the Done action, e.g. submit or focus change */ }
            ),
            shape = RoundedCornerShape(10.dp)

        )


        Button(modifier = Modifier.fillMaxWidth(0.8f).padding(10.dp).constrainAs(signupbutton){
            bottom.linkTo(parent.bottom, margin = 80.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            centerHorizontallyTo(parent)
        }, colors = ButtonDefaults.buttonColors(custombblack),
            onClick = {
                val user = SignUpRequest(ownername.value, ph.value, user.value, pass.value)
                SignupViewModel.signup(user, context, navController)
        }) {
            Text("Signup", fontFamily = Lufga, fontSize = 24.sp, color = Color.White)
        }
    }

    signupResult?.let { result ->
        if (result != "Success") {
            Text(text = result, color = Color.Red)
        }
    }

}


