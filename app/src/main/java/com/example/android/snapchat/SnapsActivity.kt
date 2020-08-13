package com.example.android.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class SnapsActivity : AppCompatActivity() {

    var snapList :ListView? = null
    val emails: ArrayList<String> = ArrayList()
    val snaps: ArrayList< DataSnapshot> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_the_snaps)

        title = "Snaps"

        snapList = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapList?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("snaps")
            .addChildEventListener(

                object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        emails.add(snapshot.child("from").value as String)
                        snaps.add(snapshot)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {}

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        var index = 0
                        for (snap: DataSnapshot in snaps) {
                            if (snap.key == snapshot.key) {
                                snaps.removeAt(index)
                                emails.removeAt(index)
                                adapter.notifyDataSetChanged()
                            }
                            index++
                        }

                    }



                })

        snapList?.onItemClickListener = (AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot = snaps[position]
            val intent = Intent(this, ViewSnapsActivity::class.java)

            intent.putExtra("imageName", snapshot.child("imageName")?.value as String )
            intent.putExtra("imageUrl", snapshot.child("imageURL")?.value as String )
            intent.putExtra("message",snapshot.child("message")?.value as String )
            intent.putExtra("snapKey",  snapshot.key)

            startActivity(intent)

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.createSnap) {
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)

        }else if (item?.itemId == R.id.logout) {
            Firebase.auth.signOut()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Firebase.auth.signOut()

    }

}