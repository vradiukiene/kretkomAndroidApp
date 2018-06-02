package lt.kvk.i11.radiukiene.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lt.kvk.i11.radiukiene.R;
import lt.kvk.i11.radiukiene.utils.GS;

/**
 * Created by Vita on 4/23/2018.
 */

public class WastesAdapter extends RecyclerView.Adapter<WastesAdapter.ViewHolder>{

    ArrayList<GS> news;
    Context context;
    ClickListnerForRecyclerView clickListnerForRecyclerView;

    public WastesAdapter(Context context , ArrayList<GS> maps){
        this.news = maps;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflator_for_wastes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WastesAdapter.ViewHolder holder, final int position) {

        final GS beens = news.get(position);

        holder.txt_waste.setText(beens.name);

        if (beens.waste_id.equals("1"))
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.blue));
        else if (beens.waste_id.equals("2"))
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.grey));
        else  if (beens.waste_id.equals("3"))
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.red));
        else  if (beens.waste_id.equals("4"))
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.yellow));
        else  if (beens.waste_id.equals("5"))
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.light_green));
        else  if (beens.waste_id.equals("6"))
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.black));
        else
            holder.img_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.orange));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public interface ClickListnerForRecyclerView {
        void Button_Clicked(View view, int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_waste;
        CircleImageView img_circle;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_waste = (TextView) itemView.findViewById(R.id.txt_waste);

            img_circle = (CircleImageView) itemView.findViewById(R.id.img_circle);

        }
    }
}
