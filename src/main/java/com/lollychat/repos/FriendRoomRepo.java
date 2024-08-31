package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import com.lollychat.model.FriendRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRoomRepo extends JpaRepository<FriendRoom, Long> {
    @Query("SELECT fr FROM FriendRoom fr JOIN fr.users u1 JOIN fr.users u2 " +
            "WHERE u1 = :user1 AND u2 = :user2")
    FriendRoom findByUsersInRoom(@Param("user1") ChatUser user1, @Param("user2") ChatUser user2);
    //напоминалка за квері:
    /*
    JOIN fr.users u1 JOIN fr.users u2:
JOIN fr.users u1: Ця частина запиту приєднує колекцію users з FriendRoom, використовуючи псевдонім u1.
Оскільки FriendRoom має колекцію users, ви повинні здійснити приєднання до цієї колекції для фільтрації.
JOIN fr.users u2: Подібно, ця частина приєднує колекцію users знову, але використовує псевдонім u2.
Це дозволяє вам порівнювати двох різних користувачів у кімнаті.
WHERE u1 = :user1 AND u2 = :user2:
WHERE u1 = :user1: Це умова фільтрує результати, включаючи тільки ті FriendRoom, де u1 співпадає з параметром user1.
AND u2 = :user2: Ця умова додає додатковий фільтр, включаючи лише ті FriendRoom, де u2 співпадає з параметром user2.
     */

}
