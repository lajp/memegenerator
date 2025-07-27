package memegeneration

import scala.util.matching.Regex

import scala.io.Source
import scala.util.{Success, Failure, Using}

import o1.*
import o1.gui.FontExtensions.wrappedTextPic
import memegeneration.Meme

/**
 * A "dynamic" meme object that tries to fetch a matching meme
 * from the knowyourmeme.com database.
 *
 * NOTE: The provided name has to be an exact case-insensitive match
 * (when only alphanumeric characters are considerd)
 */
object KnowYourMeme extends Meme("KnowYourMeme-fetcher",
  "A dynamic meme generator that automagically finds a template image from knowyourmeme.com"):

  private val htmlParser: Regex = """'og:image'\s+content=('(.*?)')""".r

  private def getHtmlContent(text: String) =
    val urlEnd = text.split("\n").head.split(" ").map(_.filter(_.isLetterOrDigit).toLowerCase())
      .mkString("-")
    val res = Using(Source.fromURL("https://knowyourmeme.com/memes/" + urlEnd)) {
      source => source.getLines().mkString
    }

    res.getOrElse("")

  // FIXME: Maybe create a proper baseImg for this
  def getBaseImg: Pic = Pic(s"$assets/facepalm.png")

  /**
   * Matches any string that has an exact case-insensitive,
   * alphanumeric-only match in the knowyourmeme.com database
   *
   * NOTE: This function is significantly slower than the other
   * matchesText-functions due to having to perform HTTP-requests
   */
  def matchesText(text: String) =
    val content = getHtmlContent(text)

    htmlParser.findFirstMatchIn(content) match
    case Some(m) => m.group(2).nonEmpty
    case _ => false

  def withText(text: String): Pic =
    val content = getHtmlContent(text)
    val memeTexts = text.split("\n").tail

    val imgUrl: String = htmlParser.findFirstMatchIn(content).get.group(2)
    var meme = Pic(imgUrl)

    val topTextPos = Pos(meme.width/2, 10)
    val bottomTextPos = Pos(meme.width/2, meme.height)

    if memeTexts.nonEmpty then
      val topTextImg = wrappedTextPic(memeTexts(0).toUpperCase, meme.width.toInt, Black, meme.width.toInt / 10, "Mono", true)
      meme = meme.place(topTextImg, topTextPos.addX(-topTextImg.width/2))
      memeTexts.lift(1) match
        case Some(t) =>
          val bottomTextImg = wrappedTextPic(t.toUpperCase, meme.width.toInt, Black, meme.width.toInt / 10, "Mono", true)
          meme = meme.place(bottomTextImg, bottomTextPos.addY(-bottomTextImg.height).addX(-bottomTextImg.width/2))
        case _ => ()

    meme


end KnowYourMeme
