package com.example.practica11_conexionmysql

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

// 1. Diálogo de Configuración IP
@Composable
fun DialogoConfiguracionIP(
    ipActual: String,
    onGuardarIP: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var ipInput by remember { mutableStateOf(ipActual) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Servidor MySQL", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ingresa la dirección IP del servidor web (XAMPP):")
                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("Dirección IP / Host") },
                    placeholder = { Text("Ej. 192.168.100.17") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (ipInput.isNotBlank()) {
                    onGuardarIP(ipInput.trim())
                }
            }) {
                Text("Conectar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// 2. PANTALLA 1: Login con SharedPreferences y fondo abstract_red.jpg + statusBarsPadding
@Composable
fun PantallaLogin(
    onLoginSuccess: () -> Unit,
    onConfigurarIpClick: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("PreferenciasVehiculos", Context.MODE_PRIVATE)
    }

    var usuario by remember { mutableStateOf(sharedPreferences.getString("usuario", "") ?: "") }
    var password by remember { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    var recordar by remember { mutableStateOf(sharedPreferences.getBoolean("recordar", false)) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.abstract_red),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Megatron Sports",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )

                    Image(
                        painter = painterResource(id = R.drawable.mando),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    OutlinedTextField(
                        value = usuario,
                        onValueChange = { usuario = it },
                        label = { Text("Usuario", color = Color.White) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = Color.White) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = recordar,
                            onCheckedChange = { recordar = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                        )
                        Text("Recordar datos (SharedPreferences)", color = Color.White)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = {
                                if (usuario.isNotBlank() && password.isNotBlank()) {
                                    if (recordar) {
                                        sharedPreferences.edit().apply {
                                            putString("usuario", usuario)
                                            putString("password", password)
                                            putBoolean("recordar", true)
                                            apply()
                                        }
                                    } else {
                                        sharedPreferences.edit().clear().apply()
                                    }
                                    Toast.makeText(context, "Bienvenido $usuario", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Ingresar", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                usuario = ""
                                password = ""
                                recordar = false
                                sharedPreferences.edit().clear().apply()
                            }
                        ) {
                            Text("Limpiar", color = Color.White)
                        }
                    }

                    TextButton(onClick = onConfigurarIpClick) {
                        Text("Configurar IP del Servidor", color = Color.LightGray)
                    }
                }
            }
        }
    }
}

