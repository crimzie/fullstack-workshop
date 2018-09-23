import java.io
import java.util.zip

import ammonite.ops
import mill._
import mill.define.{Target, Task}
import mill.scalajslib._
import mill.scalalib._
import mill.util.{Ctx, Loose}
import ujson.Js

import scala.collection.mutable

trait ScalaJSWebpackModule extends ScalaJSModule {
  def webpackVersion: Target[String] = "4.17.1"
  def webpackCliVersion: Target[String] = "3.1.0"
  def webpackDevServerVersion: Target[String] = "3.1.7"

  case class JsDeps(
      compileDependencies: List[(String, String)],
      compileDevDependencies: List[(String, String)],
      jsSources: Map[String, String]) {
    def ++(that: JsDeps): JsDeps =
      JsDeps(
        compileDependencies ++ that.compileDependencies,
        compileDevDependencies ++ that.compileDevDependencies,
        jsSources ++ that.jsSources)
  }

  object JsDeps {
    def apply(json: mutable.Map[String, Js.Value]): JsDeps = {
      def read(key: String): List[(String, String)] =
        json.get(key).fold(List.empty[(String, String)]) {
          _.arr.flatMap {
            _.obj.headOption.map { case (s, v) => s -> v.str }
          }.toList
        }

      JsDeps(
        read("compileDependencies") ++ read("compile-dependencies"),
        read("compileDevDependencies") ++ read("compile-devDependencies"),
        Map.empty)
    }
  }

  def sbtBundlerDeps: Task[JsDeps] = T.task {
    Lib.resolveDependencies(
      repositories,
      resolveCoursierDependency().apply,
      transitiveIvyDeps() ++ ivyDeps(),
      sources = false,
      Some(mapDependencies()))
      .asSuccess
      .get
      .value
      .flatMap { x =>
        @scala.annotation.tailrec
        def read(
            in: zip.ZipInputStream,
            buffer: Array[Byte] = new Array[Byte](8192),
            out: io.ByteArrayOutputStream =
            new io.ByteArrayOutputStream): io.ByteArrayOutputStream = {
          val byteCount = in.read(buffer)
          if (byteCount >= 0) {
            out.write(buffer, 0, byteCount)
            read(in, buffer, out)
          } else out
        }

        val stream: zip.ZipInputStream =
          new zip.ZipInputStream(
            new io.BufferedInputStream(new io.FileInputStream(x.path.toIO)))
        val deps: Seq[JsDeps] =
          Iterator.continually(stream.getNextEntry)
            .takeWhile { _ != null }
            .collect {
              case z if z.getName == "NPM_DEPENDENCIES" =>
                JsDeps(ujson.read(read(stream).toString).obj)
              case z if z.getName.endsWith(".js") &&
                !z.getName.startsWith("scala/") =>
                JsDeps(Nil, Nil, Map(z.getName -> read(stream).toString))
            }
            .to[Seq]
        stream.close()
        deps
      }
      .toSeq
      .foldLeft(JsDeps(Nil, Nil, Map.empty)) { _ ++ _ }
  }

  def writeBundleSources: Task[(JsDeps, ops.Path) => Unit] = T.task {
    (jsDeps: JsDeps, dst: ops.Path) =>
      jsDeps.jsSources foreach { case (n, s) => ops.write(dst / n, s) }
  }

  def writeWpConfig: Task[(ops.Path, String, String, String, Boolean) => Unit] =
    T.task {
      (dst: ops.Path, cfg: String, io: String, out: String, opt: Boolean) =>
        ops.write(
          dst / cfg,
          "module.exports = " + Js.Obj(
            "mode" -> (if (opt) "production" else "development"),
            "devtool" -> "source-map",
            "entry" -> io,
            "output" -> Js.Obj(
              "path" -> dst.toString,
              "filename" -> out)).render(2) + ";\n")
    }

  def writePackageSpec: Task[(JsDeps, ops.Path) => Unit] = T.task {
    (jsDeps: JsDeps, dst: ops.Path) =>
      val compileDeps = jsDeps.compileDependencies
      val compileDevDeps =
        jsDeps.compileDevDependencies ++ Seq(
          "webpack" -> webpackVersion(),
          "webpack-cli" -> webpackCliVersion(),
          "webpack-dev-server" -> webpackDevServerVersion(),
          "source-map-loader" -> "0.2.3")

      ops.write(
        dst / "package.json",
        Js.Obj(
          "dependencies" -> compileDeps,
          "devDependencies" -> compileDevDeps).render(2) + "\n")
  }

