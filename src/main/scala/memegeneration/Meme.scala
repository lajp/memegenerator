package memegeneration

val assets = sys.env.get("ASSETS_PATH").getOrElse("./assets")

import memegeneration.OneDoesNotSimply.{imageWithText, baseImg}
import o1.*
import o1.gui.FontExtensions.wrappedTextPic

import scala.util.matching.Regex

/**
 * A singleton object for interfacing with all the memes
 * contains a Vector of all the implemented memes and a
 * helper method generateMathing.
 *
 * NOTE: When implementing support for a new meme
 * one should also add that meme to the memes Vector
*/
object Memes:
  /**
   * A Vector defined on compile-time that contains all the
   * supported memes
  */
  val memes: Vector[Meme] = Vector(NoBitches, OneDoesNotSimply, Facepalm, HideThePain, GigaChadEnjoyer,
                                   WomanYellingAtACat, PiperPerri, SusJerma,
                                   // NOTE: These two have to be the last ones in this Vector
                                   KnowYourMeme, CnnMeme)

  /**
   * A function that takes in a string of text
   * and generates the first meme that matches that text
   *
   * @param text A String based on which the meme is generated
   * @return Option[Pic], a generated meme or None if nothing matches the text
  */
  def generateMatching(text: String): Option[Pic] =
    memes.find(_.matchesText(text)) match
      case Some(m) => Some(m.withText(text))
      case _ => None


end Memes

/**
 * A trait that depicts a meme.
 * Supported memes are created as singleton objects
 * that extend (implement) this trait.
 * The trait has two methods, a name and a description.
 *
 * @param name A name for the meme.
 * @param description A description of the meme.
*/
trait Meme(val name: String, val description: String):
  /**
   * A method that returns true if the text matches
   * the Meme-object and false otherwise
   *
   * @param text The text to be checked
   * @return Boolean whether the text matches the Meme
   */
  def matchesText(text: String): Boolean

  /**
   * A method that generates the Meme with given text
   *
   * @param text The text to be used in the generation
   * @return Pic the generated meme
   */
  def withText(text: String): Pic

  /**
   * A method that returns the base-image (aka. template)
   * for the meme. This can be used in a meme listing.
   */
  def getBaseImg: Pic
end Meme

/**
 * A singleton object depicting the "No bitches? -meme"
 * https://knowyourmeme.com/memes/no-bitches
*/
object NoBitches extends Meme("No bitches?", "No bithces? - Keywords: no + words of your choice + ?"):
  private val matcher: Regex = """(?i)no\s+(.*)?\?""".r

  private val fontSize = 72

  private val baseImg = Pic(s"$assets/nobitches.png")
  private val coords = Pos(baseImg.width/2, 10)

  def getBaseImg: Pic = baseImg

  /**
   * Matches any string that starts with "no " and ends in a question mark (case-insensitive)
   */
  def matchesText(text: String) = matcher.matches(text)

  def withText(text: String) =
    val textImg = wrappedTextPic(text.toUpperCase, baseImg.width.toInt, White, fontSize, "Mono", true)
    baseImg.place(textImg, coords.addX(-textImg.width/2))
end NoBitches

/**
 * A singleton object depicting the "One does not simply -meme"
 * https://knowyourmeme.com/memes/one-does-not-simply-walk-into-mordor
*/
object OneDoesNotSimply extends Meme("One does not simply walk into mordor",
  "One does not simply walk into mordor - Keyword: One does not simply + words of your choice"):

  private val matcher: Regex = """(?i)one\s+does\s+not\s+simply\s+(.*)?""".r

  private val fontSize = 42

  private val baseImg = Pic(s"$assets/onedoesnotsimply.png")

  private val topText = wrappedTextPic("ONE DOES NOT SIMPLY", baseImg.width.toInt, White, fontSize, "Mono", true)
  private val topTextPos = Pos(baseImg.width/2, 10).addX(-topText.width/2)
  private val bottomTextPos = Pos(baseImg.width/2, baseImg.height)

  private val imageWithText = baseImg.place(topText, topTextPos)

  def getBaseImg: Pic = baseImg

  /**
   * Matches any string starting with "one does not simply" (case-insensitive)
   */
  def matchesText(text: String) = matcher.matches(text)

  def withText(text: String) =
    val bottomText = matcher.findFirstMatchIn(text).get.group(1)
    val bottomImg = wrappedTextPic(bottomText.toUpperCase, imageWithText.width.toInt, White, fontSize, "Mono", true)
    imageWithText.place(bottomImg, bottomTextPos.addX(-bottomImg.width/2).addY(-bottomImg.height))
end OneDoesNotSimply

/**
 * A singleton object depicting the "Facepalm-meme"
 * https://knowyourmeme.com/memes/facepalm
*/
object Facepalm extends Meme("Facepalm",
  "Facepalm - keyword: facepalm + words of your choice\nYou can add bottom text by writing on another row"):

  private val matcher: Regex = """(?i)facepalm\s+((.|\n)*)?""".r

  private val fontSize = 50

  private val baseImg = Pic(s"$assets/facepalm.png")

  private val topTextPos = Pos(baseImg.width/2, 10)
  private val bottomTextPos = Pos(baseImg.width/2, baseImg.height)

  def getBaseImg: Pic = baseImg

  /**
   * Matches any string that starts with "facepalm " (case-insensitive)
   */
  def matchesText(text: String) = matcher.matches(text)

  def withText(text: String) =
    val memeText = (matcher.findFirstMatchIn(text).get).group(1)
    val lines = memeText.split("\n")

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
end Facepalm


