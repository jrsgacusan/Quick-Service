@file:Suppress("DEPRECATION")

package com.example.capstoneProject.UserInterface.Buyer.ManageOrderFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.GroupieViews.OrderItem
import com.example.capstoneProject.Models.Order
import com.example.capstoneProject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder


class OrdersFragment : Fragment() {
    private lateinit var v: View
    private lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 2
    }
    private val i = adapter.spanSizeLookup


    companion object {
        const val TAG = "JustSomeRandomTag"
        var orderClicked: Order? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_manage_orders, container, false)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView_manage)
        recyclerView.apply {
            layoutManager = GridLayoutManager(v.context, 2).apply {
                spanSizeLookup = i
            }
        }
        fetchOrders()
        return v
    }

    override fun onResume() {
        super.onResume()
        fetchOrders()

    }


    private fun fetchOrders() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("booked_by/$currentUserUid")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("UseRequireInsteadOfGet")
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clear()
                snapshot.children.forEach { order ->
                    val order = order.getValue(Order::class.java)!!
                    if (order.status == "NEW") {
                        adapter.add(OrderItem(order, v.context))
                    }
                    adapter.setOnItemClickListener { item, view ->
                        val orderItem = item as OrderItem
                        val tappedOrder = orderItem.order
                        orderClicked = tappedOrder
                        var orderDetailsFragment = BottomFragmentOrderDetails(orderClicked!!)
                        orderDetailsFragment.show(fragmentManager!!, "TAG")

                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}