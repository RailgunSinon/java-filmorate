package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import ru.yandex.practicum.filmorate.models.User;

//А это имеет прямой смысл, спасибо.
//На деле в теории можно было бы и контроллеры/сервисы так спаять. В рамках данной задачи
public interface UserStorage {
    User addData(User t);
    User updateData(User t);
    List<User> getAllData();
    User getDataById(int id);
    boolean isDataExists(int id);
}
