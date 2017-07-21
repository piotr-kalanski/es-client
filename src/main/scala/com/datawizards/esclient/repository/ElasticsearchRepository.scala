package com.datawizards.esclient.repository

import com.datawizards.esclient.dto.{AliasAction, AliasActions, SearchResult}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait ElasticsearchRepository {
  /**
    * Check if Elasticsearch cluster is running
    */
  def status(): Boolean

  /**
    * Update Elasticsearch template
    */
  def updateTemplate(templateName: String, mapping: String): Unit

  /**
    * Get Elasticsearch template by name
    */
  def getTemplate(templateName: String): String

  /**
    * Delete Elasticsearch template
    */
  def deleteTemplate(templateName: String): Unit

  /**
    * Check if Elasticsearch template exists
    */
  def templateExists(templateName: String): Boolean

  /**
    * Create new Elasticsearch index
    */
  def createIndex(indexName: String, mapping: String): Unit

  /**
    * Get Elasticsearch index settings by name
    */
  def getIndexSettings(indexName: String): String

  /**
    * Delete Elasticsearch index
    */
  def deleteIndex(indexName: String): Unit

  /**
    * Delete Elasticsearch index if not exists
    */
  def deleteIndexIfNotExists(indexName: String): Unit =
    if(indexExists(indexName))
      deleteIndex(indexName)

  /**
    * Check if Elasticsearch index exists
    */
  def indexExists(indexName: String): Boolean

  /**
    * Write document to Elasticsearch index
    */
  def index[T <: AnyRef](indexName: String, typeName: String, documentId: String, document: T): Unit

  /**
    * Write document to Elasticsearch index
    */
  def index(indexName: String, typeName: String, documentId: String, document: String): Unit

  /**
    * Write document to Elasticsearch index - alias for index()
    */
  def write[T <: AnyRef](indexName: String, typeName: String, documentId: String, document: T): Unit =
    index(indexName, typeName, documentId, document)

  /**
    * Write document to Elasticsearch index - alias for index()
    */
  def write(indexName: String, typeName: String, documentId: String, document: String): Unit =
    index(indexName, typeName, documentId, document)

  /**
    * Writes documents to Elasticsearch index
    *
    * @param indexName index name
    * @param typeName type name
    * @param documentIdGenerator function generating document id based on document class
    * @param documents list of documents to write
    */
  def writeAll[T <: AnyRef](indexName: String, typeName: String, documents: Traversable[T])(documentIdGenerator: T => String): Unit =
    for(doc <- documents)
      write(indexName, typeName, documentIdGenerator(doc), doc)

  /**
    * Add new document to Elasticsearch index - alias for index()
    */
  def append[T <: AnyRef](indexName: String, typeName: String, document: T): Unit

  /**
    * Add new document to Elasticsearch index - alias for index()
    */
  def append(indexName: String, typeName: String, document: String): Unit

  /**
    * Add new documents to Elasticsearch index
    *
    * @param indexName index name
    * @param typeName type name
    * @param documents list of documents to write
    */
  def appendAll[T <: AnyRef](indexName: String, typeName: String, documents: Traversable[T]): Unit =
    for(doc <- documents)
      append(indexName, typeName, doc)

  /**
    * Read document from Elasticsearch index
    */
  def read[T: ClassTag: TypeTag](indexName: String, typeName: String, documentId: String): T

  /**
    * Search request without any criteria - returns first documents from index
    */
  def search[T: ClassTag: TypeTag](indexName: String): SearchResult[T]

  /**
    * Aliases operations: https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html
    */
  def aliases(actions: AliasActions): Unit
}
