package codes.styxo.school.projects.SkyCinemasV2.Utils;

import java.util.ArrayList;

//Custom array list class with additional methods 
public class CustomArrayList<E> extends ArrayList<E> {
    //Wrapper for remove range method, since it was inaccessible
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    //Custom add to reduce a few lines/braces in the code
    //Since the original doesn't return the object
    public E customAdd(E e) {
        super.add(e);
        return e;
    }
}
