package recyclerviewhelpers

import Modelo.ClaseConexion
import Modelo.tbAbogado
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import jonsthan.ramirez.abogadocrud.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException


class Adaptador(var Datos: List<tbAbogado>): RecyclerView.Adapter<ViewHolder>() {


    fun actualizarLista(nuevaLista: List<tbAbogado>) {
        Datos = nuevaLista
        notifyDataSetChanged() // Notificar al adaptador sobre los cambios
    }






    /////////////////// TODO: Eliminar datos
    fun eliminarDatos(posicion: Int) {
        // 1. Obtener el ticket a eliminar
        val ticketEliminado = Datos[posicion]
        // 2. Eliminar el ticket de la lista
        val listaMutable = Datos.toMutableList()
        listaMutable.removeAt(posicion)
        Datos = listaMutable.toList()

        // 3. Notificar al adaptador del cambio
        notifyItemRemoved(posicion)

        // 4. Eliminar el ticket de la base de datos
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val deleteStatement = objConexion?.prepareStatement("DELETE FROM tbAbogado WHERE UUID_Abogado = ?")
                deleteStatement?.setString(1, ticketEliminado.UUID_Abogado)
                deleteStatement?.executeUpdate()
                objConexion?.commit()
            } catch (e: SQLException) {
                Log.e("EliminarTicket", "Error al eliminar ticket", e)
                // Manejar el error de forma adecuada

            }


        }
    }



    fun actualizarDato(ticketActualizado: tbAbogado,recyclerView: RecyclerView) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val updateTicket = objConexion?.prepareStatement(
                    "UPDATE tbAbogado SET Nombre_Abogado = ?,  Edad_Abogado = ?, Peso_Abogado = ?, Correo_Abogado = ? WHERE UUID_Abogado = ?"
                )
                updateTicket?.setString(1, ticketActualizado.Nombre_Abogado)
                updateTicket?.setInt(2, ticketActualizado.Edad_Abogado.toInt())
                updateTicket?.setDouble(3, ticketActualizado.Peso_Abogado as Double)
                updateTicket?.setString(4, ticketActualizado.Correo_Abogado)
                updateTicket?.setString(5, ticketActualizado.UUID_Abogado)
                updateTicket?.executeUpdate()

                withContext(Dispatchers.Main) {
                    val indice = Datos.indexOfFirst { it.UUID_Abogado == ticketActualizado.UUID_Abogado }
                    if (indice != -1) {
                        val listaMutable = Datos.toMutableList()
                        listaMutable[indice] = ticketActualizado
                        Datos = listaMutable.toList()
                        notifyItemChanged(indice)
                        // Obtiene el ViewHoldery actualiza la vista
                        val viewHolder =
                            (recyclerView.findViewHolderForAdapterPosition(indice) as? ViewHolder)
                        viewHolder?.bind(ticketActualizado)

                    }
                }
            }catch (e: SQLException) {
                Log.e("ActualizarDato", "Error al actualizar ticket", e)
                // Manejar el error de forma adecuada
            }
        }
    }








    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       // Unir el RecyclerView con la card

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         // Controlar a la card

        val item1 = Datos[position]
        holder.txtNombreCard.text = item1.Nombre_Abogado

        val item2 = Datos[position]
        holder.txtEdadCard.text = item2.Edad_Abogado.toString()


        val item3 = Datos[position]
        holder.txtPesoCard.text = item3.Peso_Abogado.toString()


        val item4 = Datos[position]
        holder.txtEmailCard.text = item4.Correo_Abogado






        //todo: clic al icono de eliminar
        holder.imgBorrar.setOnClickListener {

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar el abogado?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        holder.imgEditar.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Abogado")

            val view = LayoutInflater.from(context).inflate(R.layout.dialogo_editar_abogado, null) // Crea un layout para el diálogo
            builder.setView(view)

            val txtNombre = view.findViewById<EditText>(R.id.txtNombre)
            val txtEdad = view.findViewById<EditText>(R.id.txtEdad)
            val txtPeso = view.findViewById<EditText>(R.id.txtPeso)
            val txtEmail = view.findViewById<EditText>(R.id.txtEmail)


            // Llena los cuadros de texto con los datos actuales del ticket



            val abogado = Datos[position]

            txtNombre.setText(abogado.Nombre_Abogado)
            txtEdad.setText(abogado.Edad_Abogado.toString())
            txtPeso.setText(abogado.Peso_Abogado.toString())
            txtEmail.setText(abogado.Correo_Abogado)



            builder.setPositiveButton("Actualizar") { dialog, which ->
                val ticketActualizado = tbAbogado(
                    abogado.UUID_Abogado,
                    txtNombre.text.toString(),
                    txtEdad.text.toString().toInt(),
                    txtPeso.text.toString().toDouble(), // Asegúrate de usar toDouble() aquí
                    txtEmail.text.toString(),

                )
                actualizarDato(ticketActualizado, holder.itemView.parent as  RecyclerView)
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

    }
}