package ru.yandex.practicum.filmorate.service.interfaces;

import java.util.List;

public interface SimpleService<T> {

    T getDataById(int id);

    List<T> getAllData();
}
