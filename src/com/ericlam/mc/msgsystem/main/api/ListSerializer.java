package com.ericlam.mc.msgsystem.main.api;

import java.util.List;

public interface ListSerializer {

    String serialize(List<?> list);

    <E> List<E> deserialize(String string, Class<E> type);

}
