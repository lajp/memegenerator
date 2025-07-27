package memegeneration

import upickle.default._
import scala.collection.mutable.ArrayBuffer
import breeze.linalg._
import scala.collection.mutable.Set
import scala.io.Source
import scala.util.matching.Regex

import o1.*
import o1.gui.FontExtensions.wrappedTextPic

object Cnn:
  val source = scala.io.Source.fromFile(s"$assets/coefs.json")
  val input = try source.mkString finally source.close()
  val json = ujson.read(input)

  // Same in 1 go
  val json_matrix = json.arr.map(_.arr.map(_.arr.map(_.num).toArray).toArray).map(n => DenseMatrix(n:_*))

  //Few needed things:
  val BoW = bagofwords
  val outputs: Vector[String] = Vector("advice_dog.png", "all_the_things.png", "creepy_wonka.png",
    "optimistic_crab.png", "programmer.png", "sad_keanu.png", "staredad.png", "batman_panel.png")


  def get_a_result(input: String): String =
    val input_layer = BoW.transform(input.split(" "))  //Splits the sentence to list of words
    var input_layers = input_layer.map(_.toDouble)     //For somereason Breeze libary needs Doubles instead of int
    var result = DenseMatrix(input_layers:_*)

    for i <- json_matrix do
        val j = i.t //Take a transpose to make dimensions match
        result = (j * result).toDenseMatrix //Actuall prediction process

    val index = result.toArray.zipWithIndex.maxBy(_._1)._2 //which index was the final prediction?
    println(outputs(index))
    return outputs(index)

  def test() =
    println("hello")
end Cnn

/**
 * A special fallback meme-object that utilizes a pre-trained
 * CNN to determin which of the images in our library
 * best fits the user-provided caption (text)
 */
object CnnMeme extends Meme("CnnMeme",
  "Cnn - You can add bottom text by writing on another row"):

  private val fontSize = 50

  // FIXME: Maybe create a proper baseImg for this
  private var baseImg = Pic(s"$assets/facepalm.png")

  private var topTextPos = Pos(baseImg.width/2, 10)
  private var bottomTextPos = Pos(baseImg.width/2, baseImg.height)

  private val cnn = Cnn

  def getBaseImg: Pic = baseImg

  /**
   * Matches everything
   */
  def matchesText(text: String) = true

  def withText(text: String) =
    val img = cnn.get_a_result(text)
    val baseImg = Pic(s"$assets/$img")
    topTextPos = Pos(baseImg.width/2, 10)
    bottomTextPos = Pos(baseImg.width/2, baseImg.height)

    val fontSize = baseImg.width.toInt / 14

    val lines = text.split("\n")
    if lines.size > 1 then
      val topText = lines.head
      val bottomText = lines.tail.mkString(" ")

      val topTextImg = wrappedTextPic(topText.toUpperCase, baseImg.width.toInt, White, fontSize, "Mono", true)
      val bottomTextImg = wrappedTextPic(bottomText.toUpperCase, baseImg.width.toInt, White, fontSize, "Mono", true)
      baseImg.place(bottomTextImg, bottomTextPos.addX(-bottomTextImg.width/2).addY(-bottomTextImg.height))
        .place(topTextImg, topTextPos.addX(-topTextImg.width/2))
    else
      val textImg = wrappedTextPic(lines.mkString(" ").toUpperCase, baseImg.width.toInt, White, fontSize, "Mono", true)
      baseImg.place(textImg, topTextPos.addX(-textImg.width/2))
end CnnMeme
