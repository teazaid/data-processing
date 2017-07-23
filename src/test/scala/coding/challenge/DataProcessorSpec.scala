package coding.challenge

import java.time.Duration

import coding.models.{DataProcessorConfig, MeetingPrecisionConfig, UserUuidsToFind}
import org.scalatest.FunSuite

/**
  * Created by Alexander on 23.07.2017.
  */
class DataProcessorSpec extends FunSuite {
  private val dataProcessorConfig = DataProcessorConfig(UserUuidsToFind("600dfbe2", "54ead428"),
    MeetingPrecisionConfig(1.0, Duration.ofSeconds(30)))

  implicit class PrepareToBeParsed(rawData: String) {
    def makeParsable(): Array[String] = rawData.stripMargin.replaceAll("\r", "").split("\n")
  }

  private val UnParsedData =
    """timestamp,x,y,floor,uid
      |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,600dfbe2
      |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,54ead428
      |2014-07-19T16:00:06.076Z,103.78499,71.45633073293511,1,74d917a1
      |2014-07-19T16:00:06.080Z,103.720085,71.37973540637665,2,d05c03a0
      |2014-07-19T16:00:05.821Z,104.722595,90.43501545526087,3,22533c61
    """

  test("testProcessData should find meeting points pairs correctly") {

    val (parsedBuildingDataForSpecificUsers, meetingPoints) = DataProcessor.processData(
      UnParsedData.makeParsable().iterator)(dataProcessorConfig, RawDataParser.parseRow)
    assert(meetingPoints.size == 1)
    assert(parsedBuildingDataForSpecificUsers.size == 1)

    val user600dfbe2 = RawDataParser.parseRow("2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,600dfbe2")
    val user54ead428 = RawDataParser.parseRow("2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,54ead428")

    val expectedResult = Map(1 -> Map("600dfbe2" -> List(user600dfbe2.get), "54ead428" -> List(user54ead428.get)))
    assert(parsedBuildingDataForSpecificUsers == expectedResult)
  }

  test("testProcessData shouldn't populate parsed building") {
    val unparsedData =
      """timestamp,x,y,floor,uid
        |2014-07-19T16:00:06.076Z,103.78499,71.45633073293511,1,74d917a1
        |2014-07-19T16:00:06.080Z,103.720085,71.37973540637665,2,d05c03a0
        |2014-07-19T16:00:05.821Z,104.722595,90.43501545526087,3,22533c61
      """.stripMargin

    val (parsedBuildingDataForSpecificUsers, meetingPoints) = DataProcessor.processData(
      unparsedData.makeParsable().iterator)(dataProcessorConfig, RawDataParser.parseRow)
    assert(meetingPoints.size == 0)
    assert(parsedBuildingDataForSpecificUsers.size == 0)
  }

  test("testProcessData persons cant meet once they are on the different floors") {
    val unparsedData =
      """timestamp,x,y,floor,uid
        |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,600dfbe2
        |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,2,54ead428
      """.stripMargin

    val (parsedBuildingDataForSpecificUsers, meetingPoints) = DataProcessor.processData(
      unparsedData.makeParsable().iterator)(dataProcessorConfig, RawDataParser.parseRow)
    assert(meetingPoints.size == 0)
    assert(parsedBuildingDataForSpecificUsers.size == 2)
  }

  test("testProcessData persons cant meet if distance it too far") {
    val unparsedData =
      """timestamp,x,y,floor,uid
        |2014-07-19T16:00:06.071Z,103.79211,72.60419417988532,1,600dfbe2
        |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,54ead428
      """.stripMargin

    runFailedCase(unparsedData)
  }

  test("testProcessData doesnt process persons that were't parsed") {
    val unparsedData =
      """timestamp,x,y,floor,uid
        |2014-07-1916:00:06.071Z,103.79211,71.50419417988532,1,600dfbe2
        |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,2,54ead428
      """.stripMargin

    runFailedCase(unparsedData)
  }

  test("testProcessData persons cant meet if time difference in big") {
    val unparsedData =
      """timestamp,x,y,floor,uid
        |2014-07-19T16:01:06.071Z,103.79211,71.60419417988532,1,600dfbe2
        |2014-07-19T16:00:06.071Z,103.79211,71.50419417988532,1,54ead428
      """.stripMargin

    runFailedCase(unparsedData)
  }

  private def runFailedCase(unparsedData: String): Unit = {
    val (parsedBuildingDataForSpecificUsers, meetingPoints) = DataProcessor.processData(
      unparsedData.makeParsable().iterator)(dataProcessorConfig, RawDataParser.parseRow)
    assert(meetingPoints.size == 0)
    assert(parsedBuildingDataForSpecificUsers.size == 1)
  }
}
