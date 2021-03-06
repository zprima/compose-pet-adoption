package com.primalabs.devchallengepets

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.primalabs.devchallengepets.ui.theme.DevChallengePetsTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.imageloading.ImageLoadState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight

data class Pet(val name: String, val pictureUrl: String)

val pets = listOf<Pet>(
    Pet("Possum", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/50221514/1/?bust=1610039327&width=720"),
    Pet("Miss Mew", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/50221008/1/?bust=1610038901&width=720"),
    Pet("Charles", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/49101370/1/?bust=1600186781&width=720"),
    Pet("Sam", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48485776/1/?bust=1594745287&width=720"),
    Pet("Happy Angus", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48773026/1/?bust=1597368870&width=720"),
    Pet("Roxy", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/50492773/1/?bust=1612817710&width=720"),
    Pet("Pfizer", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/49286012/2/?bust=1601497707&width=720"),
    Pet("Chaplin", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/49210636/2/?bust=1600977428&width=720"),
    Pet("Harold", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48897639/1/?bust=1603892016&width=720"),
    Pet("Luna", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48702106/3/?bust=1596727167&width=720"),
    Pet("Oma", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48627994/1/?bust=1596038792&width=720"),
    Pet("Sydney", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48627874/1/?bust=1596037836&width=720"),
    Pet("Martin and Earl", "https://dl5zpyw5k3jeb.cloudfront.net/photos/pets/48308787/6/?bust=1593019090&width=720")
)


@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun App(){
    val navController = rememberNavController()

    DevChallengePetsTheme(darkTheme = false) {
        NavHost(navController, startDestination = "petlistpage") {
            composable("petlistpage") { PetListsPage(navController) }
            composable(
                route = "petdetailpage/{petIndex}",
                arguments = listOf(navArgument("petIndex") { type = NavType.StringType })) { backStackEntry ->

                PetDetailPage(navController, backStackEntry.arguments?.getString("petIndex"))
            }
        }
    }
}

@Composable
fun PetTopBar(){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color = Color(android.graphics.Color.parseColor("#FFf1f1f1")))
                    .padding(5.dp)
                    ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.Place, contentDescription = "Place")
                Text(text = "New York, NY, USA", fontSize = 12.sp)
            }
        },
        actions = {
            Image(
                painter = painterResource(R.drawable.profilepic1),
                contentDescription = "Profile pic",
                modifier = Modifier
                    .padding(8.dp)
                    .clip(shape = CircleShape)
            )
        },
        backgroundColor = Color.White,
        elevation = 0.dp,
    )
}

@ExperimentalMaterialApi
@Composable
fun PetListsPage(navController: NavController){
    Scaffold(
        topBar = {
            PetTopBar()
        },
        content = {
            PetsList(navController = navController)
        }
    )
}

@Composable
fun DoubleColumn(
    modifier: Modifier,
    content: @Composable () -> Unit
){
    val state: ScrollState = rememberScrollState()

    Layout(modifier = modifier.verticalScroll(state = state), content = content, measurePolicy = { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints.copy(minHeight = 0))
        }

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0
            var x = 0

            // Place children in the parent layout
            placeables.forEachIndexed { index, placeable ->
                // Position item on the screen
                if(index % 2 != 0) {
                    x = 260 * density.toInt()
                } else{
                    x = 0
                }

                if (index == 1){
                    yPosition = 100 * density.toInt()
                }
                else if(index % 2 != 0){
                    // right column
                    yPosition = index * 150 * density.toInt() - 100
                } else {
                    // left column
                    yPosition = index * 150 * density.toInt()
                }



                Log.d("cat", "$index, $x, $yPosition")

                placeable.place(x = x, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    })
}

@Composable
fun CatCard(navController: NavController, index: Int, pet: Pet, modifier: Modifier){
    Surface(
        modifier = modifier
            .width(200.dp)
            .height(240.dp)
            .padding(18.dp),
        shape = RoundedCornerShape(25.dp)
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomStart
        ) {
            CoilImage(
                data = pet.pictureUrl,
                contentDescription = "Pet image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
            )

            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.DarkGray),
                            startY = 210f
                        )
                    )
                    .fillMaxSize()
            ) {}

            Column(modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)) {
                Text(
                    text = pet.name,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Cat", fontSize = 12.sp, color = Color.White)
                Text(text = "1 year old", fontSize = 12.sp, color = Color.White)
            }

        }
    }
}

@ExperimentalMaterialApi
@Composable
fun PetsList(navController: NavController){


    DoubleColumn(
        modifier = Modifier,
        content = {
            pets.forEachIndexed { index, pet ->
                CatCard(navController = navController, index =index, pet =pet, modifier = Modifier.clickable { navController.navigate("petdetailpage/$index") })
            }
        }
    )




//    DoubleColumn(modifier = Modifier, content = {
//        LazyColumn(
//            modifier = Modifier,
//            content = {
//                itemsIndexed(pets) { index, pet ->
//                    CatCard(navController, index, pet)
//                }
//            }
//        )
//    })
}

@Composable
fun OG(){
    LazyColumn(
        modifier = Modifier,
        content = {
            itemsIndexed(pets) { index, pet ->
                Surface(
                    modifier = Modifier
                        .width(200.dp)
                        .height(240.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.BottomStart
                    ) {
                        CoilImage(
                            data = pet.pictureUrl,
                            contentDescription = "Pet image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                        )

                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.DarkGray),
                                        startY = 210f
                                    )
                                )
                                .fillMaxSize()
                        ) {}

                        Column(modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)) {
                            Text(
                                text = pet.name,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Cat", fontSize = 12.sp, color = Color.White)
                            Text(text = "1 year old", fontSize = 12.sp, color = Color.White)
                        }

                    }
                }
            }
        }
    )
}

@Composable
fun PetDetailPage(navController: NavController, petIndex: String?){
    val pet = pets[petIndex!!.toInt()]

    Scaffold(
        topBar = {
             TopAppBar(
                 title = { Text(text = "Pet Detail page")},
                 navigationIcon = {
                     IconButton(
                         onClick = { navController.popBackStack() },
                         content = {
                             Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                         }
                     )
                 }
             )
        },
        content = {
            Column() {
                CoilImage(
                    data = pet.pictureUrl,
                    contentDescription = "My content description",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    error = { error: ImageLoadState.Error ->
                        Text(text = error.toString())
                    },
                    contentScale = ContentScale.Crop
                )
                Text(text = pet.name, style = MaterialTheme.typography.h4, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), textAlign = TextAlign.Center)
            }
        }
    )
}