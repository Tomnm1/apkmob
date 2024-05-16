package com.edu.apkmob


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapp.MyDBHandler
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var dbHandler: MyDBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = MyDBHandler(this, null, null, 1)
        setContent {
            val timerViewModel: TimerViewModel by viewModels()
            MainScreen(dbHandler)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(dbHandler: MyDBHandler) {
    var trails by remember { mutableStateOf(emptyList<Trail>()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            trails = dbHandler.loadTrailsFromDB()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trails") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Tutaj należy dodać kod do uruchamiania aparatu
                    Toast.makeText(context, "Tutaj powinien odpalić się aparat", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircle,
                    contentDescription = "Odpal aparat"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            TrailGrid(trails = trails)
        }
    }
}

@Composable
fun TrailGrid(trails: List<Trail>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp)
    ) {
        items(trails) { trail ->
            TrailItem(trail = trail)
        }
    }
}

@Composable
fun TrailItem(trail: Trail) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val intent = Intent(context, Details::class.java)
                intent.putExtra("trail_id", trail.id)
                context.startActivity(intent)
            }
    ) {
        val imageBitmap = loadImageFromAssets(context, "images/${trail.id}.webp")
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = trail.name,
                modifier = Modifier
                    .requiredSize(180.dp)
                    .padding(2.dp)
            )
        }
        Text(text = trail.name, modifier = Modifier.padding(8.dp))
    }
}

fun loadImageFromAssets(context: Context, filePath: String): android.graphics.Bitmap? {
    return try {
        context.assets.open(filePath).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


/* funckje odpowiedzialne za obsługę galerii/aparatu itp zapożyczone :)))) TODO USUŃ TEN KOMENTARZ XD
private fun takePhoto(){
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageDirectory = "$filesDir/Images/$id/photos"
        val fileName = "$imageDirectory/$timeStamp.jpg"
        //val fileName = "$imageDirectory/a.jpg"
        Log.d("myApp", "$fileName");
        file = File(fileName)
        val tempImageUri: Uri = initTempUri()
        resultLauncher.launch(tempImageUri)
        Log.d("myApp", "pstryk");
    }

    private fun addPhotoFromGallery(){
        val imageDirectory = File("$filesDir/Images/$id/photos")
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "$imageDirectory/$timeStamp.jpg"
        //val fileName = "$imageDirectory/a.jpg"
        //Log.d("myApp", "$fileName");
        file = File(fileName)
        //mGetContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        openGallery()
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            //img.setImageBitmap(bitmap)

            saveImage(bitmap)
        }
    }
    private fun saveImage(bitmap: Bitmap) {
        try {
            FileOutputStream(file).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                refreshGallery()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    private fun refreshGallery() {
        imageGallery.removeAllViews()
        /*
        val imgPlus: ImageView = ImageView(this)
        imgPlus.setImageResource(R.mipmap.plus)
        imgPlus.setOnClickListener {
            addImage(it)
        }
        imageGallery.addView(imgPlus)
        imgPlus.minimumWidth = 128
        imgPlus.minimumHeight = 128
        */
        //val fileName = "${imageDirectory}mini.jpg"
        Log.d("myApp", "here");
        File("$filesDir/Images/$id/photos").walk().forEach {
            val indx = "$it".indexOf("mini.jpg", 0)
            val indxjpg = "$it".indexOf(".jpg", 0)
            if(indx < 0 && indxjpg > 0){
                val imgP: ImageView = ImageView(this)
                val uri: Uri? = Uri.parse("$it")
                imgP.setImageURI(uri)
                Log.d("myApp", "${imageGallery.width} - ${imageGallery.height}");
                imageGallery.addView(imgP)
                val param = imgP.layoutParams as ViewGroup.MarginLayoutParams
                //param.setMargins(0,10,0,10)
                //imgP.layoutParams = param
                //imgP.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f)
                //imgP.maxWidth = 128
                //imgP.maxHeight = 128
                //imgP.layoutParams.weig
                //Log.d("myApp", "${imageGallery.chi}")
                val f = it
                imgP.setOnLongClickListener {
                    delImage(f)
                    true
                }
                Log.d("myApp", "${imageGallery.measuredWidth} - ${imageGallery.measuredHeight}");
            }
            Log.d("myApp", "$it - $indx - $indxjpg");
            //val uri: Uri? = Uri.parse(fileName)
            //img.setImageURI(uri)
        }
    }
 */

