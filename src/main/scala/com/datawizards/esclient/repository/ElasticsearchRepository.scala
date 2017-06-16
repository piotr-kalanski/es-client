package com.datawizards.esclient.repository

trait ElasticsearchRepository {
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
    * Check if Elasticsearch index exists
    */
  def indexExists(indexName: String): Boolean
}
