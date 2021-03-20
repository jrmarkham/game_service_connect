package plugin.markhamenterprises.game_service_connect

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener


private const val CHANNEL_NAME = "plugin.markhamenterprises/game_service_connect"
private const val RC_SIGN_IN = 9000

private object Methods {
  const val getSignIn = "getSignIn"
  const val showLeaderboard = "showLeaderboard"
  const val submitScore = "submitScore"
  const val showAchievements = "showAchievements"
  const val unlockAchievement =  "unlockAchievement"
  const val setPercentAchievement = "setPercentAchievement"
}

/** GameServiceConnectPlugin */
class GameServiceConnectPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private var googleSignInClient: GoogleSignInClient? = null
  private var achievementClient: AchievementsClient? = null
  private var leaderboardsClient: LeaderboardsClient? = null
  private var playerID: String? = null
  private var displayName: String? = null
  private var activityPluginBinding: ActivityPluginBinding? = null
  private var channel: MethodChannel? = null
  private var pendingOperation: PendingOperation? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {

      Methods.submitScore -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        val leaderboardID = call.argument<String>("leaderboardID") ?: ""
//        val score = call.argument<Int>("value") ?: 0
//        submitScore(leaderboardID, score, result)
      }

      Methods.showLeaderboard -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        showLeaderboards(result)
      }
      Methods.showAchievements -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        showAchievements(result)
      }

      Methods.setPercentAchievement -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        val achievementId = call.argument<String>("id") ?: ""
//        val percent = call.argument<Int>("percent") ?: 0
//        increment(achievementId, percent, result)
      }

      Methods.unlockAchievement -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        unlock(call.argument<String>("id") ?: "", result)
      }


      Methods.getSignIn -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")

      //      silentSignIn(result)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}






