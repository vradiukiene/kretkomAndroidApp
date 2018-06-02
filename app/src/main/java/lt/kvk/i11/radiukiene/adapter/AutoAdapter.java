package lt.kvk.i11.radiukiene.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lt.kvk.i11.radiukiene.R;
import lt.kvk.i11.radiukiene.utils.GS;

/**
 * Created by Vita on 4/23/2018.
 */

public class AutoAdapter extends ArrayAdapter<GS> {

    Context context;
    int resource, textViewResourceId;
    List<GS> items, tempItems, suggestions;

    public AutoAdapter(Context context, int resource, int textViewResourceId, List<GS> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<GS>(items);
        suggestions = new ArrayList<GS>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_street, parent, false);
        }
        GS street = items.get(position);
        if (street != null) {
            TextView lblName = (TextView) view.findViewById(R.id.text_auto);
            if (lblName != null)
                lblName.setText(street.getName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((GS) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (GS streets : tempItems) {
                    if (streets.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(streets);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<GS> filterList = (ArrayList<GS>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (GS streets : filterList) {
                    add(streets);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
