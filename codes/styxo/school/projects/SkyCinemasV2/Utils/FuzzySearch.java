package codes.styxo.school.projects.SkyCinemasV2.Utils;

import java.util.ArrayList;

import codes.styxo.school.projects.SkyCinemasV2.Data.Structures.Movie;

//Class to handle searching through movies
//Using Dice's coefficient
public class FuzzySearch {
    public static final int maxItemsPerPage = 10;

    //Method to create bigram from string
    public static ArrayList<char[]> bigram(String input) {
        input = input.toLowerCase();
        ArrayList<char[]> bigram = new ArrayList<char[]>();
        for (int i = 0; i < input.length() - 1; i++) {
            char[] chars = new char[2];
            chars[0] = input.charAt(i);
            chars[1] = input.charAt(i + 1);
            bigram.add(chars);
        }
        return bigram;
    }

    //Get the value for the match
    public static long dice(ArrayList<char[]> bigram1, ArrayList<char[]> bigram2) {
        ArrayList<char[]> copy = new ArrayList<char[]>(bigram2);
        int matches = 0;
        for (int i = bigram1.size(); --i >= 0;) {
            char[] bigram = bigram1.get(i);
            for (int j = copy.size(); --j >= 0;) {
                char[] toMatch = copy.get(j);
                if (bigram[0] == toMatch[0] && bigram[1] == toMatch[1]) {
                    copy.remove(j);
                    matches += 2;
                    break;
                }
            }
        }
        //Returns the match value as percentage
        return matches * 100 / (bigram1.size() + bigram2.size());
    }

    //Sort movies based on the match value
    public static CustomArrayList<Movie> sortMovies(String query, CustomArrayList<Movie> movies) {
        CustomArrayList<Movie> searchResult = new CustomArrayList<Movie>();
        long[][] indexes = new long[movies.size()][2];
        ArrayList<char[]> queryBigram = bigram(query);

        int i = 0;
        for (Movie movie : movies) {
            indexes[i][0] = i;
            indexes[i++][1] = dice(queryBigram, bigram(movie.name));
        }

        selectionSort(indexes, false);

        for (long[] index : indexes) {
            searchResult.add(movies.get((int) index[0]));
        }

        return searchResult;
    }

    public static void selectionSort(long[][] arr, boolean ascending) {
        for (int i = 0; i < arr.length - 1; i++) {
            int index = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (ascending ? arr[j][1] < arr[index][1] : arr[j][1] > arr[index][1]) {
                    index = j;//searching for lowest index  
                }
            }
            long[] temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }
}
