package org.wit.sauna.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.wit.sauna.R
import org.wit.sauna.`interface`.MyInterface
import org.wit.sauna.models.setdata

class getpostdataforcategoriesforuser(context: Context, data: ArrayList<setdata>, myInterface : MyInterface) :
    RecyclerView.Adapter<getpostdataforcategoriesforuser.myHolder>() {
    var mcontext: Context
    var data: ArrayList<setdata>
    var interfaces : MyInterface

    var name: String? = null
    var fstore: FirebaseFirestore? = null
    var role: String? = null

    init {
        this.data = data
        mcontext = context
        this.interfaces = myInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.rowforcategories, parent, false)
        return myHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: myHolder, position: Int) {
        val petDataa: setdata = data[position]
        Glide.with(holder.postimg1.context).load(data[position].randomkey)
            .into(holder.postimg1)

        holder.title1.text = "Sauna : " + petDataa.name
        holder.quan.text = "Description : " + petDataa.description
        holder.cancelorder.setOnClickListener { }
        holder.layout.setOnClickListener { /*                Intent intent = new Intent(mcontext, displayad.class);
                    intent.putExtra("name",petDataa.getName());
                    intent.putExtra("age",petDataa.getAge());
                    intent.putExtra("email",petDataa.getEmail());
                    intent.putExtra("pass",petDataa.getPass());
                    intent.putExtra("id",petDataa.getId());
                    mcontext.startActivity(intent);*/
            holder.deletebtn.setOnClickListener(){
                interfaces.delete(data.get(position))

            }
            holder.update.setOnClickListener(){
                interfaces.update(data.get(position))

            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class myHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var price1: TextView
        var cancelorder: TextView
        var quan: TextView
        var title1: TextView
        lateinit var deletebtn: ImageView
        lateinit var update: ImageView
        var location: TextView? = null
        var postimg1: ImageView
        var postimg2: ImageView? = null
        var layout: LinearLayout

        init {
            deletebtn = itemView.findViewById(R.id.delete)
            update = itemView.findViewById(R.id.update)
            postimg1 = itemView.findViewById(R.id.img1)
            layout = itemView.findViewById(R.id.itemlinear)
            cancelorder = itemView.findViewById(R.id.cancl)
            price1 = itemView.findViewById(R.id.txt11)
            quan = itemView.findViewById(R.id.quan)
            title1 = itemView.findViewById(R.id.txt1)
        }
    }
}