package com.example.capstoneProject.seller_activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.example.capstoneProject.R
import com.example.capstoneProject.messages_activities.ChatLogActivity
import com.example.capstoneProject.models.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class BottomShowRequestFragment : BottomSheetDialogFragment() {

    lateinit var v: View
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var floatButton: FloatingActionButton
    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_bottom_show_request, container, false)
        imageView = v.findViewById(R.id.imageVIew_fragmentBottomShowRequest)
        textView = v.findViewById(R.id.textView_fragmentBottomShowRequest)
        floatButton = v.findViewById(R.id.floatingActionButton_fragmentBottomShowRequest)

        Picasso.get().load(BuyersRequestActivity.serviceRequestToBeViewed!!.userImage.toString()).into(imageView)
        textView.text = "${BuyersRequestActivity.serviceRequestToBeViewed!!.description}"

        floatButton.setOnClickListener {
            fetchTheUserWhoPostedTheRequest()
        }






        return v
    }

    private fun fetchTheUserWhoPostedTheRequest() {
        val ref = FirebaseDatabase.getInstance().getReference("users/${BuyersRequestActivity.serviceRequestToBeViewed!!.userUid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val toUser = snapshot.getValue(User::class.java)
                val intent = Intent(v.context, ChatLogActivity::class.java)
                intent.putExtra("user", toUser)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


}

