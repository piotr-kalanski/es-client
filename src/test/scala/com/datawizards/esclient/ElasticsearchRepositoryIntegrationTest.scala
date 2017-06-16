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
      writeMultipleDocumentsTest()
      appendSingleDocumentTest()
      appendMultipleDocumentsTest()
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
    repository.deleteIndexIfNotExists(indexName)
    repository.createIndex(indexName, mapping)
    repository.write(indexName, typeName, id, person)
    assertResult(person) {
      repository.read[Person](indexName, typeName, id)
    }
  }

  private def writeMultipleDocumentsTest(): Unit = {
    val indexName = "peoplemultiple"
    val typeName = "person"
    val p1 = Person("p1", 1)
    val p2 = Person("p2", 2)
    val data = Seq(p1, p2)
    repository.deleteIndexIfNotExists(indexName)
    repository.createIndex(indexName, mapping)
    repository.writeAll(indexName, typeName, data) {
      p => p.name
    }
    assertResult(p1){
      repository.read[Person](indexName, typeName, "p1")
    }
    assertResult(p2){
      repository.read[Person](indexName, typeName, "p2")
    }
  }

  private def appendSingleDocumentTest(): Unit = {
    val indexName = "peoplesingleappend"
    val typeName = "person"
    val p1 = Person("p1", 1)
    repository.deleteIndexIfNotExists(indexName)
    repository.createIndex(indexName, mapping)
    repository.append(indexName, typeName, p1)
    Thread.sleep(1000)
    val result = repository.search[Person](indexName)
    assertResult(1)(result.total)
    assert(result.hits.exists(p => p == p1))
  }

  private def appendMultipleDocumentsTest(): Unit = {
    val indexName = "peoplemultipleappend"
    val typeName = "person"
    val p1 = Person("p1", 1)
    val p2 = Person("p2", 2)
    repository.deleteIndexIfNotExists(indexName)
    repository.createIndex(indexName, mapping)
    repository.appendAll(indexName, typeName, Seq(p1, p2))
    Thread.sleep(1000)
    val result = repository.search[Person](indexName)
    assertResult(2)(result.total)
    assert(result.hits.exists(p => p == p1))
    assert(result.hits.exists(p => p == p2))
  }

}
