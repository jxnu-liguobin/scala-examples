package UnitTesting.Models

import models.User

/**
 * 用户逻辑层
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-1
 * @param userRepository
 */
class UserService(userRepository: UserRepository) {

    /**
     * 判断用户是否是admin角色
     *
     * @param user
     * @return
     */
    def isAdmin(user: User): Boolean = {
        println(user.toString)
        val roles = userRepository.roles(user)
        if (roles != null) roles.contains(Role("ADMIN")) else false
    }
}