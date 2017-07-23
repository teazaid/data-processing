package coding

import java.time.{Duration, LocalDateTime}

/**
  * Created by Alexander on 22.07.2017.
  */
package object models {
  type RawUserData = String
  type UserUuid = String
  type FloorNumber = Int
  type Floor = Map[UserUuid, List[PersonLocation]]
  type Building = Map[FloorNumber, Floor]
  type MeetingPoints = List[(PersonLocation, PersonLocation)]

  case class PersonLocation(time: LocalDateTime, x: Double, y: Double, floor: FloorNumber, uuid: UserUuid)

  case class FileReaderConfig(filePath: String)

  case class DataProcessorConfig(userUuidsToFind: UserUuidsToFind, meetingPrecisionConfig: MeetingPrecisionConfig)

  case class MeetingPrecisionConfig(maxLength: Double, maxTime: Duration)

  case class UserUuidsToFind(userFirstUuid: UserUuid, userSecondUuid: UserUuid)

  case class ProcessingData(building: Building, currentPerson: PersonLocation, floor: Floor)

}
