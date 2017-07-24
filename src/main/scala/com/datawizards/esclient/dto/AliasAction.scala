package com.datawizards.esclient.dto

case class IndexAlias(index: String, alias: String)
sealed trait AliasAction
case class RemoveAction(remove: IndexAlias) extends AliasAction
case class AddAction(add: IndexAlias) extends AliasAction


