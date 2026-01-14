package ma.projet.tp6_meteoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeteoListModel extends ArrayAdapter<MeteoItem> {
    private List<MeteoItem> listItems;
    private int resource;
    public static Map<String, Integer> images = new HashMap<>();

    static {
        // Map OpenWeather API codes to your local drawable images
        images.put("01d", R.mipmap.clear_foreground);
        images.put("01n", R.mipmap.clear_foreground);
        images.put("02d", R.mipmap.clouds_foreground);
        images.put("03d", R.mipmap.clouds_foreground);
        images.put("04d", R.mipmap.clouds_foreground);
        images.put("09d", R.mipmap.rain_foreground);
        images.put("10d", R.mipmap.rain_foreground);
        images.put("11d", R.mipmap.thunderstorm_foreground);
        // Defaults for others to prevent crashes
        images.put("13d", R.mipmap.clouds_foreground);
        images.put("50d", R.mipmap.clouds_foreground);
    }

    public MeteoListModel(Context context, int resource, List<MeteoItem> data) {
        super(context, resource, data);
        this.listItems = data;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        ImageView imageView = listItem.findViewById(R.id.imageView);
        TextView textViewTempMax = listItem.findViewById(R.id.textViewTempMAX);
        TextView textViewTempMin = listItem.findViewById(R.id.textViewTempMin);
        TextView textViewPression = listItem.findViewById(R.id.textViewPression);
        TextView textViewHumidite = listItem.findViewById(R.id.textViewHumidite);
        TextView textViewDate = listItem.findViewById(R.id.textViewDate);

        MeteoItem item = listItems.get(position);

        // Load correct image based on API code
        if (item.image != null && images.containsKey(item.image)) {
            imageView.setImageResource(images.get(item.image));
        } else {
            imageView.setImageResource(R.mipmap.clouds_foreground); // Fallback image
        }

        textViewTempMax.setText(item.tempMax + " °C");
        textViewTempMin.setText(item.tempMin + " °C");
        textViewPression.setText(item.pression + " hPa");
        textViewHumidite.setText(item.humidite + " %");
        textViewDate.setText(item.date);

        return listItem;
    }
}