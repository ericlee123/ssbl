package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.ssbl.R;

public class InboxArrayAdapter extends ArrayAdapter<String> {

    private final Context _context;
    private final String[] _values;

    public InboxArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_inbox, values);
        _context = context;
        _values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_inbox, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_inbox_preview);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_inbox_icon);
        textView.setText(_values[position]);

        // Change icon based on name
        String s = _values[position];

        imageView.setImageResource(R.drawable.gc_green_circle);
//        int rng = (int) (Math.random() * 15);
//        if (rng == 0)
//            imageView.setImageResource(R.drawable.md_amber_circle);
//        else if (rng == 1)
//            imageView.setImageResource(R.drawable.md_blue_circle);
//        else if (rng == 2)
//            imageView.setImageResource(R.drawable.md_cyan_circle);
//        else if (rng == 3)
//            imageView.setImageResource(R.drawable.md_deep_orange_circle);
//        else if (rng == 4)
//            imageView.setImageResource(R.drawable.md_green_circle);
//        else if (rng == 5)
//            imageView.setImageResource(R.drawable.md_indigo_circle);
//        else if (rng == 6)
//            imageView.setImageResource(R.drawable.md_light_blue_circle);
//        else if (rng == 7)
//            imageView.setImageResource(R.drawable.md_light_green_circle);
//        else if (rng == 8)
//            imageView.setImageResource(R.drawable.md_lime_circle);
//        else if (rng == 9)
//            imageView.setImageResource(R.drawable.md_orange_circle);
//        else if (rng == 10)
//            imageView.setImageResource(R.drawable.md_pink_circle);
//        else if (rng == 11)
//            imageView.setImageResource(R.drawable.md_purple_circle);
//        else if (rng == 12)
//            imageView.setImageResource(R.drawable.md_red_circle);
//        else if (rng == 13)
//            imageView.setImageResource(R.drawable.md_teal_circle);
//        else if (rng == 14)
//            imageView.setImageResource(R.drawable.md_yellow_circle);

        return rowView;
    }
}
