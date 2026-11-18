package com.example.practica11_conexionmysql

// Modelo de datos para Vehículos Deportivos (cumple con mínimo 5 campos: idVehiculo, marca, modelo, anio, precio, caballosDeFuerza)
data class Vehiculo(
    val idVehiculo: Int,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val precio: Double,
    val caballosDeFuerza: Int
)
