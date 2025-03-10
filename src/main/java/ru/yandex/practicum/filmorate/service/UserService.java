package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserService {
    //Сервис зависит от интерфейса, а не его реализаций
    private final UserStorage userStorage;

    //TODO: добавление в друзья, удаление из друзей, вывод списка общих друзей


}
