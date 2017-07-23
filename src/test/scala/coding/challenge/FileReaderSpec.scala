package coding.challenge

import coding.models.FileReaderConfig
import org.scalatest.FunSuite

import scala.util.Success

/**
  * Created by Alexander on 23.07.2017.
  */
class FileReaderSpec extends FunSuite {
  private val processingResult = 5
  private val emptyProcessor : Iterator[String] => Int = iter => processingResult
  test("testReadFile should read a file") {
    val fileReaderConfig = FileReaderConfig("src/test/resources/emptyFile.txt")
    assert(FileReader.readFile(emptyProcessor)(fileReaderConfig) == Success(processingResult))
  }

  test("testReadFile should fail to ") {
    val fileReaderConfig = FileReaderConfig("src/test/resources/reduced.csv")
    assert(FileReader.readFile(emptyProcessor)(fileReaderConfig).isFailure)
  }

}