package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
//А это имеет прямой смысл, спасибо.
//На деле в теории можно было бы и контроллеры/сервисы так спаять. В рамках данной задачи
public interface Storage<T> {
    T addData(T t);
    T updateData(T t);
    List<T> getAllData();
    T getDataById(int id);
    boolean isDataExists(int id);
}
