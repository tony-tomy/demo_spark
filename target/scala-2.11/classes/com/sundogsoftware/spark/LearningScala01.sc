// Values are immutable constants
val hello: String = "Hola !"

// Variables are mutable
var helloThere: String = hello
helloThere = hello + "There!"
println(helloThere)

val immutableHelloThere = hello + " There"
println(immutableHelloThere)

// Data Types

val numberOne: Int = 1
val truth: Boolean = true
val letterA: Char = 'a'
val pi: Double = 3.1415
val piSinglePrecision: Float = 3.1415f
val bigNumber: Long = 123456789
val smallNumber: Byte = 127

println("Here is a mess: " + numberOne + truth + letterA + pi)

println(f"Pi is about $piSinglePrecision%.3f")
println(f"ero padding in the left $numberOne%05d")

println(s"I can use the s prefix to use variables like $piSinglePrecision $truth $letterA")

println(s"The s prefix is not limited to variables; I can include any expression like ${1+2}")

val theUltimateAnswer: String = "To life, the universe and everything is 42."
val pattern = """.* ([\d]+).*""".r
val pattern(answerString) = theUltimateAnswer
val answer = answerString.toInt
println(answer)

//Boolean
val isGreater = 1 > 2
val isLesser = 1 < 2
val impossible = isGreater & isLesser
val anotherWay = isGreater && isLesser

val picard: String = "Picard"
val bestCaptain: String = "Picard"
val isBest: Boolean = picard == bestCaptain