// 3. PANTALLA 2: Formulario CRUD de Vehículos (con iconos en res/drawable, estilo coherente y statusBarsPadding)
@Composable
fun PantallaGestionVehiculo(
    clienteApi: ClienteApiPHP,
    onIrAlListado: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var idVehiculo by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var caballosDeFuerza by remember { mutableStateOf("") }

    fun limpiarCampos() {
        idVehiculo = ""
        marca = ""
        modelo = ""
        anio = ""
        precio = ""
        caballosDeFuerza = ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.abstract_red),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gestión de Vehículos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextButton(onClick = onCerrarSesion) {
                    Text("Cerrar Sesión", color = Color.Red)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = idVehiculo,
                        onValueChange = { idVehiculo = it },
                        label = { Text("ID Vehículo (Num)", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = marca,
                        onValueChange = { marca = it },
                        label = { Text("Marca", color = Color.White) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = modelo,
                        onValueChange = { modelo = it },
                        label = { Text("Modelo", color = Color.White) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = anio,
                        onValueChange = { anio = it },
                        label = { Text("Año", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio ($)", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = caballosDeFuerza,
                        onValueChange = { caballosDeFuerza = it },
                        label = { Text("Caballos de Fuerza (HP)", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Botones CRUD usando iconos de res/drawable (add_person, search_person, edit_person, remove_person)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // REGISTRAR / AGREGAR
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                if (idVehiculo.isNotEmpty() && marca.isNotEmpty() && modelo.isNotEmpty() &&
                                    anio.isNotEmpty() && precio.isNotEmpty() && caballosDeFuerza.isNotEmpty()) {
                                    scope.launch {
                                        val respuesta = clienteApi.registrarVehiculo(
                                            Vehiculo(
                                                idVehiculo.toInt(),
                                                marca,
                                                modelo,
                                                anio.toInt(),
                                                precio.toDouble(),
                                                caballosDeFuerza.toInt()
                                            )
                                        )
                                        Toast.makeText(context, respuesta, Toast.LENGTH_SHORT).show()
                                        if (respuesta.contains("registrado", ignoreCase = true) || respuesta.contains("éxito", ignoreCase = true)) {
                                            limpiarCampos()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_person),
                                    contentDescription = "Registrar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text("Registrar", color = Color.White, fontSize = 12.sp)
                        }

                        // BUSCAR
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                if (idVehiculo.isNotEmpty()) {
                                    scope.launch {
                                        val veh = clienteApi.buscarVehiculo(idVehiculo.toInt())
                                        if (veh != null) {
                                            marca = veh.marca
                                            modelo = veh.modelo
                                            anio = veh.anio.toString()
                                            precio = veh.precio.toString()
                                            caballosDeFuerza = veh.caballosDeFuerza.toString()
                                            Toast.makeText(context, "Vehículo encontrado", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "No existe el vehículo", Toast.LENGTH_SHORT).show()
                                            limpiarCampos()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Ingrese ID de vehículo a buscar", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.search_person),
                                    contentDescription = "Buscar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text("Buscar", color = Color.White, fontSize = 12.sp)
                        }

                        // ACTUALIZAR
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                if (idVehiculo.isNotEmpty() && marca.isNotEmpty() && modelo.isNotEmpty() &&
                                    anio.isNotEmpty() && precio.isNotEmpty() && caballosDeFuerza.isNotEmpty()) {
                                    scope.launch {
                                        val respuesta = clienteApi.actualizarVehiculo(
                                            Vehiculo(
                                                idVehiculo.toInt(),
                                                marca,
                                                modelo,
                                                anio.toInt(),
                                                precio.toDouble(),
                                                caballosDeFuerza.toInt()
                                            )
                                        )
                                        Toast.makeText(context, respuesta, Toast.LENGTH_SHORT).show()
                                        if (respuesta.contains("actualizado", ignoreCase = true) || respuesta.contains("éxito", ignoreCase = true)) {
                                            limpiarCampos()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Complete todos los campos para actualizar", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit_person),
                                    contentDescription = "Actualizar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text("Actualizar", color = Color.White, fontSize = 12.sp)
                        }

                        // ELIMINAR
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                if (idVehiculo.isNotEmpty()) {
                                    scope.launch {
                                        val respuesta = clienteApi.eliminarVehiculo(idVehiculo.toInt())
                                        Toast.makeText(context, respuesta, Toast.LENGTH_SHORT).show()
                                        if (respuesta.contains("eliminado", ignoreCase = true) || respuesta.contains("éxito", ignoreCase = true)) {
                                            limpiarCampos()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Ingrese ID de vehículo a eliminar", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.remove_person),
                                    contentDescription = "Eliminar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text("Eliminar", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = onIrAlListado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Listado de Vehículos", color = Color.White)
            }
        }
    }
}

// 4. PANTALLA 3: Listado de Vehículos con botón de volver al CRUD, estilo y statusBarsPadding
@Composable
fun PantallaListadoVehiculo(
    clienteApi: ClienteApiPHP,
    onVolverAlFormulario: () -> Unit
) {
    var listaVehiculos by remember { mutableStateOf(listOf<Vehiculo>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listaVehiculos = clienteApi.obtenerVehiculos()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.abstract_red),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cabecera organizada para evitar que se aplaste en pantallas estrechas
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Listado de Vehículos Deportivos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        onClick = onVolverAlFormulario
                    ) {
                        Text("← Volver al CRUD")
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        onClick = {
                            scope.launch {
                                listaVehiculos = clienteApi.obtenerVehiculos()
                            }
                        }
                    ) {
                        Text("Actualizar", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (listaVehiculos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
                    ) {
                        Text(
                            text = "Sin registros de vehículos o sin conexión.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaVehiculos) { vehiculo ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.82f)),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.anio})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "ID: ${vehiculo.idVehiculo}", color = Color.White)
                                Text(text = "Precio: $${vehiculo.precio}", color = Color.White)
                                Text(text = "Caballos de Fuerza: ${vehiculo.caballosDeFuerza} HP", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
