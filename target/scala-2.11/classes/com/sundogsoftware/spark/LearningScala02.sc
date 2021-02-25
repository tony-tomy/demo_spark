// Flow Control

// If / else:
if(1 > 3) println("Impossible!") else  println("The world makes sense")

if(1 > 3) {
  println("Impossible!")
  println("Really")
} else {
  println("The world makes sense")
  println("Still.")
}

// Matching
val number = 3
number match {
  case 1 => println("One")
  case 2 => println("Two")
  case 3 => println("Three")
  case _ => println("Something else")
}

// for statement
for (x <- 1 to 4){
  val squared = x * x
  println(squared)
}

// while loop
var x = 10
while (x >= 0){
  println(x)
  x -= 1
}

// do while loop
x = 0
do { println(x); x+=1} while (x <= 10)

// Expression
{val x = 10; x + 20}

println({val x = 10; x + 20})

//Exercise
//Write first 10 values of the Fibonacci Series

var a = 0
var b = 1
var c = 1
var count = 10
while (count > 0) {
  println(a)
  a = b
  b = b + c
  count -= 1
}