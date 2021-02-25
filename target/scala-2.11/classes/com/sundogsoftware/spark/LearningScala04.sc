// Data structures

// Tuples
// Immutable Lists

val captainStuff = ("Picard", "Enterprise-D", "NCC-1701-D")
println(captainStuff)

// Refer to the individual fields with a one based index
println(captainStuff._1)
println(captainStuff._2)
println(captainStuff._3)

// Key Value Pair
val picardShip = "Picard" -> "Enterprise-D"
println(picardShip._2)

val aBunchOfStuff = ("Kirk", 1964, true)

// List
// Like a tuple, but more functionality
// Must of same type
// Zero based index

val shipList = List("Enterprise", "Defiant", "Voyage", "Deep Space Nine")
println(shipList(0))
println(shipList(1))
println(shipList.head)
println(shipList.tail)

for (ship <- shipList) {println(ship)}

val backwardsShips = shipList.map( (ship : String) => {ship.reverse})
for (ship <- backwardsShips) {println(ship)}

// reduce() to combine together all the items in a collection using some function
val numberList = List(1,2,3,4,5)
val sum = numberList.reduce( (x: Int, y: Int) => x + y)
println(sum)

// filter() remove stuff
val iHateFives = numberList.filter( (x: Int) => x != 5)
val iHateThrees = numberList.filter(_ !=3)

// Concatenate Lists
val moreNumbers = List(6,7,8)
val lotsOfNumbers = numberList ++ moreNumbers

val reversed = numberList.reverse
val sorted = reversed.sorted
val lotsOfDuplicates = numberList ++numberList
val distinctValues = lotsOfDuplicates.distinct
val maxValue = numberList.max
val total = numberList.sum
val hasThree = iHateThrees.contains(3)

// Map
val shipMap = Map("Kirk" -> "Enterprie", "Picard" -> "Enterprise-D", "Sisko" -> "Deep Space Nine", "Janeway" -> "Voyager")
println(shipMap("Janeway"))
println(shipMap("Archer"))
val archerShip = util.Try(shipMap("Archer")) getOrElse("Unknown")
println(archerShip)

//Exercise
//Create a list of the numbers 1-20, Divisible by 3.