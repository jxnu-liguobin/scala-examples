import org.scalatestplus.play.PlaySpec

import scala.collection.mutable

/**
 * ScalaTest+Play
 * 扩展PlaySpec特质
 */
class StackSpec extends PlaySpec {

    //MustMatchers  断言

    "Hello world" must endWith("world")

    "A Stack" must {
        "pop values in last-in-first-out order" in {
            val stack = new mutable.Stack[Int]
            stack.push(1)
            stack.push(2)
            stack.pop() mustBe 2
            stack.pop() mustBe 1
        }
        "throw NoSuchElementException if an empty stack is popped" in {
            val emptyStack = new mutable.Stack[Int]
            a[NoSuchElementException] must be thrownBy {
                emptyStack.pop()
            }
        }
    }

    //MockitoSugar 隔离外部依赖项

}
