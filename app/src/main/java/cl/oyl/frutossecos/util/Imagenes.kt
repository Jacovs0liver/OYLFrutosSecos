package cl.oyl.frutossecos.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val CARPETA_FOTOS = "OYLFrutosSecos"

fun Context.crearArchivoTemporal(nombre: String): File {
    val carpeta = File(cacheDir, "fotos")
    carpeta.mkdirs()
    return File(carpeta, nombre)
}

fun Context.uriCompartible(archivo: File): Uri =

    FileProvider.getUriForFile(this, "$packageName.fileprovider", archivo)

fun Context.guardarEnGaleria(archivo: File): Uri? {

    val fecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val nombre = "FOTO_$fecha.jpg"

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            guardarConMediaStore(archivo, nombre)
        } else {
            guardarEnCarpetaPublica(archivo, nombre)
        }
    } catch (e: Exception) {
        null
    }
}

private fun Context.guardarConMediaStore(archivo: File, nombre: String): Uri? {

    val valores = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$CARPETA_FOTOS")

        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
        ?: return null

    val salida = contentResolver.openOutputStream(uri)
    if (salida == null) {
        contentResolver.delete(uri, null, null)
        return null
    }
    salida.use { flujoSalida ->
        archivo.inputStream().use { flujoEntrada -> flujoEntrada.copyTo(flujoSalida) }
    }

    valores.clear()
    valores.put(MediaStore.Images.Media.IS_PENDING, 0)
    contentResolver.update(uri, valores, null, null)
    return uri
}

@Suppress("DEPRECATION")
private fun Context.guardarEnCarpetaPublica(archivo: File, nombre: String): Uri? {
    val carpetaImagenes = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val carpeta = File(carpetaImagenes, CARPETA_FOTOS)
    carpeta.mkdirs()

    val destino = File(carpeta, nombre)
    archivo.copyTo(destino, overwrite = true)

    val valores = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATA, destino.absolutePath)
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
}

fun Context.copiarACache(uri: Uri, nombre: String): File? = try {
    val destino = crearArchivoTemporal(nombre)
    contentResolver.openInputStream(uri)?.use { entrada ->
        destino.outputStream().use { salida -> entrada.copyTo(salida) }
    }
    destino
} catch (e: Exception) {
    null
}

fun Context.cargarImagenReducida(uri: Uri, ladoMaximo: Int): Bitmap? = try {

    val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }

    if (medidas.outWidth <= 0 || medidas.outHeight <= 0) {
        null
    } else {

        var reduccion = 1
        while (medidas.outWidth / (reduccion * 2) >= ladoMaximo &&
            medidas.outHeight / (reduccion * 2) >= ladoMaximo
        ) {
            reduccion *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
        val imagen = contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        }

        imagen?.let { girarSegunExif(uri, it) }
    }
} catch (e: Exception) {
    null
}

private fun Context.girarSegunExif(uri: Uri, imagen: Bitmap): Bitmap {
    val orientacion = contentResolver.openInputStream(uri)?.use {
        ExifInterface(it).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL,
        )
    } ?: ExifInterface.ORIENTATION_NORMAL

    val grados = when (orientacion) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    if (grados == 0f) return imagen

    val matriz = Matrix().apply { postRotate(grados) }
    return Bitmap.createBitmap(imagen, 0, 0, imagen.width, imagen.height, matriz, true)
}
