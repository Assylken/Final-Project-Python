package kz.adilet.kazakhlearn.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kz.adilet.kazakhlearn.R;

public class ExampleAdapter extends ArrayAdapter<Examples> {

    public class ViewHolder {
        TextView text1, text2;
    }

    public ExampleAdapter(Context context, List<Examples> objects) {
        super(context, R.layout.example_item_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        ViewHolder viewHolder;

        if(convertView == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.example_item_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.text1 = row.findViewById(R.id.text1);
            viewHolder.text2 = row.findViewById(R.id.text2);

            row.setTag(viewHolder);
        } else {
            row = convertView;
            viewHolder = (ViewHolder) row.getTag();
        }

        Examples item = getItem(position);

        viewHolder.text1.setText(item.getText1());
        viewHolder.text2.setText(item.getText2());

        return row;
    }
}