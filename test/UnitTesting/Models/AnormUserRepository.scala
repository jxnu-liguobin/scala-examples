package UnitTesting.Models

import models.User

/**
 * DAO实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-1
 */
class AnormUserRepository extends UserRepository {
    override def roles(user: User): Set[Role] = {
        Set(Role("ADMIN"))
    }
}