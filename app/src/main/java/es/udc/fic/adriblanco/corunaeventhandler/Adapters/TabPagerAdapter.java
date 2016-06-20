package es.udc.fic.adriblanco.corunaeventhandler.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import es.udc.fic.adriblanco.corunaeventhandler.Fragments.PrincipalFragment;
import es.udc.fic.adriblanco.corunaeventhandler.R;


public class TabPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] {
            "Principal",
            "Música",
            "Cine",
            "Teatro",
            "Infantil",
            "Ciencia",
            "Deportes"};

    private int tabIcons[] = new int[]{
            R.drawable.principal,
            R.drawable.musica,
            R.drawable.cine,
            R.drawable.teatro,
            R.drawable.infantil,
            R.drawable.ciencia,
            R.drawable.deporte};

    private Context context;
    final int PAGE_COUNT = tabTitles.length;

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return PrincipalFragment.newInstance(tabTitles[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, tabIcons[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
