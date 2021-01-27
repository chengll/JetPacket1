package us.mifeng.jetpacket1;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import us.mifeng.jetpacket1.navgraphy.NavGraphBuilder;

public class MainActivity extends AppCompatActivity {
 private NavController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        /*
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);*/
        setContentView(R.layout.activity_main);
        Fragment mFragment=getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        controller= NavHostFragment.findNavController(mFragment);
        NavGraphBuilder.build(controller);
        //到这里 navigraph就封装完事啦

        //调用修改Fragmentnavgator 的方法
        NavGraphBuilder.build2(controller,this,mFragment.getId());


    }

}
