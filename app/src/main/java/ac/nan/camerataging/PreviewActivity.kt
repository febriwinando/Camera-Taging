package ac.nan.camerataging

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileInputStream

class PreviewActivity : AppCompatActivity() {

    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val imageView = findViewById<ImageView>(R.id.imgPreview)

        filePath = intent.getStringExtra("path")!!

        val bitmap = BitmapFactory.decodeFile(filePath)
        imageView.setImageBitmap(bitmap)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveToGallery()
        }

        findViewById<Button>(R.id.btnShare).setOnClickListener {
            shareImage()
        }

        findViewById<Button>(R.id.btnList).setOnClickListener {
            startActivity(Intent(this, ListFotoActivity::class.java))
        }
    }

    private fun saveToGallery() {
        val file = File(filePath)

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val outputStream = contentResolver.openOutputStream(uri!!)
        val inputStream = FileInputStream(file)

        inputStream.copyTo(outputStream!!)

        Toast.makeText(this, "Tersimpan di galeri", Toast.LENGTH_SHORT).show()
    }

    private fun shareImage() {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share"))
    }
}