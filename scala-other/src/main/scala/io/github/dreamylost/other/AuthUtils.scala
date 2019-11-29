package io.github.dreamylost.other

import org.apache.commons.codec.binary.Hex

object AuthUtils extends App {

  import javax.crypto.Mac
  import javax.crypto.spec.SecretKeySpec

  def authToken(secret: String, project: String, ai: String, tm: Long) = {
    val messages = s"POST\n/auth/token\nproject=$project&ai=$ai&tm=$tm"
    val hmac = Mac.getInstance("HmacSHA256")
    hmac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"))
    val signature = hmac.doFinal(messages.getBytes("UTF-8"))
    Hex.encodeHexString(signature)
  }

  val time = System.currentTimeMillis()


  /**
   * {
   * "status": "success",
   * "code": ""
   * }
   */
  println(authToken("", "4PYJMWoM", "", time))
  println(time)

}