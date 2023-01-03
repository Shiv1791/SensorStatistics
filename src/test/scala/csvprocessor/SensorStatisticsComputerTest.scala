package csvprocessor

import csvprocessor.SensorStatisticsComputer._
import org.scalatest.wordspec.AnyWordSpecLike

import java.io.File

class SensorStatisticsComputerTest extends AnyWordSpecLike   {

  val path = new File("src/test/resources/")
  val inputData = List(Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"),
    Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"),
    Sensor("s3","NaN"), Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"), Sensor("s1","10"), Sensor("s2","88"),
    Sensor("s1","NaN"))

  val expectedData = List(Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"), Sensor("s3","NaN"), Sensor("s1","10"),
    Sensor("s2","88"), Sensor("s1","NaN"), Sensor("s3","NaN"), Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"),
    Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"))

  "CSV load" in {
    val sensor = readSensorCSV(new File("src/test/resources/leader-1.csv"))
    assert(sensor.size === 4)
    assert(sensor === Vector(Sensor("s1","10"), Sensor("s2","88"), Sensor("s1","NaN"), Sensor("s3","NaN")))
  }

  "CSV files processed " in {
      val fileSize = processFiles(path)
    assert(fileSize === 4)
  }

  "CSV files" in {
    val files = getListOfFiles(path)
    assert(files === List(new File("src/test/resources/leader-1.csv"),
      new File("src/test/resources/leader-2.csv"), new File("src/test/resources/leader-3.csv"),
      new File("src/test/resources/leader-4.csv")) )
  }

  "process Sensors" in {
    assert(processSensors(path) === expectedData)
  }

  "process measurements" in {
    assert(measurements(inputData) === (5,13))
  }

  "process Humidity" in {
    assert(processHumidity(inputData) === List(("s3","NaN","NaN","NaN"), ("s2",88,88,88), ("s1",10,10,10)))
  }

}
