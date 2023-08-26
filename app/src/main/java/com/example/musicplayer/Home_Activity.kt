package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.musicplayer.Fragment.Home_Fragment
import com.example.musicplayer.Fragment.Mymusic_Fragment
import com.example.musicplayer.Fragment.Search_Fragment
import com.example.musicplayer.Fragment.Setting_Fragment
import com.example.musicplayer.databinding.ActivityHomeBinding

class Home_Activity : AppCompatActivity() {
    private val homeFragment=Home_Fragment()
    private val searchFragment=Search_Fragment()
    private val mymusicFragment=Mymusic_Fragment()
    private val settingFragment=Setting_Fragment()
    private lateinit var binding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(homeFragment)

        supportActionBar?.hide()

        binding.bottomnavigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home -> {
                    replaceFragment(homeFragment)
                }
                R.id.search ->{
                    replaceFragment(searchFragment)
                }
                R.id.mymusic ->{
                    replaceFragment(mymusicFragment)
                }
                R.id.setting ->{
                    replaceFragment(settingFragment)
                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null){
            val transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainfragment,fragment)
            transaction.commit()
//            updateActionBarVisibility(fragment)
        }
    }


//    private fun updateActionBarVisibility(fragment: Fragment) {
//        val hasActionBar = when (fragment) {
//            is Mymusic_Fragment -> true
//            else -> false
//        }
//
//        if (hasActionBar) {
//            supportActionBar?.show()
//            setTheme(R.style.Theme_withActionBar)
//        } else {
//            supportActionBar?.hide()
//            setTheme(R.style.Theme_MUSICPLAYER)
//        }
//    }

}