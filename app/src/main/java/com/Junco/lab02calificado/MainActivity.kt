package com.junco.lab02calificado

import java.util.Scanner

// 1. Dominio: Tipo de vehículo como Enum para un typing seguro
enum class TipoVehiculo(val descripcion: String) {
    MOTO("Moto"),
    AUTO("Auto"),
    CAMIONETA("Camioneta");

    companion object {
        fun desdeString(input: String): TipoVehiculo? {
            return entries.find { it.descripcion.equals(input.trim(), ignoreCase = true) }
        }
    }
}

// Data Class que representa la entidad Vehículo
data class Vehiculo(
    val placa: String,
    val tipo: TipoVehiculo,
    val horasEstacionado: Int,
    val esClienteFrecuente: Boolean
)

fun main() {
    val scanner = Scanner(System.`in`)
    val listaVehiculos = mutableListOf<Vehiculo>()

    println("=== SISTEMA DE GESTIÓN DE ESTACIONAMIENTO ===")

    // 2. Solicitar cantidad de vehículos a registrar
    val cantidadVehiculos = leerEnteroPositivo(
        scanner,
        mensaje = "Ingrese la cantidad de vehículos a registrar: ",
        minimo = 1
    )

    // 3. Bucle para la captura de datos
    for (i in 1..cantidadVehiculos) {
        println("\n--- Registro del Vehículo #$i ---")

        val placa = leerPlaca(scanner)
        val tipo = leerTipoVehiculo(scanner)
        val horas = leerEnteroPositivo(
            scanner,
            mensaje = "Ingrese horas estacionado (mínimo 1): ",
            minimo = 1
        )
        val esFrecuente = leerBooleanSN(scanner, "Is el conductor cliente frecuente? (S/N): ")

        val vehiculo = Vehiculo(
            placa = placa,
            tipo = tipo,
            horasEstacionado = horas,
            esClienteFrecuente = esFrecuente
        )

        listaVehiculos.add(vehiculo)
        println("✔ Vehículo registrado correctamente.")
    }

    // 4. Mostrar resumen básico
    mostrarResumen(listaVehiculos)
}

// ==========================================
// FUNCIONES DE VALIDACIÓN Y LECTURA ROBUTA
// ==========================================

fun leerPlaca(scanner: Scanner): String {
    while (true) {
        print("Ingrese la placa: ")
        val entrada = scanner.nextLine().trim().uppercase()
        if (entrada.isNotEmpty()) return entrada
        println("❌ Error: La placa no puede estar vacía. Reintente.")
    }
}

fun leerTipoVehiculo(scanner: Scanner): TipoVehiculo {
    while (true) {
        print("Tipo de vehículo (Moto, Auto, Camioneta): ")
        val entrada = scanner.nextLine()
        val tipo = TipoVehiculo.desdeString(entrada)

        if (tipo != null) return tipo

        println("❌ Error: Tipo inválido. Solo se permite: Moto, Auto o Camioneta.")
    }
}

fun leerEnteroPositivo(scanner: Scanner, mensaje: String, minimo: Int): Int {
    while (true) {
        print(mensaje)
        val entrada = scanner.nextLine()
        val numero = entrada.toIntOrNull()

        if (numero != null && numero >= minimo) {
            return numero
        }
        println("❌ Error: Debe ingresar un valor numérico entero igual o mayor a $minimo.")
    }
}

fun leerBooleanSN(scanner: Scanner, mensaje: String): Boolean {
    while (true) {
        print(mensaje)
        val entrada = scanner.nextLine().trim().uppercase()

        when (entrada) {
            "S" -> return true
            "N" -> return false
            else -> println("❌ Error: Responda únicamente con 'S' (Sí) o 'N' (No).")
        }
    }
}

fun mostrarResumen(vehiculos: List<Vehiculo>) {
    println("\n=======================================================")
    println(" RESUMEN DE VEHÍCULOS REGISTRADOS (${vehiculos.size})")
    println("=======================================================")

    println("%-12s | %-12s | %-8s | %-18s".format("PLACA", "TIPO", "HORAS", "CLIENTE FRECUENTE"))
    println("-".repeat(57))

    vehiculos.forEach { v ->
        println(
            "%-12s | %-12s | %-8d | %-18s".format(
                v.placa,
                v.tipo.descripcion,
                v.horasEstacionado,
                if (v.esClienteFrecuente) "Sí" else "No"
            )
        )
    }
    println("=======================================================")
}