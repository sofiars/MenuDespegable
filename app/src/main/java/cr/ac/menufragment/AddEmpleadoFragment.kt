package cr.ac.menufragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import cr.ac.menufragment.entity.Empleado
import cr.ac.menufragment.repository.EmpleadoRepository
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val PICK_IMAGE = 100

/**
 * A simple [Fragment] subclass.
 * Use the [AddEmpleadoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEmpleadoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    lateinit var img_avatar : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_add_empleado, container, false)
        img_avatar = view.findViewById(R.id.add_imagen)
        img_avatar.setOnClickListener {
            var gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        view.findViewById<Button>(R.id.boton_agregar).setOnClickListener{
            val textId = view?.findViewById<TextInputEditText>(R.id.textId)
            val textNombre = view?.findViewById<TextInputEditText>(R.id.TextNombre)
            val textPuesto = view?.findViewById<TextInputEditText>(R.id.TextPuesto)
            val textDepartamento = view?.findViewById<TextInputEditText>(R.id.TextDepartamento)
            var idEmpleado : Int = EmpleadoRepository.instance.data().size+1
            img_avatar = view.findViewById(R.id.add_imagen)

            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Desea agregar el empleado?")
                .setCancelable(false)
                .setPositiveButton("Sí") { dialog, id ->
                    var empleado : Empleado = Empleado (idEmpleado, textId?.text.toString(), textNombre?.text.toString(), textPuesto?.text.toString(), textDepartamento
                        ?.text.toString(), encodeImage(img_avatar.drawable.toBitmap())!!)
                    empleado?.let { EmpleadoRepository.instance.crear(it) }

                    //  Regresa al menu
                    var fragmento : Fragment = CamaraFragment.newInstance("Camara" )
                    fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.home_content, fragmento)
                        ?.commit()
                    activity?.title = ("Empleados")
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id ->
                }
            val alert = builder.create()
            alert.show()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            var imageUri = data?.data
            Picasso.get()
                .load(imageUri)
                .resize(150, 150)
                .centerCrop()
                .into(img_avatar)
        }
    }
    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT).replace("\n","")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment AddEmpleadoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            AddEmpleadoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}