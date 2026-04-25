package ac.nan.camerataging

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileInputStream

class PreviewActivity : AppCompatActivity() {

    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val imageView = findViewById<ImageView>(R.id.imgPreview)

        filePath = intent.getStringExtra("path")!!

//        val bitmap = BitmapFactory.decodeFile(filePath)
//        imageView.setImageBitmap(bitmap)

        val bitmap = BitmapFactory.decodeFile(filePath)

        val rotatedBitmap = fixImageRotation(bitmap, filePath)

        Glide.with(this)
            .load(File(filePath))
            .into(imageView)
//        imageView.setImageBitmap(rotatedBitmap)

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

    private fun fixImageRotation(bitmap: Bitmap, path: String): Bitmap {

        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        }

        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height,
            matrix, true
        )
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