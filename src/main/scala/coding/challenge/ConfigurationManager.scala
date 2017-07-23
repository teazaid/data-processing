package coding.challenge

import java.time.Duration

import coding.models.{DataProcessorConfig, FileReaderConfig, MeetingPrecisionConfig, UserUuidsToFind}
import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
  * Created by Alexander on 22.07.2017.
  */
class ConfigurationManager(configFile: String) {

  import ConfigurationManager._

  private val config = ConfigFactory.load(configFile)

  def fileReaderConfig(): FileReaderConfig = Try(config.getConfig(FileReaderConfigName)).map { readerConfig =>
    FileReaderConfig(Try(readerConfig.getString(FileReaderPathNameProperty)).getOrElse(DefaultPathName))
  }.getOrElse(DefaultFileReaderConfig)


  def dataProcessorConfig(): DataProcessorConfig = {
    Try(config.getConfig(DataProcessorConfigName)).map { dataProcessorConfig =>
      val userUuidsToFind = Try(dataProcessorConfig.getConfig(UserUuidsConfigName)).map { userUuidsConfig =>
        UserUuidsToFind(
          Try(userUuidsConfig.getString(UserFirstProperty)).getOrElse(DefaultUserUuid),
          Try(userUuidsConfig.getString(UserSecondProperty)).getOrElse(DefaultUserUuid)
        )
      }.getOrElse(UserUuidsToFind(DefaultUserUuid, DefaultUserUuid))

      val meetingPrecisionConfig = Try(dataProcessorConfig.getConfig(MeetingPrecisionConfigName)).map { meetingPrecisionConfig =>
        MeetingPrecisionConfig(
          Try(meetingPrecisionConfig.getDouble(MaxLengthProperty)).getOrElse(DefaultMaxLength),
          Try(meetingPrecisionConfig.getDuration(MaxTimeProperty)).getOrElse(DefaultMaxTime))
      }.getOrElse(MeetingPrecisionConfig(DefaultMaxLength, DefaultMaxTime))

      DataProcessorConfig(userUuidsToFind, meetingPrecisionConfig)
    }
  }.getOrElse(DefaultDataProcessorConfig)
}

object ConfigurationManager {
  private val FileReaderConfigName = "file.reader"
  private val FileReaderPathNameProperty = "filePath"
  private[challenge] val DefaultPathName = "src/main/resources/reduced.csv"

  private val DataProcessorConfigName = "data.processor"

  private val MeetingPrecisionConfigName = "meeting.precision"
  private val MaxLengthProperty = "maxLength"
  private val MaxTimeProperty = "maxTime"
  private[challenge] val DefaultMaxLength = 1.0
  private[challenge] val DefaultMaxTime = Duration.ofSeconds(1)

  private val UserUuidsConfigName = "user.uuids"
  private val UserFirstProperty = "user.first"
  private val UserSecondProperty = "user.second"
  private[challenge] val DefaultUserUuid = ""

  private[challenge] val DefaultFileReaderConfig = FileReaderConfig(DefaultPathName)
  private[challenge] val DefaultUserUuidsToFind = UserUuidsToFind(DefaultUserUuid, DefaultUserUuid)
  private[challenge] val DefaultMeetingPrecisionConfig = MeetingPrecisionConfig(DefaultMaxLength, DefaultMaxTime)

  private[challenge] val DefaultDataProcessorConfig = DataProcessorConfig(DefaultUserUuidsToFind, DefaultMeetingPrecisionConfig)
}