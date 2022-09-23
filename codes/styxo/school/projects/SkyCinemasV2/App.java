package codes.styxo.school.projects.SkyCinemasV2;

import codes.styxo.school.projects.SkyCinemasV2.Data.DataStore;
import codes.styxo.school.projects.SkyCinemasV2.UI.Menu;

public class App {
    //Demo mode to demonstrate app use with sample data
    public static boolean demoMode = false;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("demo")) {
            demoMode = true;
            //DataStore.replaceWithSampleData();
        }
        Menu.main();
    }
}
