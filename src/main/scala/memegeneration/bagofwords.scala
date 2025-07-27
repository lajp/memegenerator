package memegeneration

import scala.collection.mutable.Set

object bagofwords:
    var source = scala.io.Source.fromFile(s"$assets/word_to_idx.json")
    var input = try source.mkString finally source.close()
    var json = ujson.read(input)
    val word_to_idx = json.obj

    val all_words = Set[String]("Hello")
    for i <- word_to_idx.keys do
        all_words.add(i)
    val totalwords = all_words.size - 1

    def transform(list_of_words: Array[String]): Array[Int] =
        var transformed = Array.fill(totalwords)(0)
        for i <- list_of_words do
            if all_words.contains(i) then
                val word_idx = word_to_idx(i)
                transformed(word_idx.toString.toInt) += 1

        return transformed

    def test() =
        println("BoW, here!")
