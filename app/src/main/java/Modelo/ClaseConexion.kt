package Modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {


    fun cadenaConexion(): Connection?{
        try {
            val ip = "jdbc:oracle:thin:@192.168.0.10:1521:xe"
            val usuario = "system"
            val contrasena = "Q5v5xvt6"

            val connection = DriverManager.getConnection(ip, usuario, contrasena)
            return connection
        }catch (e: Exception){
            println("Este es el error: $e")
            return null
        }
    }
}