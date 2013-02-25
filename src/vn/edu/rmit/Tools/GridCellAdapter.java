package vn.edu.rmit.Tools;

import vn.edu.rmit.RMITAppMarket.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridCellAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;

	public GridCellAdapter(Context context, LayoutInflater inflater,
			int textViewResourceId, String[] objects) {
		super(context, textViewResourceId, objects);
		this.inflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View cell = convertView;

		if (cell == null) {
			cell = inflater.inflate(R.layout.grid_cell, parent, false);

			// create image
			ImageView image = (ImageView) cell
					.findViewById(R.id.grid_cell_image);

			//use Bitmap instead of Uri 
			Bitmap bm = BitmapFactory.decodeFile("/sdcard/"
					+ ApplicationHandler.getApplicationHandler()
							.getImageNameAtPosition(position));
			image.setImageBitmap(bm);

			// change application name
			TextView appName = (TextView) cell
					.findViewById(R.id.grid_cell_appName);
			appName.setText(getItem(position));
		}

		return cell;
	}
}