/**
 * A singleton object depicting the "Hide the pain Harold-meme"
 * https://knowyourmeme.com/memes/hide-the-pain-harold
*/
object HideThePain extends Meme("Hide the pain Harold",
  "Hide the pain Harold - keyword: hide the pain + words of your choice\nYou can divide the text by writing on another row"):

  private val matcher: Regex = """(?i)hide\s+the\s+pain(\s|\n)+(.*)(\s|\n)+(.*)""".r

  private val fontSize = 42

  private val baseImg = Pic(s"$assets/hidethepain.png")

  private val topTextPos = Pos(baseImg.width/2, baseImg.height/2)
  private val bottomTextPos = Pos(baseImg.width/2, baseImg.height)

  def getBaseImg: Pic = baseImg

  /**
   * Matches a string that starts with "hide the pain " and has at least two lines (case-insensitive)
   */
  def matchesText(text: String) = matcher.findFirstMatchIn(text) match
    case Some(m) => m.group(2).nonEmpty && m.group(4).nonEmpty
    case _ => false

  def withText(text: String) =
    val m = matcher.findFirstMatchIn(text).get
    val (top, bottom) = (m.group(2), m.group(4))

    val topTextImg = wrappedTextPic(top.toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)
    val bottomTextImg = wrappedTextPic(bottom.toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)
    baseImg.place(bottomTextImg, bottomTextPos.addX(-bottomTextImg.width/2).addY(-bottomTextImg.height))
      .place(topTextImg, topTextPos.addX(-topTextImg.width/2).addY(-topTextImg.height))
end HideThePain

/**
 * A singleton object depicting the "Average Chad Enjoyer-meme"
 * https://knowyourmeme.com/memes/gigachad
*/
object GigaChadEnjoyer extends Meme("Average Chad Enjoyer",
  "Giga Chad Enjoyer - keyword: average + words of your choice + enjoyer"):

  private val matcher: Regex = """(?i)average\s+(.*)?\s+enjoyer""".r

  private val baseImg = Pic(s"$assets/gigachad.png")

  private val coords = Pos(baseImg.width/2, 10)
  private val bottomTextPos = Pos(baseImg.width/2, baseImg.height)

  def getBaseImg: Pic = baseImg

  /**
   * Matches any string that starts with "average" and ends in a "enjoyer" (case-insensitive)
   */
  def matchesText(text: String) = matcher.matches(text)

  def withText(text: String) =
    val topImage = wrappedTextPic(text.dropRight(7).toUpperCase, baseImg.width.toInt, White, 70, "Mono", true)
    val bottomImg =  wrappedTextPic("ENJOYER", baseImg.width.toInt, White, 100, "Mono", true)
    val imageWithEnjoyer = baseImg.place(bottomImg, bottomTextPos.addX(-bottomImg.width/2).addY(-bottomImg.height))
    imageWithEnjoyer.place(topImage, coords.addX(-topImage.width/2))
end GigaChadEnjoyer

/**
 * A singleton object depicting the "Woman yelling at a cat" -meme
 * https://knowyourmeme.com/memes/woman-yelling-at-a-cat
*/
object WomanYellingAtACat extends Meme("Woman yelling at a Cat",
  "Woman yelling at a Cat - keywords (both required):\nwoman: + words of your choice\ncat: + words of your choice"):

  private val matcher: Regex = """(?i)woman:\s+(.*)\n\s*cat:\s+(.*)""".r

  private val woman = Pic(s"$assets/woman.png")
  private val cat = Pic(s"$assets/cat.png")

  private val baseImg = woman.leftOf(cat)

  private val fontSize = 50

  def getBaseImg: Pic = baseImg

  /**
   * Matches any string that has two lines, first of which starts with "woman: "
   * and second of which starts with "cat: " (case-insensitive)
   */
  def matchesText(text: String) = matcher.matches(text)

  def withText(text: String) =
    val m = matcher.findFirstMatchIn(text).get
    val (w, c) = (m.group(1), m.group(2))

    var womanText = wrappedTextPic(w, woman.width.toInt, Black, fontSize, "Mono")
    var catText = wrappedTextPic(c, cat.width.toInt, Black, fontSize, "Mono")

    val (wh, ch) = (womanText.height, catText.height)

    if wh > ch then
      catText = catText.below(rectangle(cat.width, wh-ch, White))
    else if ch > wh then
      womanText = womanText.below(rectangle(woman.width, ch-wh, White))

    val womanPic = womanText.above(woman)
    val catPic = catText.above(cat)

    womanPic.leftOf(catPic)
end WomanYellingAtACat

