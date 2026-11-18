package com.example.practica11_conexionmysql

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.Image
import kotlinx.coroutines.launch

//Se hace la interfaz COMPOSABLE de las opciones del menu.

// 1. DialogoConfiguracionIP (Cuadro de diálogo para solicitar la dirección IP del servidor de Web)
@Composable
fun DialogoConfiguracionIP(
    ipActual: String,
    onGuardarIP: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var ipInput by remember { mutableStateOf(ipActual) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Servidor", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ingresa la dirección IP del servidor PHP/MySQL:")
                OutlinedTextField(
                    value = ipInput,

                    onValueChange = { ipInput = it },
                    label = { Text("Dirección IP / Host") },
                    placeholder = { Text("Ej. 192.168.1.50 o 10.0.2.2") },
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
}//DialogoConfiguracionIP

// 2. PantallaPrincipalTabs (Lanzamiento del cuadro de diálogo y Menú de penstañas)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalTabs(clienteApi: ClienteApiPHP) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val pestanas = listOf("Gestión MySQL", "Listado MySQL")
    var mostrarDialogoIP by remember { mutableStateOf(true) }
    var ipConfigurada by remember { mutableStateOf(clienteApi.ipServidor)}
    // Muestra el cuadro de diálogo
    if (mostrarDialogoIP) {
        DialogoConfiguracionIP(
            ipActual = ipConfigurada,
            onGuardarIP = { nuevaIP ->
                clienteApi.ipServidor = nuevaIP
                ipConfigurada = nuevaIP
                mostrarDialogoIP = false
            },
            onDismiss = {
                // Si cancela, se mantiene la IP previa y se cierra el diálogo
                mostrarDialogoIP = false
            }
        )
    }//if
    Scaffold(
        topBar = {
            PrimaryTabRow(selectedTabIndex = tabIndex,
                Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                pestanas.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title, style = TextStyle(fontSize =
                        20.sp)) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }//PrimaryTabRow
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (tabIndex) {
                0 -> PantallaGestionEmpleado(clienteApi = clienteApi)
                1 -> PantallaListadoEmpleado(clienteApi = clienteApi)
            }
        }//Box
    }//Scaffold
}//PantallaPrincipalTabs

// 3. PantallaGestionEmpleado (interfaz del formulario CRUD)
@Composable
fun PantallaGestionEmpleado(clienteApi: ClienteApiPHP) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var numEmp by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var sueldo by remember { mutableStateOf("") }
    fun limpiarCampos() {
        numEmp = ""
        nombre = ""
        apellidos = ""
        sueldo = ""
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Datos del Empleado (Remoto)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.mando),
            contentDescription = "Imagen",
            Modifier.size(200.dp, 200.dp)
        )
        OutlinedTextField(
            value = numEmp,
            onValueChange = { numEmp = it },
            label = { Text("Número de Empleado") },
            keyboardOptions = KeyboardOptions(keyboardType =
                KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = sueldo,
            onValueChange = { sueldo = it },
            label = { Text("Sueldo") },
            keyboardOptions = KeyboardOptions(keyboardType =
                KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // AGREGAR
            IconButton(onClick = {
                if (numEmp.isNotEmpty() && nombre.isNotEmpty() &&
                    apellidos.isNotEmpty() && sueldo.isNotEmpty()) {
                    scope.launch {
                        val respuesta = clienteApi.registrarEmpleado(
                            Empleado(numEmp.toInt(), nombre, apellidos,
                                sueldo.toDouble())
                        )
                        Toast.makeText(context, respuesta,
                            Toast.LENGTH_SHORT).show()
                        if (respuesta.contains("registrado", ignoreCase =
                                true)) {
                            limpiarCampos()
                        }
                    }
                } else {
                    Toast.makeText(context, "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.add_person),
                    contentDescription = "Agregar",tint = colorScheme.primary)
            }
            // BUSCAR
            IconButton(onClick = {
                if (numEmp.isNotEmpty()) {
                    scope.launch {
                        val emp = clienteApi.buscarEmpleado(numEmp.toInt())
                        if (emp != null) {
                            nombre = emp.nombre
                            apellidos = emp.apellidos
                            sueldo = emp.sueldo.toString()
                        } else {
                            Toast.makeText(context, "Número de empleado no existe.", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                    }
                } else {
                    Toast.makeText(context, "Ingresa número de empleado",
                        Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.search_person),
                    contentDescription = "Buscar", tint = colorScheme.primary)
            }
            // ACTUALIZAR
            IconButton(onClick = {
                if (numEmp.isNotEmpty() && nombre.isNotEmpty() &&
                    apellidos.isNotEmpty() && sueldo.isNotEmpty()) {
                    scope.launch {
                        val respuesta = clienteApi.actualizarEmpleado(
                            Empleado(numEmp.toInt(), nombre, apellidos,
                                sueldo.toDouble())
                        )

                        Toast.makeText(context, respuesta,

                            Toast.LENGTH_SHORT).show()
                        if (respuesta.contains("actualizados", ignoreCase = true)) {
                            limpiarCampos()
                        }
                    }
                } else {
                    Toast.makeText(context, "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.edit_person),
                    contentDescription = "Actualizar", tint = colorScheme.primary)
            }

            // ELIMINAR
            IconButton(onClick = {
                if (numEmp.isNotEmpty()) {
                    scope.launch {
                        val respuesta =
                            clienteApi.eliminarEmpleado(numEmp.toInt())
                        Toast.makeText(context, respuesta,
                            Toast.LENGTH_SHORT).show()
                        if (respuesta.contains("eliminado", ignoreCase =
                                true)) {
                            limpiarCampos()
                        }
                    }
                } else {
                    Toast.makeText(context, "Ingresa número de empleado",
                        Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.remove_person),
                    contentDescription = "Eliminar")
            }
        }
    }//Column
}//PantallaGestionEmpleado

// 4. PantallaListadoEmpleado (interfaz para mostrar todos los registros de la base de datos)
@Composable
fun PantallaListadoEmpleado(clienteApi: ClienteApiPHP) {
    var listaEmpleados by remember { mutableStateOf(listOf<Empleado>()) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        listaEmpleados = clienteApi.obtenerEmpleados()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Empleados en MySQL",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                scope.launch {
                    listaEmpleados = clienteApi.obtenerEmpleados()
                }
            }) {
                Text("Actualizar")
            }
        }//Row
        Spacer(modifier = Modifier.height(10.dp))
        if (listaEmpleados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment =
                Alignment.Center) {
                Text("Sin registros de empleados o sin conexión.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaEmpleados) { empleado ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation =
                            CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Número: ${empleado.numEmp}",
                                fontWeight = FontWeight.Bold)
                            Text(text = "Nombre: ${empleado.nombre} ${empleado.apellidos}")
                            Text(text = "Sueldo: $${empleado.sueldo}")
                        }
                    }//Card
                }//items
            }//LazyColumn
        }//else
    }//Column
}//PantallaListadoEmpleado