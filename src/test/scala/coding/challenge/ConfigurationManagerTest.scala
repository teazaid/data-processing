package coding.challenge

import java.time.Duration

import coding.models.{DataProcessorConfig, FileReaderConfig, MeetingPrecisionConfig, UserUuidsToFind}
import org.scalatest.FunSuite

/**
  * Created by Alexander on 23.07.2017.
  */
class ConfigurationManagerTest extends FunSuite {
  import ConfigurationManager._
  private val CorrectTestConfigFile = "application-test.conf"
  private val TestConfigWithNoValues = "application-test-empty.conf"
  private val TestConfigWithEmptyDataProcessorConfig = "application-test-empty-data-processor.conf"

  test("testFileReaderConfig returns DefaultFileReaderConfig if no config found") {
    assert(new ConfigurationManager("").fileReaderConfig() == DefaultFileReaderConfig)
  }

  test("testDataProcessorConfig return DefaultDataProcessorConfig if no config found") {
    assert(new ConfigurationManager("").dataProcessorConfig() == DefaultDataProcessorConfig)
  }

  test("testFileReaderConfig returns correct config") {
    assert(new ConfigurationManager(CorrectTestConfigFile).fileReaderConfig() == FileReaderConfig("empty"))
  }

  test("testDataProcessorConfig returns correct config") {
    assert(new ConfigurationManager(CorrectTestConfigFile).dataProcessorConfig() == DataProcessorConfig(
      UserUuidsToFind("uuid1", "uuid2"), MeetingPrecisionConfig(5.0, Duration.ofSeconds(2))))
  }

  test("testFileReaderConfig returns config with default values") {
    assert(new ConfigurationManager(TestConfigWithNoValues).fileReaderConfig() == FileReaderConfig(DefaultPathName))
  }

  test("testDataProcessorConfig returns config with default values") {
    assert(new ConfigurationManager(TestConfigWithNoValues).dataProcessorConfig() == DataProcessorConfig(
      UserUuidsToFind(DefaultUserUuid, DefaultUserUuid), MeetingPrecisionConfig(DefaultMaxLength, DefaultMaxTime)))
  }

  test("testDataProcessorConfig returns config with default values if no inner configs") {
    assert(new ConfigurationManager(TestConfigWithEmptyDataProcessorConfig).dataProcessorConfig() == DataProcessorConfig(
      UserUuidsToFind(DefaultUserUuid, DefaultUserUuid), MeetingPrecisionConfig(DefaultMaxLength, DefaultMaxTime)))
  }
}
