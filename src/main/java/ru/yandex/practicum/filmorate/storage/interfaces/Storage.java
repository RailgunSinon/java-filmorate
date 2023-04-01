package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

//Тогда можно вернуться к можели одного интерфейса
public interface Storage<T> {

    T addData(T t);

    T updateData(T t);

    List<T> getAllData();

    T getDataById(int id);

    boolean isDataExists(int id);
}
