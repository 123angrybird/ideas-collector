package com.example.customapp

import android.content.ClipData
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.customapp.fragment.block.BlockFragment
import com.example.customapp.fragment.draft.DraftFragment
import com.example.customapp.fragment.source.SourceFragment
import com.example.customapp.objects.Block
import com.example.customapp.objects.Draft
import com.example.customapp.objects.Source
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class IdeasActivity: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var blockFragment: BlockFragment
    private lateinit var draftFragment: DraftFragment
    private lateinit var sourceFragment: SourceFragment
    private val db = Firebase.firestore
    private var start = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ideas_activity)

        val projectId = intent.getStringExtra("Project id")
        val projectName = intent.getStringExtra("Project name")

        draftInit(projectId!!)
        sourceInit(projectId)



        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.ideas_nav_view)

        navigationView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }

        drawerLayout = findViewById(R.id.ideas_layout)
        val toogle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        blockInit(projectId, projectName!!, navigationView)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.block ->   {
                toolbar.title = "Block"
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        blockFragment
                    ).commit()
            }

            R.id.draft -> {
                toolbar.title = "Draft"
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        draftFragment
                    ).commit()
            }

            R.id.source -> {
                toolbar.title = "Source"
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        sourceFragment
                    ).commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun draftInit(projectId: String){
        val data = ArrayList<Draft>()

        db.collection("drafts").whereEqualTo("pid", projectId).orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    data.add(Draft(projectId,document.id, document.data["name"] as String, document.data["text"] as String))
                }

                draftFragment = DraftFragment.newInstance(projectId, data)
            }
            .addOnFailureListener {
                Log.e("Firebase Data", "Error getting documents $it")
                Toast.makeText(this, "Unable to load the draft", Toast.LENGTH_SHORT).show()
            }
    }

    private fun blockInit(projectId: String, projectName: String, navigationView: NavigationView){
        val data = ArrayList<Block>()

        db.collection("blocks").whereEqualTo("pid", projectId).orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    data.add(Block(projectId,document.id, document.data["name"] as String))
                }

                blockFragment = BlockFragment.newInstance(projectId, projectName, data)
            }
            .addOnFailureListener {
                blockFragment = BlockFragment.newInstance(projectId, projectName, ArrayList())
                Log.e("Firebase Data", "Error getting documents $it")
                Toast.makeText(this, "Unable to load the block", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                if (start){
                    toolbar.title = "Block"
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            blockFragment
                        ).commit()
                    navigationView.setCheckedItem(R.id.block)
                }
            }
    }

    private fun sourceInit(projectId: String){
        val data = ArrayList<Source>()

        db.collection("sources").whereEqualTo("pid", projectId).orderBy("date",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    data
                        .add(
                            Source(
                                projectId,
                                document.id,
                                document.data["title"] as String,
                                document.data["author"] as String,
                                document.data["year"].toString().toInt()
                            )
                        )
                }

                sourceFragment = SourceFragment.newInstance(projectId, data)
            }
            .addOnFailureListener {
                Log.e("Firebase Data", "Error getting documents $it")
                Toast.makeText(this, "Unable to load the sources", Toast.LENGTH_SHORT).show()
            }
    }

}