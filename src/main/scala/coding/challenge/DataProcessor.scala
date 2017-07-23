package coding.challenge

import java.time.Duration

import coding.models._

import scala.util.Try

/**
  * Created by Alexander on 22.07.2017.
  */
object DataProcessor {
  private val EmptyBuilding: Building = Map.empty
  private val EmptyFloor: Floor = Map.empty
  private val EmptyMeetingPoints: MeetingPoints = List.empty

  def processData(unparsedData: Iterator[String])
                 (dataProcessorConfig: DataProcessorConfig,
                  parser: RawUserData => Try[PersonLocation]): (Building, MeetingPoints) = {
    val userUuids = Set(dataProcessorConfig.userUuidsToFind.userFirstUuid, dataProcessorConfig.userUuidsToFind.userSecondUuid)

    val startProcessingMs = System.currentTimeMillis()

    val result = unparsedData.collect {
      case rawPersonsLocationData if userUuids.exists(rawPersonsLocationData.endsWith) => parser(rawPersonsLocationData)
    }.foldLeft((EmptyBuilding, EmptyMeetingPoints)) { case ((building, meetingPoints), currentPersonTry) =>
      currentPersonTry.map { currentPerson =>
        process(userUuids, building, meetingPoints, currentPerson, dataProcessorConfig)
      }.getOrElse((building -> meetingPoints))
    }

    println(s"Processing time: ${System.currentTimeMillis() - startProcessingMs} ms")
    result
  }

  private def process(userUuids: Set[UserUuid],
                      building: Building,
                      meetingPoints: MeetingPoints,
                      currentPerson: PersonLocation,
                      dataProcessorConfig: DataProcessorConfig): (Building, MeetingPoints) = {
    building.get(currentPerson.floor).map { currentFloor =>
      processElementsOnTheFloor(ProcessingData(building, currentPerson, currentFloor),
        meetingPoints,
        userUuids,
        dataProcessorConfig.meetingPrecisionConfig)
    }.getOrElse {
      (addPersonOnTheEmptyFloor(building, currentPerson), meetingPoints)
    }
  }

  private def processElementsOnTheFloor(processingData: ProcessingData,
                                        meetingPoints: MeetingPoints,
                                        userUuids: Set[UserUuid],
                                        meetingPrecisionConfig: MeetingPrecisionConfig): (Building, MeetingPoints) = {
    val meetingInfo = userUuids.collectFirst {
      case anotherUserId if (anotherUserId != processingData.currentPerson.uuid) =>
        processingData.floor.get(anotherUserId).map { anotherPersonsLocations =>
          anotherPersonsLocations.collect {
            case another if isCloseEnough(another, processingData.currentPerson, meetingPrecisionConfig) =>
              processingData.currentPerson -> another
          }
        }
    }

    val newMeetingPoints = meetingInfo.flatten.getOrElse(List.empty)
    (addPersonOnTheFloor(processingData), meetingPoints ++ newMeetingPoints)
  }

  private def addPersonOnTheFloor(processingData: ProcessingData): Building = {
    val currentPersonLocations = processingData.floor.get(processingData.currentPerson.uuid).map { currentPersonLocations =>
      processingData.currentPerson :: currentPersonLocations
    }.getOrElse(List(processingData.currentPerson))

    val updatedFloor = processingData.floor + (processingData.currentPerson.uuid -> currentPersonLocations)

    processingData.building + (processingData.currentPerson.floor -> updatedFloor)
  }

  private def addPersonOnTheEmptyFloor(building: Building, currentPerson: PersonLocation): Building = {
    building + (currentPerson.floor -> Map(currentPerson.uuid -> List(currentPerson)))
  }

  private def isCloseEnough(personFirst: PersonLocation, personSecond: PersonLocation, meetingPrecisionConfig: MeetingPrecisionConfig): Boolean = {
    math.sqrt(math.pow(personFirst.x - personSecond.x, 2) + math.pow(personFirst.y - personSecond.y, 2)) <= meetingPrecisionConfig.maxLength &&
      math.abs(Duration.between(personFirst.time, personSecond.time).getSeconds) <= meetingPrecisionConfig.maxTime.getSeconds
  }
}
