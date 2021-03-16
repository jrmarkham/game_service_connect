import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:game_service_connect/game_service_connect.dart';

void main() {
  const MethodChannel channel = MethodChannel('plugin.markhamenterprises.com/game_service_connect');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getSignIn', () async {
    expect(await GameServicesConnect.signIn, '42');
  });
}
