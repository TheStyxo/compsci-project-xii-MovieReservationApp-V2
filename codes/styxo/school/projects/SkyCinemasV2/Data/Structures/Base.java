package codes.styxo.school.projects.SkyCinemasV2.Data.Structures;

import codes.styxo.school.projects.SkyCinemasV2.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.Utils.Serializer;

public class Base {
    //The ID of the object
    public String id;

    //The constructor for creating blank instances of data
    public Base() {
        this.id = DataStore.genID();
    }

    //The constructor for creating instances of fetched data
    public Base(String data) {
        String[] d = Serializer.getStringArray(data);

        this.id = d[0];
    }

    //Convert the data to a string that can be stored and converted back to a object later
    public String serialize() {
        String[] d = {
                this.id
        };

        return Serializer.getString(d);
    }
}
