package ru.yandex.practicum.filmorate.exeptions;
//Вообще после появления данного обобщения другий ненайдёныши можно было бы упразднить, но времени
//на рефактор пока не остается. ТЗ в этот раз объемное. И местами кривое =) Но тесты постмана
//спасают логику работы
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message){
        super(message);
    }
}
