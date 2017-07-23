package coding.challenge

import java.time.LocalDateTime

import org.scalatest.FunSuite

/**
  * Created by Alexander on 23.07.2017.
  */
class RawDataParserSpec extends FunSuite {
  test("parse correct raw data") {
    val parsedPersonLocationTry = RawDataParser.parseRow("2014-07-19T17:46:26.718Z,109.10516,75.48685374678671,2,5e7b40e1")
    assert(parsedPersonLocationTry.isSuccess)
    val parsedPersonLocation = parsedPersonLocationTry.get

    assert(parsedPersonLocation.time == LocalDateTime.parse("2014-07-19T17:46:26.718Z", RawDataParser.Formatter))
    assert(parsedPersonLocation.x == 109.10516)
    assert(parsedPersonLocation.y == 75.48685374678671)
    assert(parsedPersonLocation.floor == 2)
    assert(parsedPersonLocation.uuid == "5e7b40e1")
  }

  test("parse bad formatted data") {
    assert(RawDataParser.parseRow("2014-07-19T17:46:26.71,109.10516,75.48685374678671,2,5e7b40e1").isFailure)
    assert(RawDataParser.parseRow("2014-07-19T17:46:26.718Z,109.A10516,75.48685374678671,2,5e7b40e1").isFailure)
    assert(RawDataParser.parseRow("2014-07-19T17:46:26.718Z,109.10516,75.48SA685374678671,2,5e7b40e1").isFailure)
    assert(RawDataParser.parseRow("2014-07-19T17:46:26.718Z,109.10516,75.48685374678671,SAD,5e7b40e1").isFailure)
    assert(RawDataParser.parseRow("2014-07-19T17:46:26.718Z,109.10516,75.48685374678671,2").isFailure)
  }
}
