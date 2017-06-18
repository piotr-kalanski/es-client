package com.datawizards.esclient.repository

import com.datawizards.esclient.dto.SearchResult

import scalaj.http._
import org.json4s.jackson.Serialization
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

object ElasticsearchRepositoryImpl {
  implicit val formats = DefaultFormats

  private def mapClassToJson[T <: AnyRef](document: T): String =
    Serialization.write(document)

  private def mapJsonToClass[T: ClassTag : TypeTag](json: String): T =
    (parse(json) \ "_source").extract[T]

  private def mapHitsToSearchResult[T: ClassTag : TypeTag](json: String): SearchResult[T] = {
    val result = parse(json) \ "hits"
    val total = (result \ "total").extract[Long]
    val documents = result \ "hits" \ "_source"
    val hits = documents match {
      case a:JArray =>
        for(d <- a.arr)
          yield d.extract[T]
      case o:JObject =>
        Seq(o.extract[T])
      case _ => throw new Exception("Not correct json: " + json)
    }
    SearchResult(total, hits)
  }

}

class ElasticsearchRepositoryImpl(url: String) extends ElasticsearchRepository {

  override def status(): Boolean = {
    val endpoint = url
    val request = Http(endpoint)
    Try {
      val response: HttpResponse[String] = request.asString
      response.code == 200
    }.getOrElse(false)
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

  override def append[T <: AnyRef](indexName: String, typeName: String, document: T): Unit =
    append(indexName, typeName, ElasticsearchRepositoryImpl.mapClassToJson(document))

  override def append(indexName: String, typeName: String, document: String): Unit = {
    val endpoint = url + "/" + indexName + "/" + typeName
    val request = Http(endpoint).postData(document)
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

  override def search[T: ClassTag : TypeTag](indexName: String): SearchResult[T] = {
    val endpoint = url + "/" + indexName + "/_search"
    val request = Http(endpoint)
    val response: HttpResponse[String] = request.asString
    if(response.code != 200) {
      throw new Exception(response.body)
    }
    ElasticsearchRepositoryImpl.mapHitsToSearchResult(response.body)
  }
}
