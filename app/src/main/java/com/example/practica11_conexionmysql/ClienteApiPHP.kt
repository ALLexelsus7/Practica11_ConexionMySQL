package com.example.practica11_conexionmysql

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

//Controlador para conectar con la base de datos en el server MySQL y definicion de las funciones
//para cada operacion del CRUD
class ClienteApiPHP {
    // IP de servidor MYSQL (Aqui debo de colocar la que corresponda a mi servidor web)
    //En realidad aqui puse la IP actual de mi PC
    var ipServidor: String = "192.168.100.17"
    //definicion de la URL con el nombre de la empresa
    private val baseUrl: String
        get() = "http://$ipServidor/megatronsports_practica11_movilesII"

    /**
     * Función genérica para enviar peticiones HTTP POST con parámetros en el
    cuerpo (Body).
     */
    private suspend fun realizarPeticionPost(urlPath: String, parametros:
    Map<String, String>): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlPath)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "POST"
            conexion.doOutput = true // Habilita la conexión para enviar datos en el cuerpo
                    conexion.connectTimeout = 5000
            conexion.readTimeout = 5000

            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            // Formatear los parámetros en formato clave1=valor1&clave2=valor2
            val datosCuerpo = parametros.map { (clave, valor) ->

            "${URLEncoder.encode(clave, "UTF-8")}=${URLEncoder.encode(valor, "UTF-8")}"

            }.joinToString("&")

            // Escribir los datos en el cuerpo de la petición HTTP
            conexion.outputStream.use { os ->
                os.write(datosCuerpo.toByteArray(Charsets.UTF_8))
            }

            // Lectura de la respuesta del servidor
            if (conexion.responseCode == HttpURLConnection.HTTP_OK) {
                val respuesta = conexion.inputStream.bufferedReader().use {
                    it.readText() }
                conexion.disconnect()
                respuesta
            } else {
                conexion.disconnect()
                "Error: ${conexion.responseCode}"
            }
            } catch (e: Exception) {
                "Error de red: ${e.message}"
            }
        }//realizarPeticionPost

    //⁜⁜⁜Funcion para registrar empleados⁜⁜⁜
    suspend fun registrarEmpleado(empleado: Empleado): String {
        //Instrucciones para uso de metodo POST
        val url = "$baseUrl/registrar_post.php"
        val parametros = mapOf(
            "numemp" to empleado.numEmp.toString(),
            "nombre" to empleado.nombre,
            "apellidos" to empleado.apellidos,
            "sueldo" to empleado.sueldo.toString()
        )
        return realizarPeticionPost(url, parametros)
    }

    //⁜⁜⁜Funcion para buscar empleados⁜⁜⁜
    suspend fun buscarEmpleado(numEmp: Int): Empleado? {
        //Instrucciones para uso de metodo POST
        val url = "$baseUrl/buscar_post.php"
        val parametros = mapOf("numemp" to numEmp.toString())
        val respuesta = realizarPeticionPost(url, parametros)
        return try {
            if (respuesta != "No encontrado" && !respuesta.startsWith("Error")) {
                val json = JSONObject(respuesta)
                Empleado(
                    numEmp = json.getInt("numemp"),
                    nombre = json.getString("nombre"),
                    apellidos = json.getString("apellidos"),
                    sueldo = json.getDouble("sueldo")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    //⁜⁜⁜Funcion para actualizar empleados⁜⁜⁜
    suspend fun actualizarEmpleado(empleado: Empleado): String {
        //Instrucciones para uso de metodo POST
        val url = "$baseUrl/actualizar_post.php"
        val parametros = mapOf(
            "numemp" to empleado.numEmp.toString(),
            "nombre" to empleado.nombre,
            "apellidos" to empleado.apellidos,
            "sueldo" to empleado.sueldo.toString()
        )
        return realizarPeticionPost(url, parametros)
    }

    //⁜⁜⁜Funcion para eliminar empleados⁜⁜⁜
    suspend fun eliminarEmpleado(numEmp: Int): String {
        //Instrucciones para uso de metodo POST
        val url = "$baseUrl/eliminar_post.php"
        val parametros = mapOf("numemp" to numEmp.toString())
        return realizarPeticionPost(url, parametros)
    }

    //⁜⁜⁜Funcion para mostrar empleados⁜⁜⁜
    suspend fun obtenerEmpleados(): List<Empleado> {
        //Instrucciones para uso de metodo POST
        val url = "$baseUrl/listar.php"
        // Para listar podemos enviar un mapa vacío
        val respuesta = realizarPeticionPost(url, emptyMap())
        val lista = mutableListOf<Empleado>()
        try {
            if (!respuesta.startsWith("Error")) {
                val jsonArray = JSONArray(respuesta)
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
                    lista.add(
                        Empleado(
                            numEmp = json.getInt("numemp"),
                            nombre = json.getString("nombre"),
                            apellidos = json.getString("apellidos"),
                            sueldo = json.getDouble("sueldo")
                        )
                    )
                }//for
            }//if
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lista
    }
}