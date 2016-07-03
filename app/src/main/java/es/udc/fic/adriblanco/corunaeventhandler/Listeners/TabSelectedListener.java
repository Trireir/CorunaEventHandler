package es.udc.fic.adriblanco.corunaeventhandler.Listeners;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import es.udc.fic.adriblanco.corunaeventhandler.Activities.InfoActivity;
import es.udc.fic.adriblanco.corunaeventhandler.Fragments.PrincipalFragment;

public class TabSelectedListener extends TabLayout.ViewPagerOnTabSelectedListener {
    public ActionBar actionBar;

    private String tabTitles[] = new String[] { "Principal", "Música", "Cine",
            "Teatro", "Infantil", "Ciencia", "Deportivo"};

    public TabSelectedListener(ActionBar ab, ViewPager vp) {
        super(vp);
        this.actionBar = ab;
    }

    public void onTabReselected(TabLayout.Tab tab) {
        super.onTabReselected(tab);
    }

    public void onTabSelected(TabLayout.Tab tab) {
        super.onTabSelected(tab);
        actionBar.setTitle(tabTitles[tab.getPosition()]);

    }

    public void onTabUnselected(TabLayout.Tab tab) {
        super.onTabUnselected(tab);
    }
}
