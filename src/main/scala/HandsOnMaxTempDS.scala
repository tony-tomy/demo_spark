
import org.apache.spark.sql.functions._
import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SparkSession._
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructType}

object HandsOnMaxTempDS {

  case class Temperature(stationID: String, date: Int, measure_type: String, temperature: Float)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("HandsOnMaxTempDS")
      .getOrCreate()

    val temperatureSchema = new StructType()
      .add("stationID", StringType, nullable = true)
      .add("date", IntegerType, nullable = true)
      .add("measure_type", StringType, nullable = true)
      .add("temperature", FloatType, nullable = true)

    import spark.implicits._
    val ds = spark.read
      .schema(temperatureSchema)
      .csv("data/1800.csv")
      .as[Temperature]

    val maxTemp = ds.filter($"measure_type" === "TMAX")

    val stationTemps = maxTemp.select("stationID","temperature")

    val maxTempByStation = stationTemps.groupBy("stationID").max("temperature")

    val maxTempByStationF = maxTempByStation
      .withColumn("temperature", round($"max(temperature)" * 0.1f * (9.0f / 5.0f) + 32.0f, 2))
      .select("stationID", "temperature").sort("temperature")

    val results = maxTempByStationF.collect()

    for ( result <- results) {
      val station = result(0)
      val temp = result(1).asInstanceOf[Float]
      val formattedTemp = f"$temp%.2f F"
      println(s"$station maximum temperature is $formattedTemp")
    }

    spark.stop()
  }

}
