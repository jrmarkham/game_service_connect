import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:game_service_connect/game_service_connect.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  SignInResult _results;

  @override
  void initState() {
    signIn();
    super.initState();
  }

  Future<void> signIn() async {
    SignInResult results;
    try {
      results = await GameServicesConnect.signIn;
    } on PlatformException {
      results = null;
    }
    if (!mounted) return;

    setState(() {
      _results = results ?? 'error';
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Game Services Connect'),
        ),
        body: Center(
          child: Text(
              'SIGNING ON: ${_results != null ? _results.success : 'not connected'}'),
        ),
      ),
    );
  }
}
