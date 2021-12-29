//package com.sa
//
//import com.typesafe.config.ConfigFactory
//import io.getquill.mirrorContextWithQueryProbing.{query, quote}
//import io.getquill.{H2JdbcContext, JdbcContextConfig, SnakeCase}
//import io.getquill._
//import io.getquill.mirrorContextWithQueryProbing.{query,quote,querySchema,insertMeta,lift}
//
//object TestQuill extends App{
//
//
////  val dc = JdbcContextConfig(ConfigFactory.empty()).dataSource
//  lazy val ctx = new H2JdbcContext(Literal, "testH2DB")
//  println(s"dc: $ctx")
//
////  lift
//  case class Person(name: String, age: Int)
//
//  val person = quote { querySchema[Person]("Person") }
//
//  implicit val personInsertMeta = insertMeta[Person](_.name)
//
//  val q = quote { person.insert(lift(Person("John",45)))  }
//
//  ctx.run(q)
//
////  val q = quote {
////    query[Person].filter(p => p.name == "John").map(p => p.age)
////  }
//
////  ctx.run(q)
////  val trans =
////    ctx.transaction {
////      for {
////        _ <- ctx.run(query[Person].delete)
////        _ <- ctx.run(query[Person].insert(Person("Joe", 123)))
////        p <- ctx.run(query[Person])
////      } yield p
////    } //returns: Task[List[Person]]
//
////  val result = trans.
////  val del = ctx.run(query[Product].delete)
//}
