package com.ehealthinformatics.app.activity.utils;

import com.ehealthinformatics.data.dto.Category;
import com.ehealthinformatics.data.dto.SimpleItem;
import com.ehealthinformatics.data.dto.Uom;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static List<SimpleItem> toSimpleItems (List<Category> categories) {
        ArrayList<SimpleItem> simpleItems = new ArrayList<>();
        int index = categories.size();
        while (index-- > 0){
            Category category = categories.get(index);
            simpleItems.add(new SimpleItem(category.getId()+"", category.getName()));
        }
        return simpleItems;
    }

    public static List<SimpleItem> toSimpleItems2 (List<Uom> uoms) {
        ArrayList<SimpleItem> simpleItems = new ArrayList<>();
        int index = uoms.size();
        while (index-- > 0){
            Uom uom = uoms.get(index);
            simpleItems.add(new SimpleItem(uom.getId()+"", uom.getName()));
        }
        return simpleItems;
    }


}
