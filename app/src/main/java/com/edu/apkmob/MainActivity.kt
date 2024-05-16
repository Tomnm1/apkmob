package com.edu.apkmob


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.apkmob.ui.theme.ApkmobTheme
import com.example.myapp.MyDBHandler


class MainActivity : ComponentActivity() {
    private lateinit var dbHandler: MyDBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = MyDBHandler(this, null, null, 1)
        setContent {
            ApkmobTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityLayout(context = this, dbHandler = dbHandler)
                }
            }
        }
    }

    @Composable
    fun MainActivityLayout(context: Context, dbHandler: MyDBHandler) {
        var trails by remember { mutableStateOf(listOf<Trail>()) }

        LaunchedEffect(Unit) {
            trails = loadTrailsFromDB(dbHandler)
            Log.d("MainActivity", "Loaded ${trails.size} trails")
        }
        Box(modifier = Modifier.fillMaxSize()){
            if (trails.isEmpty()) {
                Text(
                    text = "No trails available.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(trails) { trail ->
                        TrailCard(trail = trail, context = context)
                    }
                }
            }

        }
    }

    @Composable
    fun TrailCard(trail: Trail, context: Context) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    val intent = Intent(context, Details::class.java).apply {
                        putExtra("trail_id", trail.id)
                    }
                    context.startActivity(intent)
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = trail.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp
                )
            }
        }
    }

    private suspend fun loadTrailsFromDB(dbHandler: MyDBHandler): List<Trail> {
        return dbHandler.loadTrailsFromDB()
    }
}

//FloatingActionButton(
//onClick = {
//    // Tutaj należy dodać kod do uruchamiania aparatu
//    Toast.makeText(context, "Tutaj powinien odpalić się aparat", Toast.LENGTH_SHORT).show()
//},
//modifier = Modifier
//.align(Alignment.BottomEnd)
//.padding(16.dp)
//) {
//    Icon(
//        imageVector = Icons.Outlined.AddCircle,
//        contentDescription = "Odpal aparat"
//    )
//}

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

