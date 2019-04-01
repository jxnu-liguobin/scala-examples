import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

/**
 * 拓展MockitoSugar特质
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class ExampleMockitoSpec extends PlaySpec with MockitoSugar {
    "Has data" should {
        "return true if the data is not null" in {
            val mockDataService = mock[DateService]
            when(mockDataService.findDate) thenReturn Date(new java.util.Date())
            val actual = if (mockDataService.findDate != null) true
            actual mustBe true
        }
    }
}

trait DateService {
    def findDate: Date
}

case class Date(date: java.util.Date)
