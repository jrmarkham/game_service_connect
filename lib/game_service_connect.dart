import 'dart:async';
import 'dart:io';

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
    final Map<dynamic, dynamic> _response =
        await _channel.invokeMethod(Methods.getSignIn);

    SignInResult result = new SignInResult()
      ..success = _response[_RESPONSE] == _SUCCESS;
    result.message = _response[_MESSAGE];

    if (result.success) {
      result.account = Account()
        ..id = _response[_ID]
        ..displayName = _response[_DISPLAY_NAME];

      /// TO DO ADD IMAGE CONNECTION
    }
    return result;
  }

  /// LEADERBOARD
  /// show leaderboard must specify a particular leaderboard by id (String)
  static Future<bool> showLeaderboard(String id)  async  {
    try {
      final String response = await _channel.invokeMethod(Methods.showLeaderboard, {_ID: id});
      if (response ==  _SUCCESS) return true;
      debugPrint ('showLeaderboard error ::::  ${response.toString()}');
    }catch (e) {
      debugPrint ('showLeaderboard error ::::  ${e.toString()}');
      return false;
    }
    return false;
  }

  /// submit leaderboard score ::: id (String) && score (int)
  static Future<bool> submitScore({@required String id, @required int score})
  async  {
    try {
      final String response = await _channel.invokeMethod(Methods.submitScore, {_ID: id, _SCORE: score});
      if (response ==  _SUCCESS) return true;
      debugPrint ('submitScore error ::::  ${response.toString()}');
    }catch (e) {
      debugPrint ('submitScore error ::::  ${e.toString()}');
      return false;
    }
    return false;
  }


  /// ACHIEVEMENTS
  /// show achievements
  static Future<bool> showAchievements() async {
    try {
      final String response = await _channel.invokeMethod(Methods.showAchievements);
      if (response ==  _SUCCESS) return true;
      debugPrint ('showAchievements error ::::  ${response.toString()}');
    }catch (e) {
      debugPrint ('showAchievements error ::::  ${e.toString()}');
      return false;
    }
    return false;
  }


  /// unlock an achievement ::: id (String)
  static Future<bool> unlockAchievement(String id) async {
    try {
      final String response = await _channel.invokeMethod(Methods.unlockAchievement, {_ID: id});
      if (response ==  _SUCCESS) return true;
      debugPrint ('unlockAchievement error ::::  ${response.toString()}');
    }catch (e) {
      debugPrint ('unlockAchievement error ::::  ${e.toString()}');
      return false;
    }
    return false;
  }


  /// set percentage for an achievement ::: id (String) && percent (double) 0.01-100.00
  static Future<bool> setPercentAchievement({@required String id, @required double percent}) async {
    try {
      final String response = await _channel.invokeMethod(Methods.setPercentAchievement,
          {_ID: id, _PERCENT: Platform.isAndroid ? percent.ceil().toInt() : percent});
      if (response ==  _SUCCESS) return true;
      debugPrint ('setPercentAchievement error ::::  ${response.toString()}');
    }catch (e) {
      debugPrint ('setPercentAchievement error ::::  ${e.toString()}');
      return false;
    }
    return false;
  }
}
