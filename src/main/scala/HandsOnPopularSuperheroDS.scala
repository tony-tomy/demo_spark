
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StructType, StringType, IntegerType}
import org.apache.log4j._

object HandsOnPopularSuperheroDS {

  case class SuperHeroNames(id: Int, name: String)
  case class SuperHero(value: String)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("HandsOnPopularSuperheroDS")
      .master("local[*]")
      .getOrCreate()

    val superHeroNameSchema = new StructType()
      .add("id", IntegerType, nullable = true)
      .add("name",StringType, nullable = true)

    import spark.implicits._
    val names = spark.read
      .schema(superHeroNameSchema)
      .option("sep"," ")
      .csv("data/Marvel-names.txt")
      .as[SuperHeroNames]

    val lines = spark.read
      .text("data/Marvel-graph.txt")
      .as[SuperHero]

    val connections = lines
      .withColumn("id", split(col("value"),pattern = " ")(0))
      .withColumn("connections", size(split(col("value"),pattern = " ")) -1)
      .groupBy("id").agg(sum("connections").alias("connections"))

    val mostPopular = connections
      .sort($"connections".desc)
      .first()

    val mostPopularName = names
      .filter($"id" === mostPopular(0))
      .select("name")
      .first()

    println(s"${mostPopularName(0)} is the most popular super hero with ${mostPopular(1)} co-appearances.")
  }


}
