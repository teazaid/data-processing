package coding.challenge

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import coding.models.{PersonLocation, RawUserData}

import scala.util.Try

/**
  * Created by Alexander on 22.07.2017.
  */
object RawDataParser {
  private val Delimiter = ","
  private[challenge] val TimeStampPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  private[challenge] val Formatter = DateTimeFormatter.ofPattern(TimeStampPattern)

  def parseRow(rawData: RawUserData): Try[PersonLocation] = {
    val splitRawData = rawData.split(Delimiter)

    for {
      time <- Try(LocalDateTime.parse(splitRawData(0), Formatter))
      x <- Try(splitRawData(1).toDouble)
      y <- Try(splitRawData(2).toDouble)
      floor <- Try(splitRawData(3).toInt)
      uuid <- Try(splitRawData(4))
    } yield PersonLocation(time, x, y, floor, uuid)
  }
}
