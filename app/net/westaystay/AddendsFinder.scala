package net.westaystay

import javax.inject._

case class Addends(indices: (Int, Int), numbers: (Int, Int))

@Singleton
class AddendsFinder {
  def findAddends(data: Array[Int], target: Int): Option[Addends] = {
    //return index of a second addend in the tail
    def findSecondIndex(tail: Vector[Int], first: Int, indexOffset: Int): Option[Int] = if (tail.isEmpty) {
      None
    } else {
      if (tail.head + first == target) Some(indexOffset) else findSecondIndex(tail.tail, first, indexOffset + 1)
    }

    def findPairOfIndices(tail: Vector[Int], indexOffset: Int): Option[(Int, Int)] = if (tail.isEmpty) {
      None  
    } else {
      findSecondIndex(tail.tail, tail.head, indexOffset + 1).fold(findPairOfIndices(tail.tail, indexOffset + 1))(i => Some(indexOffset -> i))
    }
    
    findPairOfIndices(data.toVector, 0).map(pair => Addends(pair, data(pair._1) -> data(pair._2)))
  }
}
