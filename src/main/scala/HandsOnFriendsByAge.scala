
import org.apache.spark.sql._
import org.apache.log4j._

object HandsOnFriendsByAge {

  case class Person(id:Int, name:String, age:Int, friends:Int)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("HandsOnFriendsByAge")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val schemaPeople = spark.read
      .option("header", "true")
      .option("inferSchema","true")
      .csv("data/fakefriends.csv")
      .as[Person]

    schemaPeople.printSchema()

    schemaPeople.createOrReplaceTempView("people")

    val friendsByAge = spark.sql("SELECT age, AVG(friends) FROM people GROUP BY age")

    val results = friendsByAge.collect()

    results.foreach(println)

    spark.stop()



  }

}
