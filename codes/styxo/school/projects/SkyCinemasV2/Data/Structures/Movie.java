package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

import java.time.Duration;

import codes.styxo.school.projects.SkyCinemasV2.Utils.Serializer;

public class Movie extends Base {
    //The name of the show
    public String name;
    //The base cost of the ticket
    public double baseCost;
    //Duration of the show in ms
    public Duration duration;

    //The constructor for creating blank instances of movies
    public Movie() {
        super();
    }

    //Constructor to load stored data from file
    public Movie(String data) {
        String[] d = Serializer.getStringArray(data);

        this.id = d[0];
        this.name = d[1];
        this.baseCost = Double.parseDouble(d[2]);
        this.duration = Duration.ofMillis(Long.parseLong(d[3]));
    }

    //Convert the data to a string that can be stored and converted back to a show object later
    public String serialize() {
        String[] d = {
                this.id,
                this.name,
                String.valueOf(this.baseCost),
                String.valueOf(this.duration.toMillis())
        };

        return Serializer.getString(d);
    }
}
