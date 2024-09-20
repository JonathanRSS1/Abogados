package recyclerviewhelpers

import Modelo.tbAbogado
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jonsthan.ramirez.abogadocrud.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    //En el viewHolder mando a llama a los elementos de la Card

    val imgEditar: ImageView = view.findViewById(R.id.imgEditar)
    val imgBorrar: ImageView = view.findViewById(R.id.imgBorrar)


    val txtNombreCard = view.findViewById<TextView>(R.id.txtNombreCard)
    val txtPesoCard = view.findViewById<TextView>(R.id.txtPesoCard)
    val txtEdadCard = view.findViewById<TextView>(R.id.txtEdadCard)
    val txtEmailCard = view.findViewById<TextView>(R.id.txtEmailCard)




    fun bind(ticket: tbAbogado) {
        txtNombreCard.text = ticket.Nombre_Abogado
        txtPesoCard.text = ticket.Peso_Abogado.toString()
        txtEdadCard.text = ticket.Edad_Abogado.toString()
        txtEmailCard.text = ticket.Correo_Abogado

    }




}