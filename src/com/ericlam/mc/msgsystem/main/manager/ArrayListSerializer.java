package com.ericlam.mc.msgsystem.main.manager;

import com.ericlam.mc.msgsystem.main.api.ListSerializer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ArrayListSerializer implements ListSerializer {

    @Override
    public String serialize(List<?> list) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(new Gson().toJson(list.get(i)));
            if (i != list.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public <E> List<E> deserialize(String string, Class<E> type) {
        List<E> list = new ArrayList<>();
        String[] elements = string.replace("[", "").replace("]", "").split(", ");
        for (String element : elements) {
            if (element.isBlank()) continue;
            E e = new Gson().fromJson(element, type);
            list.add(e);
        }
        return list;
    }
}
