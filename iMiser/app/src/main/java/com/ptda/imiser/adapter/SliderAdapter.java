package com.ptda.imiser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.ptda.imiser.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.icon1,
            R.drawable.icon2,
            R.drawable.icon3,
    };

    public String[] slide_headings = {
            "EAT",
            "SLEEP",
            "CODE"
    };

    public String[] slide_desc = {
            "It is a long established fact that a reader will be distracted by the readable content " +
                    "of a page when looking at its layout. The point of using Lorem Ipsum is that it " +
                    "has a more-or-less normal distribution of letters, as opposed to using 'Content here," +
                    " content here', making it look like readable English.",
            "There are many variations of passages of Lorem Ipsum available, but the majority have " +
                    "suffered alteration in some form, by injected humour, or randomised words which " +
                    "don't look even slightly believable.",
            "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those " +
                    "interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\""
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;             // Cuidado com este relativo
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
