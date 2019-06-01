package actor

import actor.HelloActor.SayHello
import akka.actor.{Actor, Props}

/**
 * actor
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-30
 */
class HelloActor extends Actor {

    override def receive: Receive = {
        case SayHello(name: String) => {
            //回复发送者
            sender() ! "Hello " + name
        }
        case _ => {
            sender() ! "Not found"
        }
    }
}

object HelloActor {

    def props = Props[HelloActor]

    case class SayHello(name: String)

}