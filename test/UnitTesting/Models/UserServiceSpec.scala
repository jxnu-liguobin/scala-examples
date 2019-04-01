package UnitTesting.Models

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

/**
 * 测试models
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class UserServiceSpec extends PlaySpec with MockitoSugar {
    "UserService#isAdmin" should {
        "be true when the role is admin" in {
            val userRepository = mock[UserRepository]
            //若传入对象满足条件，则返回角色是ADMIN
            when(userRepository.roles(models.User(11, "Steve"))) thenReturn Set(Role("ADMIN"))

            val userService = new UserService(userRepository)

            val actual = userService.isAdmin(models.User(11, "Steve"))
            val actual2 = userService.isAdmin(models.User(21, "Steve"))
            actual mustBe true
            actual2 mustBe false
        }
    }
}
