package s1.telegrambots
import s1.telegrambots.BasicBot

import scala.util.Using
import scala.io.Source
import memegeneration.Memes
import o1.*

object YourBot extends App:
    object Bot extends BasicBot:

        /**
         * TODO: Luokaa bottinne tähän metodeineen ja reagoijineen.
         */

        def memeReply(msg: Message) =
          val id = getChatId(msg)
          val text = getString(msg)
          val meme = Memes.generateMatching(text)

          // NOTE: CnnMeme matches everything
          // therefore None variant is unreachable
          meme.get.toImage match
            case Some(p) => sendBufferedImage(p, id)
            case _ => writeMessage("failed to send image", id)

        end memeReply

        /** helpMenu sends a message containing all the commands the bot responds to and a description of the commands */

        def helpMenu(msg: Message) =
          val id = getChatId(msg)
          "This bot creates memes using the messages you send it! \n\n You can use the following commands to interact with me: \n\n /memelist - Gives you a list of the memes in the database, and their keywords"

        /** memeList sends images of the available memes and a description of how to use them */
        def memeList(msg: Message) =
          val id = getChatId(msg)
          val memes = Memes.memes

          writeMessage("The available memes are below", id)

          for m <- memes do
            m.getBaseImg.toImage match
              case Some(p) => sendBufferedImage(p, id, Some(m.description))
              case _ => ()

          // FIXME: Maybe make replying with a String optional for command handling
          ":)"

        this.onUserCommand("help", helpMenu)
        this.onUserCommand("start", helpMenu)
        this.onUserCommand("memelist", memeList)
        this.onUserMessage(memeReply)

        this.run()
        // Tarkistetaan, että lähti käyntiin
        println("Started the bot")
        while true do Thread.sleep(10000)

    end Bot

    // Tämä rivi pyytää ja ajaa täten yllä olevan botin
    val bot = Bot
end YourBot
