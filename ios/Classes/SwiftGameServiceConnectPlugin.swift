import Flutter
import UIKit
import GameKit

private let CHANNEL_NAME = "plugin.markhamenterprises/game_service_connect"
private let RESPONSE = "response";
private let SUCCESS = "success";
private let ERROR = "error";
private let UNIMPLEMENTED = "unimplemented";
private let MESSAGE = "message";
private let ID = "id";
private let DISPLAY_NAME = "displayName";
private let SCORE = "score";
private let PERCENT = "percent";

struct Methods {
    static let getSignIn = "getSignIn"
    static let showLeaderboard = "showLeaderboard"
    static let submitScore = "submitScore"
    static let showAchievements = "showAchievements"
    static let unlockAchievement =  "unlockAchievement"
    static let setPercentAchievement = "setPercentAchievement"
  }


public class SwiftGameServiceConnectPlugin: NSObject, FlutterPlugin {
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
                let id = args [ID]!
                 showLeaderboard(id:id,result:result)
            case Methods.submitScore:
                let args = call.arguments as! [String:Any]
                 let id = args [ID] as! String
                 let score = args[SCORE] as! Int64
                 submitScore(id:id, score:score, result:result )
            case Methods.showAchievements:showAchievements(result:result)
            case Methods.unlockAchievement:
                let args = call.arguments as! [String:String]
                let id = args [ID]!
                 unlockAchievement(id:id, result:result)
            case Methods.setPercentAchievement:
                let args = call.arguments as! [String:Any]
                let id = args [ID] as! String
                let percent = args [PERCENT] as! Double
                setPercentAchievement(id:id, percent:percent, result:result)
            default:
                result(UNIMPLEMENTED)
        }
      }

    func signInUser(result: @escaping FlutterResult) {
         let player = GKLocalPlayer.local
        player.authenticateHandler = { vc, error in
          guard error == nil else {
          let error:[String: Any] = [RESPONSE : ERROR, MESSAGE : "NIL error"]
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

             let results:[String: Any] = [ RESPONSE : SUCCESS,
                MESSAGE: "player connect to game center",
                ID :playerID, DISPLAY_NAME:player.displayName]

            result(results)
          } else {
             let error:[String: Any] = [RESPONSE : ERROR,
                 MESSAGE :"player auth failed"]
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
        result(SUCCESS)
      }

      func submitScore(id: String, score: Int64, result:@escaping FlutterResult) {
        let reportedScore = GKScore(leaderboardIdentifier: id)
        reportedScore.value = score
        GKScore.report([reportedScore]) { (error) in
          guard error == nil else {
            result(error?.localizedDescription ?? "")
            return
          }
         result(SUCCESS)
        }
      }

      // ACHIEVEMENTS
      func showAchievements(result: @escaping FlutterResult) {
        let vc = GKGameCenterViewController()
        vc.gameCenterDelegate = self
        vc.viewState = .achievements
        viewController.present(vc, animated: true, completion: nil)
        result(SUCCESS)
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
          result(SUCCESS)
        }
      }

    // SET PERCENTAGE
      func setPercentAchievement(id: String, percent: Double, result: @escaping FlutterResult) {
        let achievement = GKAchievement(identifier: id)
        achievement.percentComplete = percent
        achievement.showsCompletionBanner = true
        GKAchievement.report([achievement]) { (error) in
          guard error == nil else {
            result(error?.localizedDescription ?? "")
            return
          }
          result(SUCCESS)
        }
      }



   public static func register(with registrar: FlutterPluginRegistrar) {
     let channel = FlutterMethodChannel(name: CHANNEL_NAME, binaryMessenger: registrar.messenger())
     let instance = SwiftGameServiceConnectPlugin()
       registrar.addMethodCallDelegate(instance, channel: channel)
     }
}

extension SwiftGameServiceConnectPlugin: GKGameCenterControllerDelegate {
  public func gameCenterViewControllerDidFinish(_ gameCenterViewController: GKGameCenterViewController) {
    viewController.dismiss(animated: true, completion: nil)
  }
}
