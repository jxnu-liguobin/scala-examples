package UnitTesting.Models

import models.User

/**
 * DAO接口层
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-1
 */
trait UserRepository {
    def roles(user: User): Set[Role]
}