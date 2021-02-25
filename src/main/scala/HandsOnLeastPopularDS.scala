
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StructType, StringType, IntegerType}
import org.apache.log4j._

object HandsOnLeastPopularDS {

  case class SuperHeroNames(id: Int, name: String)
  case class SuperHero(value: String)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("HandsOnLeastPopularDS")
      .master("local[*]")
      .getOrCreate()

    val superHeroNamesSchema = new StructType()
      .add("id", IntegerType, nullable = true)
      .add("name", StringType, nullable = true)

    import spark.implicits._
    val names = spark.read
      .schema(superHeroNamesSchema)
      .option("sep"," ")
      .csv("data/Marvel-names.txt")
      .as[SuperHeroNames]

    val lines = spark.read
      .text("data/Marvel-graph.txt")
      .as[SuperHero]

    val connections = lines
      .withColumn("id", split(col("value"), pattern = " ")(0))
      .withColumn("connections", size(split(col("value"), pattern = " ")) - 1)
      .groupBy("id").agg(sum(col("connections")).as("connections"))

    val minConnectionCount = connections.agg(min("connections")).first().getLong(0)
    
    val minConnections = connections.filter($"connections" === minConnectionCount)
    
    val minConnectionsWithNames = minConnections.join(names, usingColumn = "id")

    println(" The following connections have only " + minConnectionCount + " connection(s):")

    minConnectionsWithNames.select("name").show()

  }

}
