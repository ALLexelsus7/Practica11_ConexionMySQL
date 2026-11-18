package com.example.practica11_conexionmysql

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.practica11_conexionmysql.ui.theme.Practica11_ConexionMySQLTheme

private val clienteApi = ClienteApiPHP()
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica11_ConexionMySQLTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PantallaPrincipalTabs(clienteApi = clienteApi)
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