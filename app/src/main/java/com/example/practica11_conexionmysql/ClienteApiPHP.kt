package com.example.practica11_conexionmysql

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// Controlador para conectar con la base de datos MySQL en el servidor web (Vehículos Deportivos)
class ClienteApiPHP {
    // IP del servidor web (actualizada por el usuario)
    var ipServidor: String = "192.168.100.17"

    // URL base apuntando a la carpeta del proyecto en htdocs de XAMPP
    private val baseUrl: String
        get() = "http://$ipServidor/megatronsports_practica11_movilesII"

    /**
     * Función genérica para enviar peticiones HTTP POST con parámetros en el cuerpo (Body).
     */
    private suspend fun realizarPeticionPost(urlPath: String, parametros: Map<String, String>): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlPath)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "POST"
            conexion.doOutput = true
            conexion.connectTimeout = 5000
            conexion.readTimeout = 5000

            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val datosCuerpo = parametros.map { (clave, valor) ->
                "${URLEncoder.encode(clave, "UTF-8")}=${URLEncoder.encode(valor, "UTF-8")}"
            }.joinToString("&")

            conexion.outputStream.use { os ->
                os.write(datosCuerpo.toByteArray(Charsets.UTF_8))
            }

            if (conexion.responseCode == HttpURLConnection.HTTP_OK) {
                val respuesta = conexion.inputStream.bufferedReader().use { it.readText() }
                conexion.disconnect()
                respuesta
            } else {
                conexion.disconnect()
                "Error: ${conexion.responseCode}"
            }
        } catch (e: Exception) {
            "Error de red: ${e.message}"
        }
    }

    // Registrar Vehículo
    suspend fun registrarVehiculo(vehiculo: Vehiculo): String {
        val url = "$baseUrl/registrar_post.php"
        val parametros = mapOf(
            "id_vehiculo" to vehiculo.idVehiculo.toString(),
            "marca" to vehiculo.marca,
            "modelo" to vehiculo.modelo,
            "anio" to vehiculo.anio.toString(),
            "precio" to vehiculo.precio.toString(),
            "caballos_de_fuerza" to vehiculo.caballosDeFuerza.toString()
        )
        return realizarPeticionPost(url, parametros)
    }

    // Buscar Vehículo
    suspend fun buscarVehiculo(idVehiculo: Int): Vehiculo? {
        val url = "$baseUrl/buscar_post.php"
        val parametros = mapOf("id_vehiculo" to idVehiculo.toString())
        val respuesta = realizarPeticionPost(url, parametros)
        return try {
            if (respuesta != "No encontrado" && !respuesta.startsWith("Error")) {
                val json = JSONObject(respuesta)
                Vehiculo(
                    idVehiculo = json.getInt("id_vehiculo"),
                    marca = json.getString("marca"),
                    modelo = json.getString("modelo"),
                    anio = json.getInt("anio"),
                    precio = json.getDouble("precio"),
                    caballosDeFuerza = json.getInt("caballos_de_fuerza")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar Vehículo
    suspend fun actualizarVehiculo(vehiculo: Vehiculo): String {
        val url = "$baseUrl/actualizar_post.php"
        val parametros = mapOf(
            "id_vehiculo" to vehiculo.idVehiculo.toString(),
            "marca" to vehiculo.marca,
            "modelo" to vehiculo.modelo,
            "anio" to vehiculo.anio.toString(),
            "precio" to vehiculo.precio.toString(),
            "caballos_de_fuerza" to vehiculo.caballosDeFuerza.toString()
        )
        return realizarPeticionPost(url, parametros)
    }

    // Eliminar Vehículo
    suspend fun eliminarVehiculo(idVehiculo: Int): String {
        val url = "$baseUrl/eliminar_post.php"
        val parametros = mapOf("id_vehiculo" to idVehiculo.toString())
        return realizarPeticionPost(url, parametros)
    }

    // Listar Vehículos
    suspend fun obtenerVehiculos(): List<Vehiculo> {
        val url = "$baseUrl/listar.php"
        val respuesta = realizarPeticionPost(url, emptyMap())
        val lista = mutableListOf<Vehiculo>()
        try {
            if (!respuesta.startsWith("Error")) {
                val jsonArray = JSONArray(respuesta)
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
                    lista.add(
                        Vehiculo(
                            idVehiculo = json.getInt("id_vehiculo"),
                            marca = json.getString("marca"),
                            modelo = json.getString("modelo"),
                            anio = json.getInt("anio"),
                            precio = json.getDouble("precio"),
                            caballosDeFuerza = json.getInt("caballos_de_fuerza")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lista
    }
}
