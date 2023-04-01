package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship implements Comparable<Friendship> {

    private int friendId;
    private boolean status;

    @Override
    public int compareTo(Friendship o) {
        if (o.getFriendId() == this.getFriendId() && o.status == this.status) {
            return 0;
        } else if (this.getFriendId() > o.getFriendId()) {
            return 1;
        } else {
            return -1;
        }
    }
}
