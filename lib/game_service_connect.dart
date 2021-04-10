import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

const String _CHANNEL_NAME = "plugin.markhamenterprises/game_service_connect";
const String _SUCCESS = "success";
const String _RESPONSE = "response";
const String _MESSAGE = "message";
const String _ID = "id";
const String _DISPLAY_NAME = "displayName";
const String _SCORE = "score";
const String _PERCENT = "percent";


class Methods {
  static const String getSignIn = "getSignIn";
  static const String showLeaderboard = "showLeaderboard";
  static const String submitScore = "submitScore";
  static const String showAchievements = "showAchievements";
  static const String unlockAchievement = "unlockAchievement";
  static const String setPercentAchievement = "setPercentAchievement";
}

/// Account Object contains id and display name
/// todo :: add image access
class Account {
  String id;
  String displayName;
}

/// Sign In Object::
class SignInResult {
  Account account;
  String message;
  bool success;
}

/// Plugin Class for Game Center And Google :::
/// signIn must be called first :::
class GameServicesConnect {
  static const MethodChannel _channel = const MethodChannel(_CHANNEL_NAME);

  /// signIn call make this connection early in your code configuration
  static Future<SignInResult> get signIn async {
    final Map<dynamic, dynamic> _response = await _channel.invokeMethod(Methods.getSignIn);
    final bool success = _response[_RESPONSE] == _SUCCESS;

    SignInResult result = new SignInResult()..success = success;
    result.message =  _response[_MESSAGE];

    if(result.success) {
      result.account = Account ()
        ..id = _response[_ID]
        ..displayName = _response[_DISPLAY_NAME];
      /// TO DO ADD IMAGE CONNECTION
    }
    return result;
  }

  /// LEADERBOARD
  /// show leaderboard must specify a particular leaderboard by id (String)
  static Future<bool> showLeaderboard(String id) async => await _channel
      .invokeMethod(Methods.showLeaderboard, {_ID: id}) == _SUCCESS;
  /// submit leaderboard score ::: id (String) && score (int)
  static Future<bool> submitScore({@required String id, @required int score}) async => await _channel.invokeMethod(Methods.submitScore, {_ID: id, _SCORE: score}) ==  _SUCCESS;

  /// ACHIEVEMENTS
  /// show achievements
  static Future<bool> showAchievements() async => await _channel.invokeMethod
    (Methods.showAchievements) == "success";
  /// unlock an achievement ::: id (String)
  static Future<bool> unlockAchievement(String id) async => await _channel.invokeMethod(
      Methods.unlockAchievement, {'id': id}) == "success";
  /// set percentage for an achievement ::: id (String) && percent (double) 0.01-100.00
  static Future<bool> setPercentAchievement({@required String id, @required double percent}) async {
    return await _channel.invokeMethod(Methods.setPercentAchievement, {_ID: id, _PERCENT: percent}) == _SUCCESS;
  }
}
