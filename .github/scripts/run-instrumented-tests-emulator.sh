#!/usr/bin/env bash
set -euo pipefail

wait_for_android_ready() {
  adb wait-for-device

  echo "Waiting for boot completion props..."
  until adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r" | grep -q "^1$"; do sleep 3; done
  until adb shell getprop dev.bootcomplete 2>/dev/null | tr -d "\r" | grep -q "^1$"; do sleep 3; done
  until adb shell getprop init.svc.bootanim 2>/dev/null | tr -d "\r" | grep -q "^stopped$"; do sleep 3; done

  echo "Waiting for framework services..."
  until adb shell service check package 2>/dev/null | grep -q "Service package: found"; do sleep 3; done
  until adb shell service check activity 2>/dev/null | grep -q "Service activity: found"; do sleep 3; done

  echo "Verifying PackageManager command path..."
  until adb shell pm path android 2>/dev/null | grep -q "package:"; do sleep 3; done
}

dump_diagnostics() {
  echo "===== ADB devices ====="
  adb devices -l || true
  echo "===== Key props ====="
  adb shell getprop ro.build.version.release || true
  adb shell getprop ro.build.version.sdk || true
  adb shell getprop sys.boot_completed || true
  adb shell getprop dev.bootcomplete || true
  adb shell getprop init.svc.bootanim || true
  echo "===== Service checks ====="
  adb shell service check package || true
  adb shell service check activity || true
}

wait_for_android_ready

attempt=1
max_attempts=2
until [ "$attempt" -gt "$max_attempts" ]; do
  echo "Running connectedDebugAndroidTest (attempt ${attempt}/${max_attempts})"
  if ./gradlew connectedDebugAndroidTest --stacktrace; then
    exit 0
  fi

  echo "connectedDebugAndroidTest failed on attempt ${attempt}."
  dump_diagnostics

  if [ "$attempt" -eq "$max_attempts" ]; then
    echo "No retries left."
    exit 1
  fi

  echo "Resetting adb server before retry..."
  adb kill-server || true
  adb start-server || true
  wait_for_android_ready
  attempt=$((attempt + 1))
done

