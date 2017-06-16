package com.datawizards.esclient

import com.datawizards.esclient.repository.ElasticsearchRepositoryImpl
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import TestModel._

@RunWith(classOf[JUnitRunner])
class ElasticsearchRepositoryIntegrationTest extends FunSuite {
  private val repository = new ElasticsearchRepositoryImpl("http://localhost:9200")
  private val mapping =
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

  test("Run tests") {
    // Run integration tests if Elasticsearch cluster is running
    if(repository.status()) {
      createIndexTest()
      writeReadDocumentTest()
    }
    else {
      println("Elasticsearch instance not running!")
    }
  }

  private def createIndexTest(): Unit = {
    val indexName = "persontest"
    repository.deleteIndexIfNotExists(indexName)
    repository.createIndex(indexName, mapping)
    assert(repository.indexExists(indexName))
    repository.deleteIndex(indexName)
    assert(!repository.indexExists(indexName))
  }

  private def writeReadDocumentTest(): Unit = {
    val person = Person("p1", 30)
    val indexName = "people"
    val typeName = "person"
    val id = "1"
    repository.deleteIndex(indexName)
    repository.createIndex(indexName, mapping)
    repository.write(indexName, typeName, id, person)
    assertResult(person) {
      repository.read[Person](indexName, typeName, id)
    }
  }

}
