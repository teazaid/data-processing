import coding.challenge._
import coding.models.MeetingPoints

/**
  * Created by Alexander on 22.07.2017.
  */
object Runner {
  def main(args: Array[String]) {
    val configurationManager = new ConfigurationManager("application.conf")

    val resultTry = FileReader.readFile(
      DataProcessor.processData(_)(configurationManager.dataProcessorConfig(), RawDataParser.parseRow)
    )(configurationManager.fileReaderConfig())

    resultTry.map { case (_, meetingPoints) =>
      printResultNicely(meetingPoints)
    }
  }

  private def printResultNicely(meetingPoints: MeetingPoints): Unit =
    meetingPoints.map { case (personFirst, personSecond) =>
      val floor = personFirst.floor

      val minX = math.min(personFirst.x, personSecond.x)
      val maxX = math.max(personFirst.x, personSecond.x)

      val minY = math.min(personFirst.y, personSecond.y)
      val maxY = math.max(personFirst.y, personSecond.y)

      val (timeMin, timeMax) = if(personFirst.time.compareTo(personSecond.time) < 0)
        (personFirst.time, personSecond.time) else (personSecond.time, personFirst.time)

      println(s"${personFirst.uuid} and ${personSecond.uuid} are met on the ${floor} floor from ${timeMin} to ${timeMax} at the place between [${minX}, ${minY}] and [${maxX}, ${maxY}]")
    }
}
