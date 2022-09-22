package codes.styxo.school.projects.SkyCinemasV2.Utils;

//Class that helps to store data as a string in files,
//there is a better way to do this,
//But it is not in our syllabus,
//So can't use that...
public class Serializer {
    //This is the null control character,
    //I'm using this to separate different properties of the data objects
    private static final String dataSeperator = "\u0000";

    public static String getString(String[] data) {
        return String.join(dataSeperator, data);
    }

    public static String[] getStringArray(String data) {
        //The \\ is needed as this needs a regular expression
        return data.split("\\" + dataSeperator);
    }
}
