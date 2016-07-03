package es.udc.fic.adriblanco.corunaeventhandler.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import es.udc.fic.adriblanco.corunaeventhandler.R;
import es.udc.fic.adriblanco.corunaeventhandler.Adapters.TabPagerAdapter;
import es.udc.fic.adriblanco.corunaeventhandler.Listeners.TabSelectedListener;

public class MainActivity extends AppCompatActivity {
    private ActionBar ab;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static String POSITION = "POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupTablayout();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_pets_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupTablayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        viewPager.setOffscreenPageLimit(0);
        this.viewPager = viewPager;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabSelectedListener(ab, viewPager));
        this.tabLayout = tabLayout;
    }

}