/**
 * A singleton object depicting the "Piper Perri Surrounded-meme"
 * https://knowyourmeme.com/memes/piper-perri-surrounded
*/
object PiperPerri extends Meme("Piper Perri Surrounded",
  "Piper Perri Surrounded - keyword: piper perri + optional word(s) + surrounded by + (words, words, words, words, words)"):

  def getBaseImg: Pic = baseImg

  private val matcher: Regex = """(.*)\s?+surrounded\s+by\s+(.*)?""".r

  private val fontSize = 28

  private val baseImg = Pic(s"$assets/piperperri.png")

  private val dude1Pos = Pos(0, baseImg.height*0.15)
  private val dude2Pos = Pos(baseImg.width*0.30, 0)
  private val dude3Pos = Pos(baseImg.width*0.51, baseImg.height*0.1)
  private val dude4Pos = Pos(baseImg.width*0.73, baseImg.height*0.12)
  private val dude5Pos = Pos(baseImg.width*0.9, baseImg.height*0.26)
  private val piperTextPos = Pos(baseImg.width*0.63, baseImg.height*0.82)

  /**
   * Matches a string that starts with "piper perri (possible words here) surrounded by" and ends with chosen words (case-insensitive)
   */
  def matchesText(text: String) = matcher.findFirstMatchIn(text) match
    case Some(m) => m.group(1).nonEmpty || m.group(2).nonEmpty
    case _ => false

  def withText(text: String) =
    val m = matcher.findFirstMatchIn(text).get
    val (perri, dudes) = (m.group(1), m.group(2))
    val dudesTextArray = dudes.split(", ").map(_.trim())

    // FIXME: this could be optimized
    val dude1 = if dudesTextArray.nonEmpty then
      wrappedTextPic(dudesTextArray(0).toUpperCase, (baseImg.width*0.2).toInt, Black, fontSize, "Mono", true)
    else wrappedTextPic(" ".toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)

    val dude2 = if dudesTextArray.size > 1 then
      wrappedTextPic(dudesTextArray(1).toUpperCase, (baseImg.width*0.2).toInt, Black, fontSize, "Mono", true)
    else wrappedTextPic(" ".toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)

    val dude3 = if dudesTextArray.size > 2 then
      wrappedTextPic(dudesTextArray(2).toUpperCase, (baseImg.width*0.2).toInt, Black, fontSize, "Mono", true)
    else wrappedTextPic(" ".toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)

    val dude4 = if dudesTextArray.size > 3 then
      wrappedTextPic(dudesTextArray(3).toUpperCase, (baseImg.width*0.2).toInt, Black, fontSize, "Mono", true)
    else wrappedTextPic(" ".toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)

    val dude5 = if dudesTextArray.size > 4 then
      wrappedTextPic(dudesTextArray(4).toUpperCase, (baseImg.width*0.2).toInt, Black, fontSize, "Mono", true)
    else wrappedTextPic(" ".toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)

    val bottomTextImg = wrappedTextPic((perri+" ").toUpperCase, baseImg.width.toInt, Black, fontSize, "Mono", true)
    baseImg.place(bottomTextImg, piperTextPos.addX(-bottomTextImg.width/2).addY(-bottomTextImg.height))
      .place(dude1, dude1Pos.addX(-dude1.width/2).addY(-dude1.height/2).clamp(0.0, baseImg.width, 0.0, baseImg.height))
      .place(dude2, dude2Pos.addX(-dude2.width/2).addY(-dude2.height/2).clamp(-300, baseImg.width, 0.0, baseImg.height))
      .place(dude3, dude3Pos.addX(-dude3.width/2).addY(-dude3.height/2).clamp(-300, baseImg.width, 0.0, baseImg.height))
      .place(dude4, dude4Pos.addX(-dude4.width/2).addY(-dude4.height/2).clamp(-300, baseImg.width, 0.0, baseImg.height))
      .place(dude5, dude5Pos.addX(-dude5.width/2).addY(-dude5.height/2).clamp(-300, baseImg.width, 0.0, baseImg.height))
end PiperPerri

/**
 * A singleton object depicting the "Jerma Sus-meme"
 * https://knowyourmeme.com/memes/when-the-imposter-is-sus-sus-jerma
*/
object SusJerma extends Meme("Sus Jerma", "Sus Jerma - keyword: words of your choice + is sus"):

  private val matcher: Regex = """(?i)(.*)\s+is sus""".r

  private val baseImg = Pic(s"$assets/jermasus.png")

  private val flushed = Pic(s"$assets/flushedemoji.png")

  private val fontSize = 40

  def getBaseImg: Pic = baseImg

  /**
   * Matches any string that ends with "is sus" (case-insensitive)
   */
  def matchesText(text: String) = matcher.matches(text)

  def withText(text: String) =
    val m = matcher.findFirstMatchIn(text).get
    val sus = m.group(1)

    var susText = wrappedTextPic(sus + " is sus!", (baseImg.width*0.9).toInt, Black, fontSize, "Mono")
    var susImage = susText.scaleTo(susText.width*1.1, susText.height*0.95)
      .leftOf(flushed.scaleTo(baseImg.width*0.1, susText.height)).scaleTo(baseImg.width, susText.height)

    susImage.above(baseImg)
end SusJerma
