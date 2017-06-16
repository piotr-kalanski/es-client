package com.datawizards.esclient.examples

import com.datawizards.esclient.repository.ElasticsearchRepositoryImpl

object WriteReadDocument extends App {
  val repository = new ElasticsearchRepositoryImpl("http://localhost:9200")

  case class Person(name: String, age: Int)

  repository.write("people", "person", "1", Person("p1", 20))
  repository.write("people", "person", "2", Person("p2", 30))
  println(repository.read[Person]("people", "person", "1"))
  println(repository.read[Person]("people", "person", "2"))
}
