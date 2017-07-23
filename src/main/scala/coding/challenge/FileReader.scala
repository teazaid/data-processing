package coding.challenge

import coding.models.FileReaderConfig

import scala.io.Source
import scala.util.Try

/**
  * Created by Alexander on 22.07.2017.
  */
object FileReader {
  def readFile[T](fileProcessor: Iterator[String] => T)(fileReaderConfig: FileReaderConfig): Try[T] = {
    val start = System.currentTimeMillis()
    val result = Try(Source.fromFile(fileReaderConfig.filePath)).map {data =>
      val lines = data.getLines()
      val processingResult = fileProcessor(lines)

      Try(data.close())
      processingResult
    }

    println(s"Files read and processed in: ${System.currentTimeMillis() - start} ms")
    result
  }
}
