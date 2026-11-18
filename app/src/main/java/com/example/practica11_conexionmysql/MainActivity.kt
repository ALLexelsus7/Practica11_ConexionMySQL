package com.example.practica11_conexionmysql

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.example.practica11_conexionmysql.ui.theme.Practica11_ConexionMySQLTheme

class MainActivity : ComponentActivity() {
    private val clienteApi = ClienteApiPHP()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica11_ConexionMySQLTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var currentScreen by remember { mutableStateOf("login") } // "login", "gestion", "listado"
                    var mostrarDialogoIP by remember { mutableStateOf(false) }
                    var ipConfigurada by remember { mutableStateOf(clienteApi.ipServidor) }

                    if (mostrarDialogoIP) {
                        DialogoConfiguracionIP(
                            ipActual = ipConfigurada,
                            onGuardarIP = { nuevaIP ->
                                clienteApi.ipServidor = nuevaIP
                                ipConfigurada = nuevaIP
                                mostrarDialogoIP = false
                            },
                            onDismiss = {
                                mostrarDialogoIP = false
                            }
                        )
                    }

                    when (currentScreen) {
                        "login" -> PantallaLogin(
                            onLoginSuccess = {
                                currentScreen = "gestion"
                            },
                            onConfigurarIpClick = {
                                mostrarDialogoIP = true
                            }
                        )
                        "gestion" -> PantallaGestionVehiculo(
                            clienteApi = clienteApi,
                            onIrAlListado = {
                                currentScreen = "listado"
                            },
                            onCerrarSesion = {
                                currentScreen = "login"
                            }
                        )
                        "listado" -> PantallaListadoVehiculo(
                            clienteApi = clienteApi,
                            onVolverAlFormulario = {
                                currentScreen = "gestion"
                            }
                        )
                    }
                }
            }
        }
    }
}

/*
En XAMPP, inicio Apache y MySQL.
Luego, en su Shell, ingreso con mysql -u root
y Hago los scripts:
1. CREATE DATABASE IF NOT EXISTS megatronsports_practica11_movilesII;
2. USE megatronsports_practica11_movilesII;
3. CREATE TABLE IF NOT EXISTS empleado (
numemp INT PRIMARY KEY,
nombre VARCHAR(100) NOT NULL,
apellidos VARCHAR(100) NOT NULL,
sueldo DECIMAL(10,2) NOT NULL
);
4. show tables;
5. describe empleado;
Despues, en htdocs de la carpeta de xampp, agrego la carpeta megatronsports_practica11_movilesII
y la abro en VSCode en donde agrego los archivos php para el CRUD y el archivo de conexion a la bd:
1. conexion.php
2. registrar_post.php
3. buscar_post.php
4. actualizar_post.php
5. eliminar_post.php
6. listar.php

OJO, en Windows Firewal -> Permitir una aplicación a través del Firewall de Windows
 -> Cambiar la configuración -> Busca Apache HTTP Server (o el ejecutable C:/xampp/apache/bin/httpd.exe)
 y marcar la casilla de Privada (Private).
Ademas, el movil y la PC deben de estar en la misma red wifi.

Finalmente, al iniciar la app, debe de ingresarse la IP actual de la PC, se gestionana archivos
y en la consola de mysql de xampp se verifica con
select * from empleado;
* */


/*
Para la practica completa use la misma base de datos y cree una nueva tabla:
USE megatronsports_practica11_movilesII;
CREATE TABLE IF NOT EXISTS vehiculo (
    id_vehiculo INT PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    anio INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    caballos_de_fuerza INT NOT NULL
);
-- Para verificar la estructura:
DESCRIBE vehiculo;

y ademas, actualice los archivos php para aceptar los nuevos campos (comente lo otro)
* */