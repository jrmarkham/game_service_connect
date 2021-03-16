import Flutter
import UIKit
import GameKit


private let CHANNEL_NAME = "plugin.markhamenterprises/game_service_connect" as! [String:String]
private struct Methods {
  let getSignIn = "getSignIn"as! [String:String]
  let showLeaderboard = "showLeaderboard"as! [String:String]
  let submitScore = "submitScore"as! [String:String]
  let showAchievements = "showAchievements"as! [String:String]
  let unlockAchievement =  "unlockAchievement"as! [String:String]
  let setPercentAchievement = "setPercentAchievement"as! [String:String]
}

public class SwiftIOSGameCenterPlugin: NSObject, FlutterPlugin {
// view controller
  var viewController: UIViewController {
    return UIApplication.shared.keyWindow!.rootViewController!
  }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
            case Methods.getSignIn:
                signInUser(result:result)
            case Methods.showLeaderboard:
                let args = call.arguments as! [String:String]
                let id = args ["id"]!
                 showLeaderboard(id:id,result:result)
            case Methods.submitScore:
                let args = call.arguments as! [String:Any]
                 let id = args ["id"] as! String
                 let score = args["score"] as! Int64
                 submitScore(id:id, score:score, result:result )
            case Methods.showAchievements:showAchievements(result:result)
            case Methods.unlockAchievement:
                let args = call.arguments as! [String:String]
                let id = args ["id"]!
                 unlockAchievement(id:id, result:result)
            case Methods.setPercentAchievement:
                let args = call.arguments as! [String:Any]
                let id = args ["id"] as! String
                let percent = args ["percent"] as! Double
                setPercentAchievement(id:id, percent:percent, result:result)
            default:
                result("unimplemented")
        }
      }

    func signInUser(result: @escaping FlutterResult) {
         let player = GKLocalPlayer.local
        player.authenticateHandler = { vc, error in
          guard error == nil else {
          let error:[String: Any] = ["response" : "ERROR", "message":"NIL error"]
           result(error)
            return
          }
          if let vc = vc {
            self.viewController.present(vc, animated: true, completion: nil)
          } else if player.isAuthenticated {

            var playerID:String
            if #available(iOS 12.4, *){
                playerID = player.gamePlayerID
             }else{
                playerID = player.playerID
             }

             let results:[String: Any] = ["response" :"SUCCESS","message":"player connect to game center", "id":playerID,
             "displayName": player.displayName]

            result(results)
          } else {
             let error:[String: Any] = ["response" : "ERROR_NOT_SIGN_IN", "message":"player auth failed"]
            result(error)
          }
        }
      }

    func showLeaderboard(id: String, result: @escaping FlutterResult) {
        let vc = GKGameCenterViewController()
        vc.gameCenterDelegate = self
        vc.viewState = .leaderboards
        vc.leaderboardIdentifier = id
        viewController.present(vc, animated: true, completion: nil)
        result("success")
      }

      func submitScore(id: String, score: Int64, result:@escaping FlutterResult) {
        let reportedScore = GKScore(leaderboardIdentifier: id)
        reportedScore.value = score
        GKScore.report([reportedScore]) { (error) in
          guard error == nil else {
            result(error?.localizedDescription ?? "")
            return
          }
          result("success")
        }
      }

      // ACHIEVEMENTS
      func showAchievements(result: @escaping FlutterResult) {
        let vc = GKGameCenterViewController()
        vc.gameCenterDelegate = self
        vc.viewState = .achievements
        viewController.present(vc, animated: true, completion: nil)
        result("success")
      }

    // UNLOCK
    func unlockAchievement(id: String, result: @escaping FlutterResult) {
        let achievement = GKAchievement(identifier: id)
        achievement.percentComplete = 100.0
        achievement.showsCompletionBanner = true
        GKAchievement.report([achievement]) { (error) in
          guard error == nil else {
            result(error?.localizedDescription ?? "")
            return
          }
          result("success")
        }
      }

    // INCREMENT
      func setPercentAchievement(id: String, percent: Double,
      result: @escaping FlutterResult) {
        let achievement = GKAchievement(identifier: id)
        achievement.percentComplete = percent
        achievement.showsCompletionBanner = true
        GKAchievement.report([achievement]) { (error) in
          guard error == nil else {
            result(error?.localizedDescription ?? "")
            return
          }
          result("success")
        }
      }



   public static func register(with registrar: FlutterPluginRegistrar) {
       let channel = FlutterMethodChannel(name: "plugin.markhamenterprises.com/game_service_connect", binaryMessenger: registrar.messenger())
       let instance = SwiftIOSGameCenterPlugin()
       registrar.addMethodCallDelegate(instance, channel: channel)
     }
}

extension SwiftIOSGameCenterPlugin: GKGameCenterControllerDelegate {
  public func gameCenterViewControllerDidFinish(_ gameCenterViewController: GKGameCenterViewController) {
    viewController.dismiss(animated: true, completion: nil)
  }
}
