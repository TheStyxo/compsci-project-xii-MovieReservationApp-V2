package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

import codes.styxo.school.projects.SkyCinemasV2.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Serializer;
import java.util.Date;

public class Show extends Base {
    //The screen where the show is going to be shown
    public int screenID;
    //The movie
    public Movie movie;
    //The time when the show starts
    public Date startTime;

    //The constructor for creating blank instances of shows
    public Show() {
        super();
    }

    //Constructor to load stored data from file
    public Show(String data) {
        String[] d = Serializer.getStringArray(data);

        this.id = d[0];
        this.screenID = Integer.parseInt(d[1]);
        this.movie = DataStore.getMovie(d[2]); //Find in database using movie id
        this.startTime = new Date(Long.parseLong(d[3]));
    }

    //Convert the data to a string that can be stored and converted back to a show object later
    public String serialize() {
        String[] d = {
                this.id,
                String.valueOf(this.screenID),
                this.movie.id,
                String.valueOf(this.startTime.getTime())
        };

        return Serializer.getString(d);
    }
}
