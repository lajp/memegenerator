package o1.gui

import o1.gui.PicHistory.op.Create
import o1.gui.{Color => O1Color}
import o1.gui.colors.*

import java.awt.Graphics2D
import java.awt.Font
import java.awt.image.BufferedImage

object FontExtensions:

  o1.util.smclInit()

  // Needed for calculating image sizes from font metrics
  private val baseGraphics = new BufferedImage(100, 100 ,BufferedImage.TYPE_INT_ARGB).getGraphics

  /**
   * A function to generate a text-Pic that is automatically
   * wrapped based on the maxWidth given.
   *
   * First the function tries to wrap the text by words and if
   * that is not succesfull it wraps with no respect to word boundaries
   *
   * @param text The text included in the image
   * @param maxWidth Maximum width for the text image (can't be 0)
   * @param color The color in which the text is drawn (defaults to Black)
   * @param fontSize The font size used (defaults to 60)
   * @param fontFamily The name of the font used (defaults to "SansSerif")
   * @param bold Whether the font should be drawn in bold (defaults to false)
  */
  def wrappedTextPic(
  // TODO: Maybe implement maxHeight
      text: String,
      maxWidth: Int,
      color: O1Color = Black,
      fontSize: Int = 60,
      fontFamily: String = "SansSerif",
      bold: Boolean = false): o1.gui.Pic =

    assert(maxWidth > 0)
    val font = java.awt.Font(fontFamily, if bold then Font.BOLD else Font.PLAIN, fontSize)

    val fontMetrics = baseGraphics.getFontMetrics(font)
    val maxAscent   = fontMetrics.getMaxAscent
    val maxDescent  = fontMetrics.getMaxAscent

    var lines: Vector[Vector[String]] = Vector(text.split(" ").toVector)
    var newText = text

    while newText.split("\n").map(fontMetrics.stringWidth).max > maxWidth do
      // First try word wrap
      if lines.exists(_.size > 1) then
        val longest = lines.maxBy(_.mkString(" ").length)
        val (ls1, ls2) = longest.splitAt(longest.size/2)

        lines = lines.takeWhile(_ != longest) ++ Vector(ls1.toVector, ls2.toVector)
          ++ lines.dropWhile(_ != longest).drop(1)
        newText = lines.map(_.mkString(" ")).mkString("\n")
      // Then just wrap
      else
        val newLines = newText.split("\n").toVector
        val longestLine = newLines.maxBy(_.length)
        val ind = newLines.indexOf(longestLine)

        val rest = if newLines.length > ind+1
          then Vector(newLines(ind).init, newLines(ind).last+newLines(ind+1)) ++ newLines.drop(ind+2)
          else Vector(newLines(ind).init, newLines(ind).takeRight(1))


        newText = (newLines.take(ind) ++ rest).mkString("\n")

    val trueWidth   = newText.split("\n").map(fontMetrics.stringWidth).max
    val totalHeight = fontMetrics.getHeight*newText.split("\n").size

    val image = new BufferedImage(trueWidth, totalHeight, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = image.getGraphics.asInstanceOf[Graphics2D]

    g.setColor(color.toSwingColor)
    g.setFont(font)

    var curY = maxDescent
    newText.split("\n").foreach(line => {
      val lw = fontMetrics.stringWidth(line)
      g.drawString(line, trueWidth/2-lw/2, curY);
      curY += fontMetrics.getHeight
    })

    val possiblePic = for
      bitmap    <- smcl.infrastructure.jvmawt.RichBufferedImage(image).toSMCLBitmap
      anchor     = o1.world.objects.Anchor.Absolute.apply(Pos(0, 0))
      newHistory = PicHistory(Create(method = "Pic.generate", simpleDescription = s"Pic with the text: $text"))
    yield o1.gui.Pic(smclContent = bitmap, anchor, newHistory)

    possiblePic.getOrElse(Pic.emptyCanvas(1, 1 ,Black))
end FontExtensions
