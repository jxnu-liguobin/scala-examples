package io.github.dreamylost.zio

import zio.{ ExitCode, URIO, ZIO }
import zio.console._

import java.io.IOException

object MyApp extends zio.App {

  def run(args: List[String]): URIO[Console, ExitCode] = {
    myAppLogic.exitCode

  }

  val myAppLogic: ZIO[Console, IOException, Unit] = {
    for {
      _ <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _ <- putStrLn(s"Hello, $name, welcome to ZIO!")
    } yield ()
  }

}