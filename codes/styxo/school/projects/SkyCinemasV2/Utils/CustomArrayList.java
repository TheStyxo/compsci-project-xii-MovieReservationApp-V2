package codes.styxo.school.projects.SkyCinemasV2.Utils;

import java.util.ArrayList;

//Custom array list class with additional methods 
public class CustomArrayList<E> extends ArrayList<E> {
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    public E customAdd(E e) {
        super.add(e);
        return e;
    }
}
