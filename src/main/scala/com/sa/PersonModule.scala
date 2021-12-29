package com.sa

import com.sa
import zio._

trait PersonModule {
  val xModule: PersonModule.Service[Any]
}

object PersonModule {

//  case class X(x: String = "x", y: String = "y", z: String = "z")

  case class Person(id: Int, name: String, age: Int)

  trait Service[R] {
    def x: ZIO[R, Nothing, Person]
  }

  trait Live extends PersonModule {
    val xInstance: Person

    val xModule: sa.PersonModule.Service[Any] = new Service[Any] {
      override def x: ZIO[Any, Nothing, Person] = UIO(xInstance)
    }
  }

  object factory extends sa.PersonModule.Service[PersonModule] {
    override def x: ZIO[PersonModule, Nothing, Person] =
      ZIO.environment[PersonModule].flatMap(_.xModule.x)
  }


//  trait Live extends XModule {
//    val xInstance: X
//
//    val xModule: XModule.Service[Any] = new Service[Any] {
//      override def x: ZIO[Any, Nothing, X] = UIO(xInstance)
//    }
//  }

//  object factory extends XModule.Service[XModule] {
//    override def x: ZIO[XModule, Nothing, X] = ZIO.environment[XModule].flatMap(_.xModule.x)
//  }
}