//class GameServiceConnectPlugin(private var activity: Activity? = null) : FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
//
//  private var googleSignInClient: GoogleSignInClient? = null
//  private var achievementClient: AchievementsClient? = null
//  private var leaderboardsClient: LeaderboardsClient? = null
//  private var playerID: String? = null
//  private var displayName: String? = null
//  private var activityPluginBinding: ActivityPluginBinding? = null
//  private var channel: MethodChannel? = null
//  private var pendingOperation: PendingOperation? = null
//
//  companion object {
//    @JvmStatic
//    fun registerWith(registrar: PluginRegistry.Registrar) {
//      val channel = MethodChannel(registrar.messenger(), CHANNEL_NAME)
//      val plugin = GamesServicesPlugin(registrar.activity())
//      channel.setMethodCallHandler(plugin)
//      registrar.addActivityResultListener(plugin)
//    }
//  }
//
//
//  private fun silentSignIn(result: Result) {
//    val activity = activity ?: return
//    val builder = GoogleSignInOptions.Builder(
//            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//    googleSignInClient = GoogleSignIn.getClient(activity, builder.build())
//    googleSignInClient?.silentSignIn()?.addOnCompleteListener { task ->
//      pendingOperation = PendingOperation(Methods.silentSignIn, result)
//      if (task.isSuccessful) {
//        val googleSignInAccount = task.result
//        handleSignInResult(googleSignInAccount!!, result)
//      } else {
//        Log.e("Error", "signInError", task.exception)
//        Log.i("ExplicitSignIn", "Trying explicit sign in")
//        explicitSignIn()
//      }
//    }
//  }
//
//  private fun explicitSignIn() {
//    val activity = activity ?: return
//    val builder = GoogleSignInOptions.Builder(
//            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail()
//    googleSignInClient = GoogleSignIn.getClient(activity, builder.build())
//    activity.startActivityForResult(googleSignInClient?.signInIntent, RC_SIGN_IN)
//  }
//
//  private fun handleSignInResult(googleSignInAccount: GoogleSignInAccount,
//                                 result: Result) {
//    achievementClient = Games.getAchievementsClient(activity!!, googleSignInAccount)
//    leaderboardsClient = Games.getLeaderboardsClient(activity!!, googleSignInAccount)
//    val playersClient = Games.getPlayersClient(activity!!, googleSignInAccount)
//    playersClient.currentPlayer?.addOnSuccessListener { innerTask->
//      playerID = innerTask.playerId
//      displayName = innerTask.displayName
//    }
//
//
//    val successMap: Map<String, String> = HashMap()
//      successMap.put("response", "success")
//      successMap.put("message", "player connect to game center")
//      successMap.put("id", playerID)
//      successMap.put("displayName", displayName)
//    result(successMap)
//
//    finishPendingOperationWithSuccess()
//  }
//
//  private fun showAchievements(result: Result) {
//    showLoginErrorIfNotLoggedIn(result)
//    achievementClient!!.achievementsIntent.addOnSuccessListener { intent ->
//      activity?.startActivityForResult(intent, 0)
//      result.success("success")
//    }.addOnFailureListener {
//      result.error("error", "${it.message}", null)
//    }
//  }
//
//  private fun unlock(achievementID: String, result: Result) {
//    showLoginErrorIfNotLoggedIn(result)
//    achievementClient?.unlockImmediate(achievementID)
//            ?.addOnSuccessListener {
//              result.success("success")
//            }?.addOnFailureListener {
//              result.error("error", it.localizedMessage, null)
//            }
//  }
//
//  private fun increment(achievementID: String, precent: Int, result: Result) {
//    showLoginErrorIfNotLoggedIn(result)
//    achievementClient?.incrementImmediate(achievementID, precent)
//            ?.addOnSuccessListener {
//              result.success("success")
//            }?.addOnFailureListener {
//              result.error("error", it.localizedMessage, null)
//            }
//  }
//
//  private fun showLeaderboards(result: Result) {
//    showLoginErrorIfNotLoggedIn(result)
//    leaderboardsClient!!.allLeaderboardsIntent.addOnSuccessListener { intent ->
//      activity?.startActivityForResult(intent, 0)
//      result.success("success")
//    }.addOnFailureListener {
//      result.error("error", "${it.message}", null)
//    }
//  }
//
//  private fun submitScore(leaderboardID: String, score: Int, result: Result) {
//    showLoginErrorIfNotLoggedIn(result)
//    leaderboardsClient?.submitScoreImmediate(leaderboardID, score.toLong())
//            ?.addOnSuccessListener {
//              result.success("success")
//            }?.addOnFailureListener {
//              result.error("error", it.localizedMessage, null)
//            }
//  }
//
//  private fun showLoginErrorIfNotLoggedIn(result: Result) {
//    if (achievementClient == null || leaderboardsClient == null) {
//      result.error("error", "Please make sure to call signIn() first", null)
//    }
//  }
//
//  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//    setupChannel(binding.binaryMessenger)
//  }
//
//  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//    teardownChannel()
//  }
//
//  private fun setupChannel(messenger: BinaryMessenger) {
//    channel = MethodChannel(messenger, CHANNEL_NAME)
//    channel?.setMethodCallHandler(this)
//  }
//
//  private fun teardownChannel() {
//    channel?.setMethodCallHandler(null)
//    channel = null
//  }
//
//  private fun disposeActivity() {
//    activityPluginBinding?.removeActivityResultListener(this)
//    activityPluginBinding = null
//  }
//
//  override fun onDetachedFromActivity() {
//    disposeActivity()
//  }
//
//  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
//    onAttachedToActivity(binding)
//  }
//
//  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
//    activityPluginBinding = binding
//    activity = binding.activity
//    binding.addActivityResultListener(this)
//  }
//
//  override fun onDetachedFromActivityForConfigChanges() {
//    onDetachedFromActivity()
//  }
//
//  private class PendingOperation constructor(val method: String, val result: Result)
//
//  private fun finishPendingOperationWithSuccess() {
//    Log.i(pendingOperation!!.method, "success")
//    pendingOperation!!.result.success("success")
//    pendingOperation = null
//  }
//
//  private fun finishPendingOperationWithError(errorMessage: String) {
//    Log.i(pendingOperation!!.method, "error")
//    pendingOperation!!.result.error("error", errorMessage, null)
//    pendingOperation = null
//  }
//
//  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
//    if (requestCode == RC_SIGN_IN) {
//      val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
//      val signInAccount = result.signInAccount
//      if (result.isSuccess && signInAccount != null) {
//        handleSignInResult(signInAccount)
//      } else {
//        var message = result.status.statusMessage ?: ""
//        if (message.isEmpty()) {
//          message = "Something went wrong " + result.status
//        }
//        finishPendingOperationWithError(message)
//      }
//      return true
//    }
//    return false
//  }
//
//
//  override fun onMethodCall(call: MethodCall, result: Result) {
//    when (call.method) {
//
//      Methods.submitScore -> {
//        val leaderboardID = call.argument<String>("leaderboardID") ?: ""
//        val score = call.argument<Int>("value") ?: 0
//        submitScore(leaderboardID, score, result)
//      }
//
//      Methods.showLeaderboard -> {
//        showLeaderboards(result)
//      }
//      Methods.showAchievements -> {
//        showAchievements(result)
//      }
//
//      Methods.setPercentAchievement -> {
//        val achievementId = call.argument<String>("id") ?: ""
//        val percent = call.argument<Int>("percent") ?: 0
//        increment(achievementId, percent, result)
//      }
//
//      Methods.unlockAchievement -> {
//        unlock(call.argument<String>("id") ?: "", result)
//      }
//
//
//      Methods.getSignIn -> {
//        silentSignIn(result)
//      }
//      else -> result.notImplemented()
//    }
//  }
//}
