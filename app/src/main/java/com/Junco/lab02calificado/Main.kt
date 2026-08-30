package com.Junco.lab02calificado

import java.util.Scanner

enum class TipoVehiculo(val descripcion: String, val tarifaBase: Double) {
    MOTO("Moto", 2.0),
    AUTO("Auto", 4.0),
    CAMIONETA("Camioneta", 10.0);

    companion object {
        fun desdeString(input: String): TipoVehiculo? {
            return entries.find { it.descripcion.equals(input.trim(), ignoreCase = true) }
        }
    }
}

data class Vehiculo(
    val placa: String,
    val tipo: TipoVehiculo,
    val horasEstacionado: Int,
    val esClienteFrecuente: Boolean
) {
    val subtotal: Double
        get() {
            var total = 0.0
            val base = tipo.tarifaBase

            for (hora in 1..horasEstacionado) {
                total += when {
                    hora <= 2 -> base
                    hora in 3..5 -> base * 1.20
                    else -> base * 1.50
                }
            }
            return total
        }

    val descuento: Double
        get() = if (esClienteFrecuente) subtotal * 0.10 else 0.0

    val totalPagar: Double
        get() = subtotal - descuento
}

fun main() {
    val scanner = Scanner(System.`in`)
    val listaVehiculos = mutableListOf<Vehiculo>()

    println("=== SISTEMA DE GESTIÓN DE ESTACIONAMIENTO ===")

    val cantidadVehiculos = leerEnteroPositivo(
        scanner,
        mensaje = "Ingrese la cantidad de vehículos a registrar: ",
        minimo = 1
    )

    for (i in 1..cantidadVehiculos) {
        println("\n--- Registro del Vehículo #$i ---")

        val placa = leerPlaca(scanner)
        val tipo = leerTipoVehiculo(scanner)
        val horas = leerEnteroPositivo(
            scanner,
            mensaje = "Ingrese horas estacionado (mínimo 1): ",
            minimo = 1
        )
        val esFrecuente = leerBooleanSN(scanner, "¿Es cliente frecuente? (S/N): ")

        val vehiculo = Vehiculo(
            placa = placa,
            tipo = tipo,
            horasEstacionado = horas,
            esClienteFrecuente = esFrecuente
        )

        listaVehiculos.add(vehiculo)
        println("✔ Vehículo registrado correctamente.")
    }

    mostrarResumen(listaVehiculos)
    mostrarEstadisticas(listaVehiculos)
}

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
    println("\n=========================================================================================")
    println("                                RESUMEN DE COBRO DE BOLETAS                              ")
    println("=========================================================================================")

    println("%-10s | %-10s | %-6s | %-12s | %-12s | %-12s".format(
        "PLACA", "TIPO", "HORAS", "SUBTOTAL", "DESCUENTO", "TOTAL PAGAR"
    ))
    println("-".repeat(89))

    var granTotalRecaudado = 0.0

    vehiculos.forEach { v ->
        granTotalRecaudado += v.totalPagar
        println(
            "%-10s | %-10s | %-6d | S/ %-9.2f | S/ %-9.2f | S/ %-9.2f".format(
                v.placa,
                v.tipo.descripcion,
                v.horasEstacionado,
                v.subtotal,
                v.descuento,
                v.totalPagar
            )
        )
    }

    println("=========================================================================================")
    println("GRAN TOTAL RECAUDADO: S/ %.2f".format(granTotalRecaudado))
    println("=========================================================================================")
}

fun mostrarEstadisticas(vehiculos: List<Vehiculo>) {
    if (vehiculos.isEmpty()) return

    println("\n=========================================================================================")
    println("                                ESTADÍSTICAS Y RESULTADOS                                ")
    println("=========================================================================================")

    val mayorPago = vehiculos.maxByOrNull { it.totalPagar }
    val masHoras = vehiculos.maxByOrNull { it.horasEstacionado }
    val promedioRecaudado = vehiculos.map { it.totalPagar }.average()

    if (mayorPago != null) {
        println("📌 Vehículo con mayor pago     : Placa ${mayorPago.placa} (${mayorPago.tipo.descripcion}) - Total: S/ %.2f".format(mayorPago.totalPagar))
    }
    if (masHoras != null) {
        println("📌 Vehículo con más horas      : Placa ${masHoras.placa} (${masHoras.tipo.descripcion}) - ${masHoras.horasEstacionado} horas")
    }
    println("📌 Promedio de cobro por vehículo: S/ %.2f".format(promedioRecaudado))

    println("\n--- Conteo de Vehículos por Tipo ---")
    val conteoPorTipo = vehiculos.groupBy { it.tipo }
    TipoVehiculo.entries.forEach { tipo ->
        val cantidad = conteoPorTipo[tipo]?.size ?: 0
        println(" • %-10s: %d".format(tipo.descripcion, cantidad))
    }
    println("=========================================================================================")
}