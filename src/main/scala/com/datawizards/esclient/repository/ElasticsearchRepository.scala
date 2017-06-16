package com.datawizards.esclient.repository

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
    * Read document from Elasticsearch index
    */
  def read[T: ClassTag: TypeTag](indexName: String, typeName: String, documentId: String): T
}
