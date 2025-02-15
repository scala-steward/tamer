/*
 * Copyright (c) 2019-2025 LaserDisc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tamer
package db

import java.time.Instant

import doobie.generic.auto._
import doobie.implicits.legacy.instant._
import doobie.syntax.string._
import zio._

object DatabaseSimple extends ZIOAppDefault {
  import implicits._

  override final val run = DbSetup
    .tumbling(window =>
      sql"""SELECT id, name, description, modified_at FROM users WHERE modified_at > ${window.from} AND modified_at <= ${window.to}""".query[Row]
    )(recordFrom = (_, v) => Record(v.id, v), from = Instant.parse("2020-01-01T00:00:00.00Z"), tumblingStep = 5.days)
    .runWith(dbLayerFromEnvironment ++ KafkaConfig.fromEnvironment)
}
