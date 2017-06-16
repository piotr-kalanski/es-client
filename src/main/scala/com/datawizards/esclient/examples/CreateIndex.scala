package com.datawizards.esclient.examples

import com.datawizards.esclient.repository.ElasticsearchRepositoryImpl

object CreateIndex extends App {
  val repository = new ElasticsearchRepositoryImpl("http://localhost:9200")
  val mapping =
    """{
      |   "mappings" : {
      |      "Person" : {
      |         "properties" : {
      |            "name" : {"type" : "string"},
      |            "age" : {"type" : "integer"}
      |         }
      |      }
      |   }
      |}""".stripMargin

  val indexName = "persontest"
  repository.createIndex(indexName, mapping)
  println(repository.getIndexSettings(indexName))
  println(repository.indexExists(indexName))
  println(repository.deleteIndex(indexName))
  println(repository.indexExists(indexName))
}
