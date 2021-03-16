
import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

/// ENUM for Sign In Results
enum SignInResultType {
  SUCCESS,
  ERROR,
  ERROR_NOT_SIGN_IN,
  ERROR_ANDROID
}
/// Convert String response to Enum
SignInResultType getSignType(String text)=> SignInResultType.values.firstWhere((SignInResultType type) => describeEnum(type)== text);

/// Account Object contains id and display name
/// todo :: add image access
class Account {
  String id;
  String displayName;
}

/// Sign In Object::
class SignInResult {
  SignInResultType type;
  Account account;
  String message;
  bool get success => type == SignInResultType.SUCCESS;
}


/// Plugin Class for Game Center And Google :::
/// signIn must be called first :::
class GameServicesConnect {
  static const MethodChannel _channel = const MethodChannel('plugin.markhamenterprises/game_service_connect');

  /// signIn call make this connection early in your code configuration
  static Future<SignInResult> get signIn async {
    final Map<dynamic, dynamic> _response = await _channel.invokeMethod('getSignIn');
    final SignInResultType type = getSignType(_response['response']);

    SignInResult result = new SignInResult()..type = type;
    result.message =  _response['message'];

    if(result.type == SignInResultType.SUCCESS) {
      result.account = Account ()
        ..id = _response['id']
        ..displayName = _response['displayName'];
      /// TO DO ADD IMAGE CONNECTION
    }
    return result;
  }

  /// LEADERBOARD
  /// show leaderboard must specify a particular leaderboard by id (String)
  static Future<bool> showLeaderboard(String id) async => await _channel
      .invokeMethod('showLeaderboard', {'id': id}) == "success";
  /// submit leaderboard score ::: id (String) && score (int)
  static Future<bool> submitScore({@required String id, @required int score}) async => await _channel.invokeMethod(
      'submitScore', {'id': id, 'score': score}) == "success";

  /// ACHIEVEMENTS
  /// show achievements
  static Future<bool> showAchievements() async => await _channel.invokeMethod('showAchievements') == "success";
  /// unlock an achievement ::: id (String)
  static Future<bool> unlockAchievement(String id) async => await _channel.invokeMethod(
      'unlockAchievement', {'id': id}) == "success";
  /// set percentage for an achievement ::: id (String) && percent (double) 0.01-100.00
  static Future<bool> setPercentAchievement({@required String id, @required double percent}) async {
    return await _channel.invokeMethod('setPercentAchievement', {'id': id, 'percent': percent}) == "success";
  }
}
