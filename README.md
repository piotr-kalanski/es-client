# es-client

Lightweight Scala Elasticsearch client implemented using Elasticsearch REST API.

[![Build Status](https://api.travis-ci.org/piotr-kalanski/es-client.png?branch=development)](https://api.travis-ci.org/piotr-kalanski/es-client.png?branch=development)
[![codecov.io](http://codecov.io/github/piotr-kalanski/es-client/coverage.svg?branch=development)](http://codecov.io/github/piotr-kalanski/es-client/coverage.svg?branch=development)
[<img src="https://img.shields.io/maven-central/v/com.github.piotr-kalanski/es-client_2.11.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22es-client_2.11%22)
[![Stories in Ready](https://badge.waffle.io/piotr-kalanski/es-client.png?label=Ready)](https://waffle.io/piotr-kalanski/es-client)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Table of contents

- [Goals](#goals)
- [Getting started](#getting-started)
- [Examples](#examples)

# Goals

- Speed up unit testing when using Spark
- Enable switching between Spark execution engine and Scala collections depending on use case, especially size of data without changing implementation

# Getting started

## Include dependencies

```scala
"com.github.piotr-kalanski" % "es-client_2.11" % "0.1.0"
```

or

```xml
<dependency>
    <groupId>com.github.piotr-kalanski</groupId>
    <artifactId>es-client_2.11</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Create Elasticsearch repository

```scala
import com.datawizards.esclient.repository.ElasticsearchRepositoryImpl

val repository = new ElasticsearchRepositoryImpl("http://localhost:9200")
```

# Examples

## Create index

```scala
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
```

## Check if index exists

```scala
repository.indexExists(indexName)
```

## Delete index

```scala
repository.deleteIndex(indexName)
```

## Writing and reading documents

```scala
case class Person(name: String, age: Int)

repository.write("people", "person", "1", Person("p1", 20))
repository.read[Person]("people", "person", "1")
```