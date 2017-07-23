import coding.challenge._

/**
  * Created by Alexander on 22.07.2017.
  */
object Runner {
  def main(args: Array[String]) {
    val configurationManager = new ConfigurationManager("application.conf")

    val meetingPointsTry = FileReader.readFile(
      DataProcessor.processData(_)(configurationManager.dataProcessorConfig(), RawDataParser.parseRow)
    )(configurationManager.fileReaderConfig())

    meetingPointsTry.map { meetingPoints =>
      meetingPoints.map { case (personFirst, personSecond) =>
        println(s"${personFirst} met ${personSecond}")
      }
    }
  }
}
