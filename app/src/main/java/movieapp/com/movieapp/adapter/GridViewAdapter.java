package movieapp.com.movieapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import movieapp.com.movieapp.R;

public class GridViewAdapter extends ArrayAdapter {
	private Context context;
	private int layoutResourceId;
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

	public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		return super.getCount();
	}

	/**
	 * Returns the position of the specified item in the array.
	 *
	 * @param item The item to retrieve the position of.
	 * @return The position of the specified item.
	 */
	@Override
	public int getPosition(Object item) {
		return super.getPosition(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) row.findViewById(R.id.imageView_poster);
			holder.text = (TextView) row.findViewById(R.id.text) ;
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		ImageItem item = data.get(position);
        Picasso.with(getContext()).load(item.getImageURL()).into(holder.image);
		holder.text.setText(item.getImageTitle());
		return row;
	}

	static class ViewHolder {
		ImageView image;
		TextView text;
	}

}