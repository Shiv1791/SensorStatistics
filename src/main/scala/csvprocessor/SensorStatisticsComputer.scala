package csvprocessor

import java.io.File
import scala.annotation.tailrec
import scala.io.Source

object SensorStatisticsComputer extends App {

  def readSensorCSV(fileName: File): Seq[Sensor] = {
    for {
      line <- Source.fromFile(fileName).getLines().drop(1).toVector
      values = line.split(",").map(_.trim)
    } yield Sensor(values(0), values(1))
  }

  def getListOfFiles(dir: File): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      List("csv").exists(file.getName.endsWith(_))
    }
  }

  def processSensors(path: File): List[Sensor] = {
    getListOfFiles(path).flatMap(readSensorCSV)
  }

  def processFiles(path: File): Int = {
    getListOfFiles(path).size
  }


  def measurements(leaders: List[Sensor]): (Int, Int) = {
    @tailrec
    def helper(list: List[Sensor], failed: Int, total: Int): (Int, Int) = {
      list match {
        case Nil => (failed, total)
        case x::tail if  x.humidity == "NaN" => helper(tail, failed+1, total+1)
        case _::tail => helper(tail, failed, total+1)
      }
    }
    helper(leaders, 0, 0)
  }

  def processHumidity(sensors: List[Sensor]): List[(String, Any, Any, Any)] = {
    sensors.groupBy(_.sensorId).map{ x=>  (x._1, x._2.map(_.humidity))} map {
      sensorMap =>
        if (sensorMap._2.exists(_ != "NaN")) {
          val humidity = sensorMap._2.filter(_ != "NaN").map(_.toInt)
          (sensorMap._1, humidity.min, humidity.sum / humidity.size, humidity.max)
        } else {
          (sensorMap._1, "NaN", "NaN", "NaN")
        }
    }
  }.toList


  println(s"Num of files processed:  ${processFiles(new File("C:/Users/shivr/IdeaProjects/sensor-statistics/src/main/resources/"))}")
  println(s"Num of processed measurements:  ${measurements(processSensors(new File("C:/Users/shivr/IdeaProjects/sensor-statistics/src/main/resources/")))._2}")
  println(s"Num of failed measurements:  ${measurements(processSensors(new File("C:/Users/shivr/IdeaProjects/sensor-statistics/src/main/resources/")))._1}")
  println("Sensors with highest avg humidity:" + "\n"+ "sensorId, min, avg, max")
  processHumidity(processSensors(new File("C:/Users/shivr/IdeaProjects/sensor-statistics/src/main/resources/"))).foreach(x=> println(s"   $x"))

}