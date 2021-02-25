
import org.apache.spark.sql.functions._
import org.apache.log4j._
import org.apache.spark.sql.{SparkSession, Dataset}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructType}

object HandsOnMovieSimilarityDataSet {

  case class Movies(userID: Int, movieID: Int, rating: Int, timestamp: Long)
  case class MovieNames(movieID: Int, MovieTitle: String)
  case class MoviePairs(movie1: Int, movie2: Int, rating1: Int, rating2: Int)
  case class MoviePairSimilarity(movie1: Int, movie2: Int, score: Double, numPairs: Long)

  def computeCosineSimilarity(spark: SparkSession, data: Dataset[MoviePairs]): Dataset[MoviePairSimilarity] = {

    val pairScores = data
      .withColumn("xx",col("rating1") * col("rating1"))
      .withColumn("yy", col("rating2") * col("rating2"))
      .withColumn("xy",col("rating1") * col("rating2"))

    val calculateSimilarity = pairScores
      .groupBy("movie1","movie2")
      .agg(
        sum(col("xy")).as("numerator"),
        (sqrt(sum(col("xx"))) * sqrt(sum(col("yy")))).as("denominator"),
        count(col("xy")).as("numPairs")
      )

    // Calculate score and select only needed columns (movie1, movie2, score, numPairs)
    import spark.implicits._
    val result = calculateSimilarity
      .withColumn("score",
        when(col("denominator") =!= 0, col("numerator")/col("denominator"))
          .otherwise(null)
      ).select("movie1", "movie2", "score", "numPairs").as[MoviePairSimilarity]

    result
  }

  def getMovieName(movieNames: Dataset[MovieNames], movieID: Int): String = {
    val result = movieNames.filter(col("movieID") === movieID)
      .select("MovieTitle").collect()(0)
    result(0).toString

  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("HandsOnMovieSimilarity")
      .getOrCreate()

    val moviesNamesSchema = new StructType()
      .add("movieID", IntegerType, nullable = true)
      .add("MovieTitle", StringType, nullable = true)

    val moviesSchema = new StructType()
      .add("userID", IntegerType, nullable = true)
      .add("movieID", IntegerType, nullable = true)
      .add("rating", IntegerType, nullable = true)
      .add("timestamp", LongType, nullable = true)

    println("\n Movies data loading...")

    import spark.implicits._
    val movieNames = spark.read
      .option("sep", "|")
      .option("charset", "ISO-8859-1")
      .schema(moviesNamesSchema)
      .csv("data/ml-100k/u.item")
      .as[MovieNames]

    val movies = spark.read
      .option("sep", "\t")
      .schema(moviesSchema)
      .csv("data/ml-100k/u.data")
      .as[Movies]

    val ratings = movies.select("userID", "movieID", "rating")

    // Emit every movie rated together by the same user.
    // Self-join to find every combination.
    // Select movie pairs and rating pairs
    val moviePairs = ratings.as("ratings1")
      .join(ratings.as("ratings2"), $"ratings1.userID" === $"ratings2.userID" && $"ratings1.movieID" < $"ratings2.movieID")
      .select($"ratings1.movieID".as("movie1"),
        $"ratings2.movieID".as("movie2"),
        $"ratings1.rating".as("rating1"),
        $"ratings2.rating".as("rating2")
      ).as[MoviePairs]

    val moviePairSimilarities = computeCosineSimilarity(spark, moviePairs).cache()

    if (args.length > 0) {
      val scoreThreshold = 0.97
      val coOccurrenceThreshold = 50.0

      val movieID: Int = args(0).toInt

      // our quality thresholds above
      val filteredResults = moviePairSimilarities.filter(
        (col("movie1") === movieID || col("movie2") === movieID) &&
          col("score") > scoreThreshold && col("numPairs") > coOccurrenceThreshold)

      // Sort by quality score.
      val results = filteredResults.sort(col("score").desc).take(10)

      println("\nTop 10 similar movies for " + getMovieName(movieNames, movieID))
      for (result <- results) {
        // Display the similarity result that isn't the movie we're looking at
        var similarMovieID = result.movie1
        if (similarMovieID == movieID) {
          similarMovieID = result.movie2
        }
        println(getMovieName(movieNames, similarMovieID) + "\tscore: " + result.score + "\tstrength: " + result.numPairs)
      }

    }
  }
}
