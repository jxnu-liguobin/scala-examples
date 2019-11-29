package io.github.dreamylost.actor

import akka.actor.{ Actor, ActorIdentity, ActorRef, Identify, Terminated }

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
object Selection_Actor {

  /**
   * actorSelection
   * 每个actor都有一个唯一的逻辑路径，该路径是通过从子actor到父级直到系统actor的根开始遵循actor链来获得的，并且它具有一条物理路径，如果监管链包括任何远程主管，则可能会有所不同
   **/
  class Follower extends Actor {


    val identifyId = 1
    context.actorSelection("/user/another") ! Identify(identifyId)

    def receive = {
      case ActorIdentity(`identifyId`, Some(ref)) =>

        /**
         * Registers this actor as a Monitor for the provided ActorRef.
         * This actor will receive a Terminated(subject) message when watched actor is terminated.
         */
        context.watch(ref)

        /**
         * Changes the Actor's behavior to become the new 'Receive' (PartialFunction[Any, Unit]) handler.
         */
        context.become(active(ref))
      case ActorIdentity(`identifyId`, None) => context.stop(self)

    }

    def active(another: ActorRef): Actor.Receive = {
      case Terminated(`another`) => context.stop(self)
    }
  }

}
