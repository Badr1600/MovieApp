package movieapp.com.movieapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import movieapp.com.movieapp.R;

/**
 * Created by Ahmed on 9/5/2016.
 */
public class ReviewAdapter extends ArrayAdapter {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ReviewItem> data = new ArrayList<ReviewItem>();

    public ReviewAdapter(Context context, int resource, ArrayList data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.layout_review, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.icon_review);
            holder.text = (TextView) row.findViewById(R.id.ReviewID) ;
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ReviewItem item = data.get(position);
        holder.text.setText(item.getReview().getAuthor());
        return row;
    }

    static class ViewHolder {
        ImageView image;
        TextView text;
    }
}
