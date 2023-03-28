// @GENERATOR:play-routes-compiler
// @SOURCE:D:/IT2022-23/Profile/ITteam/template/ITSD-DT2022-Template/ITSD-DT2022-Template/conf/routes
// @DATE:Tue Feb 07 01:43:13 GMT 2023

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseGameScreenController GameScreenController = new controllers.ReverseGameScreenController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseGameScreenController GameScreenController = new controllers.javascript.ReverseGameScreenController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
  }

}
