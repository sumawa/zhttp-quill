package com.sa

import zio.{ ExitCode, ZIO }
import zio.test._
import zio.test.environment._
import zio.test.Assertion._
import zio.test.TestAspect.{ ignore, timeout }

object ZHttpQuillSpec extends DefaultRunnableSpec {

  def spec = suite("ZHttpQuillSpec")()
}
