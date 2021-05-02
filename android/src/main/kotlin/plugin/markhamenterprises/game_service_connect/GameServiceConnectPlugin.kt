package plugin.markhamenterprises.game_service_connect

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Gravity
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
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener

private const val CHANNEL_NAME = "plugin.markhamenterprises/game_service_connect"
private const val RC_SIGN_IN = 9000
private const val SUCCESS = "success";
private const val ERROR = "error";
private const val RESPONSE = "response";
private const val MESSAGE = "message";
private const val ID = "id";
private const val DISPLAY_NAME = "displayName";
private const val SCORE = "score";
private const val PERCENT = "percent";

private object Methods {
  const val getSignIn = "getSignIn"
  const val showLeaderboard = "showLeaderboard"
  const val submitScore = "submitScore"
  const val showAchievements = "showAchievements"
  const val unlockAchievement =  "unlockAchievement"
  const val setPercentAchievement = "setPercentAchievement"
}

class GameServiceConnectPlugin(private var activity: Activity? = null) : FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private var googleSignInClient: GoogleSignInClient? = null
  private var googleSignInAccount: GoogleSignInAccount? = null
  private var achievementClient: AchievementsClient? = null
  private var leaderboardsClient: LeaderboardsClient? = null
  private var activityPluginBinding: ActivityPluginBinding? = null
  private var channel: MethodChannel? = null
  private var pendingOperation: PendingOperation? = null

  companion object {
    @JvmStatic
    fun registerWith(registrar: PluginRegistry.Registrar) {
      val channel = MethodChannel(registrar.messenger(), CHANNEL_NAME)
      val plugin = GameServiceConnectPlugin(registrar.activity())
      channel.setMethodCallHandler(plugin)
      registrar.addActivityResultListener(plugin)
    }
  }

  private fun getSignIn(result: Result) {
    val activity = activity ?: return
    val builder = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
    googleSignInClient = GoogleSignIn.getClient(activity, builder.build())
    googleSignInClient?.silentSignIn()?.addOnCompleteListener { response ->
      pendingOperation = PendingOperation(Methods.getSignIn, result)
      if (response.isSuccessful) {
        googleSignInAccount = response.result
        handleSignInResult(googleSignInAccount!!)
      } else {
        Log.e(ERROR, "signInError", response.exception)
        Log.i("ExplicitSignIn", "Trying explicit sign in")
        explicitSignIn()
      }
    }
  }

  private fun explicitSignIn() {
    val activity = activity ?: return
    val builder = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestEmail()
    googleSignInClient = GoogleSignIn.getClient(activity, builder.build())
    activity.startActivityForResult(googleSignInClient?.signInIntent, RC_SIGN_IN)
  }

  private fun handleSignInResult(googleSignInAccount: GoogleSignInAccount) {
    val activity = this.activity!!
    achievementClient = Games.getAchievementsClient(activity, googleSignInAccount)
    leaderboardsClient = Games.getLeaderboardsClient(activity, googleSignInAccount)

    val gamesClient = Games.getGamesClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
    gamesClient.setViewForPopups(activity.findViewById(android.R.id.content))
    gamesClient.setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)

    val playersClient = Games.getPlayersClient(activity!!, googleSignInAccount)
    playersClient.currentPlayer?.addOnSuccessListener { currentPlayer ->
      val successMap = mapOf(RESPONSE to SUCCESS,
              MESSAGE to "player connect to game center",
              ID to currentPlayer.playerId, DISPLAY_NAME to currentPlayer.displayName)

      pendingOperation!!.result.success(successMap)
      pendingOperation = null

    }?.addOnFailureListener {
      pendingOperation!!.result.success( "error fetching player profile")
      pendingOperation = null
    }
  }

  // ACHIEVEMENT METHODS
  private fun showAchievements(result: Result) {
    showLoginErrorIfNotLoggedIn(result)
    achievementClient!!.achievementsIntent.addOnSuccessListener { intent ->
      activity?.startActivityForResult(intent, 0)
      result.success(SUCCESS)
    }.addOnFailureListener {
      Log.e(ERROR, "Could not show achievement", null)
      result.success("Chould not show achievement")
    }
  }

  private fun unlockAchievement(achievementID: String, result: Result) {
    showLoginErrorIfNotLoggedIn(result)

    if(googleSignInAccount != null) {
      Games.getAchievementsClient(activity!!, googleSignInAccount!!).unlockImmediate(achievementID)
      result.success(SUCCESS)
      return
    }
    Log.e(ERROR, "Could not unlock achievement", null)
    result.success("Could not unlock achievement")



//    achievementClient!!.unlockImmediate(achievementID).addOnSuccessListener{
//           result.success(SUCCESS)
//        }.addOnFailureListener {
//          Log.e(ERROR, "Could not unlock achievement", null)
//      result.success("Could not  achievement")
//        }
  }

  private fun setPercentAchievement(achievementID: String, percent: Int, result: Result) {
    showLoginErrorIfNotLoggedIn(result)


    /*
    Games.getAchievementsClient(activity, googleSignInAccount)
     */


    if(googleSignInAccount != null) {
      Games.getAchievementsClient(activity!!, googleSignInAccount!!).setStepsImmediate(achievementID, percent)
      result.success(SUCCESS)
      return
    }
    Log.e(ERROR, "Could not update achievement", null)
    result.success("Could not update achievement")



//    achievementClient!!.setStepsImmediate(achievementID, percent)
//    .addOnSuccessListener {
//      result.success(SUCCESS)
//    }.addOnFailureListener {
//      Log.e(ERROR, "Could not update achievement", null)
//      result.success("Could not update achievement")
//    }
  }

  private fun showLeaderboards(result: Result) {
    showLoginErrorIfNotLoggedIn(result)
    leaderboardsClient!!.allLeaderboardsIntent.addOnSuccessListener { intent ->
      activity?.startActivityForResult(intent, 0)
      result.success(SUCCESS)
    }.addOnFailureListener {
      Log.e(ERROR, "Could not show leader boarder", null)
      result.success("Could not show leader boarder")
    }
  }

  private fun submitScore(leaderboardID: String, score: Long, result: Result) {
    showLoginErrorIfNotLoggedIn(result)

    if(googleSignInAccount != null) {
      Games.getLeaderboardsClient(activity!!, googleSignInAccount!!).submitScore(leaderboardID, score)
      result.success(SUCCESS)
      return
    }
     Log.e(ERROR, "Could not submit score", null)
     result.success("Could not submit score")
  }

  private fun showLoginErrorIfNotLoggedIn(result: Result) {
    if (achievementClient == null || leaderboardsClient == null) {
      result.success("Please make sure to call signIn() first")
    }
  }

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    setupChannel(binding.binaryMessenger)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    teardownChannel()
  }

  private fun setupChannel(messenger: BinaryMessenger) {
    channel = MethodChannel(messenger, CHANNEL_NAME)
    channel?.setMethodCallHandler(this)
  }

  private fun teardownChannel() {
    channel?.setMethodCallHandler(null)
    channel = null
  }

  private fun disposeActivity() {
    activityPluginBinding?.removeActivityResultListener(this)
    activityPluginBinding = null
  }

  override fun onDetachedFromActivity() {
    disposeActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activityPluginBinding = binding
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  private class PendingOperation constructor(val method: String, val result: Result)

  private fun finishPendingOperationWithError(errorMessage: String) {
    Log.i(pendingOperation!!.method, ERROR)
    pendingOperation!!.result.error(ERROR, errorMessage, null)
    pendingOperation = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == RC_SIGN_IN) {
      val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
      val signInAccount = result?.signInAccount
      if (result?.isSuccess == true && signInAccount != null) {
        handleSignInResult(signInAccount)
      } else {
        var message = result?.status?.statusMessage ?: ""
        if (message.isEmpty()) {
          message = "Something went wrong " + result?.status
        }
        finishPendingOperationWithError(message)
      }
      return true
    }
    return false
  }
  //endregion

  //region MethodCallHandler
  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {

      Methods.getSignIn -> {
        getSignIn(result)
      }

      Methods.showLeaderboard -> {
        showLeaderboards(result)
      }

      Methods.submitScore -> {
        val leaderboardID = call.argument<String>(ID) ?: ""
        val score = call.argument<Int>(SCORE) ?: 0
        submitScore(leaderboardID, score.toLong(), result)
      }

      Methods.showAchievements -> {
        showAchievements(result)
      }

      Methods.unlockAchievement  -> {
        val achievementId = call.argument<String>(ID) ?: ""
        unlockAchievement(achievementId, result)
      }

      Methods.setPercentAchievement -> {
        val achievementId = call.argument<String>(ID) ?: ""
        val percent = call.argument<Int>(PERCENT) ?: 0
        setPercentAchievement(achievementId, percent, result)
      }

      else -> result.notImplemented()
    }
  }
}