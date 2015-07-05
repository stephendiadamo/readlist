package com.s_diadamo.readlist;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.s_diadamo.readlist.shelf.Shelf;

public class ColourPickerDialog extends AlertDialog {
    public ColourPickerDialog(Context context, final Shelf shelf, final ImageView colourView) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View content = inflater.inflate(R.layout.colour_pallet, null);

        ImageView colours[] = {
                (ImageView) content.findViewById(R.id.color_pallet_dark_red),
                (ImageView) content.findViewById(R.id.color_pallet_dark_blue),
                (ImageView) content.findViewById(R.id.color_pallet_purple),
                (ImageView) content.findViewById(R.id.color_pallet_dark_green),
                (ImageView) content.findViewById(R.id.color_pallet_dark_orange),
                (ImageView) content.findViewById(R.id.color_pallet_light_red),
                (ImageView) content.findViewById(R.id.color_pallet_light_blue),
                (ImageView) content.findViewById(R.id.color_pallet_dark_gray),
                (ImageView) content.findViewById(R.id.color_pallet_light_green),
                (ImageView) content.findViewById(R.id.color_pallet_light_orange)
        };

        View.OnClickListener listener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colourId = ((ColorDrawable) v.getBackground()).getColor();
                shelf.setColour(colourId);
                colourView.setBackgroundColor(colourId);
                dismiss();
            }
        });

        for (ImageView colour : colours) {
            colour.setOnClickListener(listener);
        }

        setView(content);
    }
}
