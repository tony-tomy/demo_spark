
import org.apache.spark._
import org.apache.log4j._
import scala.math.max


/** FInd the Maximum temperature by whether station */
object HandsOnMaxTemp {

  /** Function to parse the individual lines */
  def parseLines(line: String): (String, String, Float) = {
    val fields = line.split(",")
    val stationID = fields(0)
    val entryType = fields(2)
    val temperature = fields(3).toFloat * 0.1f * (9.0f / 5.0f) + 32.0f
    (stationID, entryType, temperature )
  }

  /** Main Function */
  def main(args: Array[String]): Unit = {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]","HandsOnMaxTemp")

    // Read each line of input data
    val lines = sc.textFile("data/1800.csv")

    // Convert to stationID, entryType, temperature tuples
    val parsedLines = lines.map(parseLines)

    // Filter out all but TMAX entries
    val maxTemps = parsedLines.filter( x => x._2 == "TMAX")

    // Convert to stationID, temperature
    val stationTemp = maxTemps.map( x => (x._1, x._3.toFloat))

    // Reduce by stationID retaining the max temperature
    val maxTempsByStations = stationTemp.reduceByKey( (x,y) => max(x,y))

    // Collect, format and print the result
    val results = maxTempsByStations.collect()

    for (result <- results.sorted){
      val station = result._1
      val temperature = result._2
      val formattedTemp = f"$temperature%.2f F"
      println(s"$station maximum temperature : $formattedTemp")
    }

  }
}
