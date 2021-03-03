package io.github.dreamylost.zio

import zio.Chunk

import scala.reflect.ClassTag

/**
 *
 * @author 梦境迷离
 * @version 1.0,2021/3/3
 */
object ZIOChunks {

  def sequence[A, B: ClassTag](s: Chunk[Either[A, B]]): Either[A, Chunk[B]] = {
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) =>
        for {
          xs <- Right(acc).value
          x <- Right(e).value
        } yield x :: xs
    }.map(r => Chunk.fromArray(r.toArray))
  }

}
