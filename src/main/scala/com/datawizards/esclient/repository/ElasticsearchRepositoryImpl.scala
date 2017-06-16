package com.datawizards.esclient.repository

import scalaj.http._
import org.json4s.jackson.Serialization
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

object ElasticsearchRepositoryImpl {
  implicit val formats = DefaultFormats

  private def mapClassToJson[T <: AnyRef](document: T): String =
    Serialization.write(document)

  private def mapJsonToClass[T: ClassTag : TypeTag](json: String): T =
    (parse(json) \ "_source").extract[T]
}

class ElasticsearchRepositoryImpl(url: String) extends ElasticsearchRepository {

  override def status(): Boolean = {
    val endpoint = url
    val request = Http(endpoint)
    val response: HttpResponse[String] = request.asString
    response.code == 200
  }

  override def updateTemplate(templateName: String, mapping: String): Unit = {
    val endpoint = url + "/_template/" + templateName
    val request = Http(endpoint).put(mapping)
    val response: HttpResponse[String] = request.asString
    if(response.code != 200) {
      throw new Exception(response.body)
    }
  }

  override def getTemplate(templateName: String): String = {
    val endpoint = url + "/_template/" + templateName
    val request = Http(endpoint)
    val response: HttpResponse[String] = request.asString
    response.body
  }

  override def createIndex(indexName: String, mapping: String): Unit = {
    val endpoint = url + "/" + indexName
    val request = Http(endpoint).put(mapping)
    val response: HttpResponse[String] = request.asString
    if(response.code != 200) {
      throw new Exception(response.body)
    }
  }

  override def getIndexSettings(indexName: String): String = {
    val endpoint = url + "/" + indexName
    val request = Http(endpoint)
    val response: HttpResponse[String] = request.asString
    response.body
  }

  override def deleteTemplate(templateName: String): Unit = {
    val endpoint = url + "/_template/" + templateName
    val request = Http(endpoint).method("DELETE")
    val response: HttpResponse[String] = request.asString
    if(response.code != 200) {
      throw new Exception(response.body)
    }
  }

  override def deleteIndex(indexName: String): Unit = {
    val endpoint = url + "/" + indexName
    val request = Http(endpoint).method("DELETE")
    val response: HttpResponse[String] = request.asString
    if(response.code != 200) {
      throw new Exception(response.body)
    }
  }

  override def templateExists(templateName: String): Boolean = {
    val endpoint = url + "/_template/" + templateName
    val request = Http(endpoint).method("HEAD")
    val response: HttpResponse[String] = request.asString
    response.code == 200
  }

  override def indexExists(indexName: String): Boolean = {
    val endpoint = url + "/" + indexName
    val request = Http(endpoint).method("HEAD")
    val response: HttpResponse[String] = request.asString
    response.code == 200
  }

  override def index[T <: AnyRef](indexName: String, typeName: String, documentId: String, document: T): Unit =
    index(indexName, typeName, documentId, ElasticsearchRepositoryImpl.mapClassToJson(document))

  override def index(indexName: String, typeName: String, documentId: String, document: String): Unit = {
    val endpoint = url + "/" + indexName + "/" + typeName + "/" + documentId
    val request = Http(endpoint).put(document)
    val response: HttpResponse[String] = request.asString
    if(response.code >= 300) {
      throw new Exception(response.body)
    }
  }

  override def read[T: ClassTag : TypeTag](indexName: String, typeName: String, documentId: String): T = {
    val endpoint = url + "/" + indexName + "/" + typeName + "/" + documentId
    val request = Http(endpoint)
    val response: HttpResponse[String] = request.asString
    if(response.code != 200) {
      throw new Exception(response.body)
    }
    ElasticsearchRepositoryImpl.mapJsonToClass(response.body)
  }

}
