package com.example.capstoneProject.UserInterface.Buyer.BuyerActivities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneProject.GroupieViews.ManageRequestItem
import com.example.capstoneProject.GroupieViews.ServiceCreatedItem
import com.example.capstoneProject.Handlers.ServiceRequestHandler
import com.example.capstoneProject.Models.ServiceRequest
import com.example.capstoneProject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.ArrayList


class ManageRequestActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var serviceRequestRecyclerView: RecyclerView
    var currentUserUid = FirebaseAuth.getInstance().uid
    var serviceRequestHandler = ServiceRequestHandler()
    lateinit var hideThisTextView: TextView
    lateinit var hideThisImageView: ImageView
    lateinit var clickHereTextView: TextView
    val adapter = GroupAdapter<ViewHolder>()

    companion object {
        var serviceRequestGettingEdited: ServiceRequest? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_request)
        //map everything here
        toolbar = findViewById(R.id.toolBar_activityManageRequest)
        serviceRequestRecyclerView = findViewById(R.id.requestListView_activityManageRequest)
        hideThisTextView = findViewById(R.id.hideThisTextView_activityManageRequest)
        hideThisImageView = findViewById(R.id.hideThisImageView_activityManageRequest)
        clickHereTextView = findViewById(R.id.clickHereTextView_activityManageRequest)

        //
        toolbar.title = "Manage Requests"
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clickHereTextView.setOnClickListener {
            val intent = Intent(applicationContext, RequestActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onStart() {
        super.onStart()
        //register a listener to teverytime the database updates
        serviceRequestHandler.serviceRequestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                adapter.clear()
                p0.children.forEach { it ->
                    val serviceRequest = it.getValue(ServiceRequest::class.java)
                    if (serviceRequest!!.userUid == currentUserUid) {
                        adapter.add(ManageRequestItem(serviceRequest))
                    }
                    adapter.setOnItemClickListener{ item, view ->
                        val itemClicked = item as ManageRequestItem
                        val requestClicked = itemClicked.request
                        showMenu(requestClicked, view)

                    }
                }
                serviceRequestRecyclerView.adapter = adapter

                hideOrShowViews()

            }

            override fun onCancelled(p0: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun showMenu(requestClicked: ServiceRequest, view: View) {
        val popupMenu: PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_edit_delete, popupMenu.menu)
        val pause = popupMenu.menu.findItem(R.id.pause)
        val setAsActive = popupMenu.menu.findItem(R.id.setAsActive)
        val preview = popupMenu.menu.findItem(R.id.preview)
        pause.isVisible = false
        setAsActive.isVisible = false
        preview.isVisible = false

        popupMenu.setOnMenuItemClickListener {  menu ->
            when(menu.itemId){
                R.id.edit -> {
                    serviceRequestGettingEdited = requestClicked
                    startActivity(Intent(this, RequestActivity::class.java))
                }
                R.id.delete -> {
                    val dialogBuilder = AlertDialog.Builder(view.context)
                    dialogBuilder.setMessage("Do you want to delete this request?")
                            .setCancelable(true)
                            .setPositiveButton("Continue", DialogInterface.OnClickListener { _, _ ->
                                val ref = FirebaseDatabase.getInstance().getReference("/service_requests/${requestClicked.uid}")
                                ref.removeValue()
                                        .addOnCompleteListener {
                                            Toast.makeText(view.context, "Request Deleted", Toast.LENGTH_SHORT).show()
                                        }
                            })
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                                dialog.cancel()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle("Delete")
                    alert.show()
                }
            }
            true

        }

        //show the icons
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
        } catch (e: Exception) {
            //Log the exception here
        } finally {
            popupMenu.show()
        }

    }


    private fun hideOrShowViews() {
        if (adapter.itemCount == 0) {
            hideThisTextView.isVisible = true
            hideThisImageView.isVisible = true
            clickHereTextView.isVisible = true
        } else {
            hideThisTextView.isVisible = false
            hideThisImageView.isVisible = false
            clickHereTextView.isVisible = false
        }
    }


}