  def runWebpack: Task[(ops.Path, String) => Unit] = T.task {
    (dst: ops.Path, cfg: String) =>
      print(ops.%%("npm", "install")(dst).out.string)
      print(ops.%%(
        "node",
        dst / "node_modules" / "webpack" / "bin" / "webpack",
        "--bail",
        "--profile",
        "--config",
        cfg)(dst).out.string)
  }

  def webpack: Task[(ops.Path, ops.Path, Boolean) => Unit] = T.task {
    (src: ops.Path, dst: ops.Path, opt: Boolean) =>
      val outjs = dst / src.segments.last
      val deps = sbtBundlerDeps()
      val cfg = "webpack.config.js"
      ops.cp(src, outjs)
      writeBundleSources().apply(deps, dst)
      writeWpConfig().apply(dst, cfg, outjs.toString, "out-bundle.js", opt)
      writePackageSpec().apply(deps, dst)
      runWebpack().apply(dst, cfg)
      ops.rm(outjs)
  }

  def fastOptWp: Target[PathRef] = T {
    val dst = Ctx.taskCtx.dest
    webpack().apply(fastOpt().path, dst, false)
    PathRef(dst)
  }

  def fullOptWp: Target[PathRef] = T {
    val dst = Ctx.taskCtx.dest
    webpack().apply(fullOpt().path, dst, true)
    PathRef(dst)
  }
}

trait Module extends ScalaModule {
  override def scalacOptions: Target[Seq[String]] =
    Seq("-deprecation", "-feature")
  def scalaVersion = "2.12.6"
  val monixVersion = "3.0.0-RC1"
  val betterFilesVersion = "3.6.0"
  val endpointsVersion = "0.6.0"
  val circeVersion = "0.9.3"
  val ahcbmVersion = "1.3.0-RC5"
  val scalatagsVersion = "0.6.7"
  val outwatchVersion = "1.0.0-RC2"
}

trait Test extends TestModule {
  override def moduleDeps: Seq[JavaModule] =
    if (this == shared.test) super.moduleDeps
    else super.moduleDeps :+ shared.test
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    Agg(ivy"org.scalatest::scalatest::3.0.5")
  def testFrameworks: Target[Seq[String]] =
    Seq("org.scalatest.tools.Framework")
}

trait JsModule extends Module with ScalaJSModule {
  override def scalaJSVersion: Target[String] = "0.6.25"

  object test extends Test with Tests
}

trait JvmModule extends Module {
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.scala-js::scalajs-stubs:${shared.scalaJSVersion()}",
    )

  object test extends Test with Tests
}

object shared extends JsModule {
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-algebra:$endpointsVersion",
      ivy"org.julienrf::endpoints-algebra-circe:$endpointsVersion",
      ivy"org.julienrf::endpoints-openapi:$endpointsVersion",
      ivy"org.julienrf::endpoints-json-schema-generic:$endpointsVersion",
      ivy"org.julienrf::endpoints-json-schema-circe:$endpointsVersion",
      ivy"io.circe::circe-generic::$circeVersion",
    )
}

object back extends JvmModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def mainClass: Target[Some[String]] =
    Some("com.crimzie.workshop.Main")
  //override def runClasspath = T { super.runClasspath() ++ jsfront.fastOpt() }
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"io.monix::monix::$monixVersion",
      ivy"org.julienrf::endpoints-akka-http-server:$endpointsVersion",
      ivy"com.lihaoyi::scalatags:$scalatagsVersion",
      ivy"com.github.pathikrit::better-files:$betterFilesVersion",
    )
}

object jvmclient extends JvmModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def mainClass: Target[Some[String]] =
    Some("com.crimzie.workshop.Main")
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"io.monix::monix::$monixVersion",
      ivy"org.julienrf::endpoints-sttp-client:$endpointsVersion",
      ivy"org.julienrf::endpoints-scalaj-client:$endpointsVersion",
      //ivy"org.julienrf::endpoints-scalaj-client-circe:$endpointsVersion",
      ivy"com.softwaremill.sttp::async-http-client-backend-monix:$ahcbmVersion",
    )
}

object jsclient extends JsModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-xhr-client::$endpointsVersion",
      ivy"org.julienrf::endpoints-xhr-client-circe::$endpointsVersion",
    )
}

object jsfront extends JsModule with ScalaJSWebpackModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def moduleKind: Target[ModuleKind] =
    T { ModuleKind.CommonJSModule }
  override def mainClass: Target[Some[String]] =
    Some("com.crimzie.workshop.Main")
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-xhr-client::$endpointsVersion",
      ivy"org.julienrf::endpoints-xhr-client-circe::$endpointsVersion",
      ivy"io.github.outwatch::outwatch::$outwatchVersion",
    )
}
