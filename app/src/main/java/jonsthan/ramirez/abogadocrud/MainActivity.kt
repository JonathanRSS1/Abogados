package jonsthan.ramirez.abogadocrud

import Modelo.ClaseConexion
import Modelo.tbAbogado
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import recyclerviewhelpers.Adaptador
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // 1 Mandar a llamar a todos los elementos de la vista


        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)

        val btnInsertar = findViewById<Button>(R.id.btnInsertar)
        val rcvTickets = findViewById<RecyclerView>(R.id.rcvAbogados)


        //Agrego un layaout al RecyclerView

        rcvTickets.layoutManager = LinearLayoutManager(this)


        //////////////// TODO :  mostrar datos


        fun obtenerAbogados(): List<tbAbogado>{

            //1- Crear un onjeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()

            val resultSet = statement?.executeQuery("SELECT * FROM tbAbogado")!!

            val listaAbogados = mutableListOf<tbAbogado>()

            while (resultSet.next()){
                val uuid = resultSet.getString("uuid")
                val Nombre_Abogado = resultSet.getString("Nombre_Abogado")
                val Peso_Abogado = resultSet.getDouble("Peso_Abogado")
                val Edad_Abogado = resultSet.getInt("Edad_Abogado")
                val Correo_Abogado = resultSet.getString("Correo_Abogado")



                val valoresJuntos = tbAbogado(uuid, Nombre_Abogado, Peso_Abogado, Edad_Abogado, Correo_Abogado)

                listaAbogados.add(valoresJuntos)


            }

            return listaAbogados
        }

        // Asignarle el adaptador al RecyclerView

        CoroutineScope(Dispatchers.IO).launch {


            val AbogadoBD = obtenerAbogados()
            withContext(Dispatchers.Main){

                val adapter = Adaptador(AbogadoBD)

                rcvTickets.adapter= adapter
            }


        }




        // 2 Programar el boton insertar

        btnInsertar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                //1-Crear un objeto de la clase conexion

                val objConexion = ClaseConexion().cadenaConexion()

                // Crear una variable que contenga un PrepareStatement


                val addAbogado = objConexion?.prepareStatement("insert into tbAbogado (uuid, Nombre_Abogado, Peso_Abogado, Edad_Abogado, Correo_Abogado)  VALUES (?, ?, ?, ?, ?)")!!
                addAbogado.setString(1, UUID.randomUUID().toString())
                addAbogado.setString(2, txtNombre.text.toString())
                addAbogado.setDouble(3, txtPeso.text.toString().toDouble())
                addAbogado.setInt(4, txtEdad.text.toString().toInt())
                addAbogado.setString(5, txtEmail.text.toString())

                addAbogado.executeUpdate()

            }

        }



    }
}