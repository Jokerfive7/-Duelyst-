// @GENERATOR:play-routes-compiler
// @SOURCE:D:/IT2022-23/Profile/ITteam/template/ITSD-DT2022-Template/ITSD-DT2022-Template/conf/routes
// @DATE:Tue Feb 07 01:43:13 GMT 2023


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
