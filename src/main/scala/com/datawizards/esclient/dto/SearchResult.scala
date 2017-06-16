package com.datawizards.esclient.dto

case class SearchResult[T](
  total: Long,
  hits: Traversable[T]
)
