package com.bytebandits.krishiaayog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.custombblack
import com.bytebandits.krishiaayog.ui.theme.greenColor

@Composable
fun StartPage(navController: NavController) {

    Box(modifier = Modifier.fillMaxSize()){
        Image(modifier = Modifier.fillMaxSize(), painter = painterResource(id = R.drawable.loginpagepicture), contentDescription = "Login page background", contentScale = ContentScale.Crop)
    Column(modifier = Modifier.padding(20.dp).fillMaxWidth().offset(y= 120.dp)) {
        Text("KrishiAayog",fontFamily = Lufga, fontWeight = FontWeight.SemiBold, fontSize = 42.sp, color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center )
    }
    }



    ConstraintLayout(modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top =20.dp, bottom = 10.dp)) {


        val (loginbutton, signupbutton) = createRefs()

        Button(modifier = Modifier.fillMaxWidth(0.8f).padding(10.dp).constrainAs(loginbutton){
            bottom.linkTo(signupbutton.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            centerHorizontallyTo(parent)
        }, colors = ButtonDefaults.buttonColors(greenColor), onClick = { navController.navigate("login") }) {
            Text("Login", fontFamily = Lufga, fontSize = 24.sp, color = Color.Black)
        }
        Button(modifier = Modifier.fillMaxWidth(0.8f).padding(10.dp).constrainAs(signupbutton){
            bottom.linkTo(parent.bottom, margin = 80.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            centerHorizontallyTo(parent)
        }, colors = ButtonDefaults.buttonColors(custombblack), onClick = { navController.navigate("signup") }) {
            Text("Signup", fontFamily = Lufga, fontSize = 24.sp, color = Color.White)
        }
    }


}