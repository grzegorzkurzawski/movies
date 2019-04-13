package pl.kurzawski.management.util

import pl.kurzawski.management.model.{PostMovie, PostReview}

object DataContext {
  val postMovie = PostMovie("title", "Hopkins", List("Pitt", "Jolie"))
  val postReview = PostReview(1)
  val postReview2 = PostReview(5)
  val postReview3 = PostReview(3)
  val id = 123
}
